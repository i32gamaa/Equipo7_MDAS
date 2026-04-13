package es.uco.pw.demo.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.uco.pw.demo.model.domain.Alquiler;
import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.repository.AlquilerRepository;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;

@RestController
@RequestMapping(path="/api/alquileres", produces="application/json")
public class AlquilerRestController {

    AlquilerRepository alquilerRepository;
    EmbarcacionRepository embarcacionRepository;
    
    public AlquilerRestController(AlquilerRepository alquilerRepository, EmbarcacionRepository embarcacionRepository){
        this.alquilerRepository = alquilerRepository;
        this.embarcacionRepository = embarcacionRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.alquilerRepository.setSQLQueriesFileName(sqlQueriesFileName);
        this.embarcacionRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping
    public ResponseEntity<List<Alquiler>> getAllAlquileres(){
        List<Alquiler> alquileres = alquilerRepository.findAllAlquileres();
        return new ResponseEntity<>(alquileres, HttpStatus.OK);
    }

    @GetMapping(params="fecha")
    public ResponseEntity<List<Alquiler>> getAlquileresFuturos( // REFACTORIZACIÓN (Regla 11): Nombre de método más descriptivo
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<Alquiler> alquileres = alquilerRepository.findByDate(fecha);
        return new ResponseEntity<>(alquileres, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alquiler> getAlquilerById(@PathVariable Integer id){
        Alquiler alquiler = alquilerRepository.findById(id);
        if(alquiler != null){
            return new ResponseEntity<>(alquiler, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/disponibles", params = {"start", "end"})
    public ResponseEntity<List<Embarcacion>> getAvailableBoats( // REFACTORIZACIÓN (Regla 1 y 15): startDate y endDate en lugar de start/end
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Embarcacion> disponibles = embarcacionRepository.findAvailableByDateRange(startDate, endDate);
        return new ResponseEntity<>(disponibles, HttpStatus.OK);
    }

    @PostMapping(consumes="application/json")
    public ResponseEntity<Alquiler> createAlquiler(@RequestBody Alquiler alquiler) { // REFACTORIZACIÓN (Regla 11): postAlquiler -> createAlquiler
        if(alquiler.getStartDate() == null || alquiler.getEndDate() == null || 
           alquiler.getRegistrationNumber() == null || alquiler.getUserId() == null){
            return new ResponseEntity<>(alquiler, HttpStatus.BAD_REQUEST);
        } 
        else if(alquiler.getStartDate().isAfter(alquiler.getEndDate()) || 
           alquiler.getStartDate().isBefore(LocalDate.now())){
            return new ResponseEntity<>(alquiler, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        else {
            boolean isDisponible = embarcacionRepository.isEmbarcacionAvailable(
                alquiler.getRegistrationNumber(), alquiler.getStartDate(), alquiler.getEndDate());
            
            if(!isDisponible){
                return new ResponseEntity<>(alquiler, HttpStatus.UNPROCESSABLE_ENTITY);
            } else {
                double importeCalculado = alquilerRepository.calculateAmount(
                    alquiler.getStartDate(), alquiler.getEndDate(), alquiler.getNumberOfSeats()); // Adaptado
                alquiler.setAmount(importeCalculado);
                
                int generatedRentalId = alquilerRepository.addAlquiler(alquiler); // REFACTORIZACIÓN: idAlquiler -> generatedRentalId
                if(generatedRentalId != -1){
                    alquiler.setRentalId(generatedRentalId);
                    return new ResponseEntity<>(alquiler, HttpStatus.CREATED);
                } else {
                    return new ResponseEntity<>(alquiler, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlquiler(@PathVariable Integer id) {
        Alquiler alquiler = alquilerRepository.findById(id);
        
        if (alquiler == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            if (alquiler.getStartDate().isBefore(LocalDate.now()) || alquiler.getStartDate().isEqual(LocalDate.now())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            } else {
                boolean isDeleted = alquilerRepository.deleteById(id); // REFACTORIZACIÓN (Regla 1): resultOk -> isDeleted
                if (isDeleted) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
    }

    @PatchMapping(path="/{id}", consumes="application/json")
    public ResponseEntity<Alquiler> updateAlquiler(@PathVariable Integer id, @RequestBody Alquiler updates) { // REFACTORIZACIÓN: patchAlquiler -> updateAlquiler
        Alquiler existingAlquiler = alquilerRepository.findById(id); // REFACTORIZACIÓN: current -> existingAlquiler
        
        if (existingAlquiler == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (existingAlquiler.getStartDate().isBefore(LocalDate.now())) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (updates.getNumberOfSeats() > 0) { // Adaptado
            existingAlquiler.setNumberOfSeats(updates.getNumberOfSeats());
            
            double nuevoImporte = alquilerRepository.calculateAmount(
                existingAlquiler.getStartDate(), existingAlquiler.getEndDate(), existingAlquiler.getNumberOfSeats());
            existingAlquiler.setAmount(nuevoImporte);
        }

        boolean isUpdated = alquilerRepository.update(existingAlquiler); // REFACTORIZACIÓN: resultOk -> isUpdated
        
        if (isUpdated) {
            return new ResponseEntity<>(existingAlquiler, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}