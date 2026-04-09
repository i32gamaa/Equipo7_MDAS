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
    //GET /api/alquileres -> lista todos los alquileres
    @GetMapping
    public ResponseEntity<List<Alquiler>> getAllAlquileres(){
        List<Alquiler> alquileres = alquilerRepository.findAllAlquileres();
        ResponseEntity<List<Alquiler>> response = new ResponseEntity<>(alquileres, HttpStatus.OK);
        return response;
    }
    //GET /api/alquileres?fecha=YYYY-MM-DD -> lista los alquileres a partir de una fecha
    @GetMapping(params="fecha")
    public ResponseEntity<List<Alquiler>> getAlquileresFuturos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<Alquiler> alquileres = alquilerRepository.findByDate(fecha);
        ResponseEntity<List<Alquiler>> response = new ResponseEntity<>(alquileres, HttpStatus.OK);
        return response;
    }
    //GET /api/alquileres/{id} -> obtiene un alquiler por id
    @GetMapping("/{id}")
    public ResponseEntity<Alquiler> getAlquilerById(@PathVariable Integer id){
        Alquiler alquiler = alquilerRepository.findById(id);
        ResponseEntity<Alquiler> response;
        
        if(alquiler != null){
            response = new ResponseEntity<>(alquiler, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }
    //GET /api/alquileres/disponibles?start=YYYY-MM-DD&end=YYYY-MM-DD -> lista las embarcaciones disponibles en un rango de fechas
    @GetMapping(path = "/disponibles", params = {"start", "end"})
    public ResponseEntity<List<Embarcacion>> getAvailableBoats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        
        List<Embarcacion> disponibles = embarcacionRepository.findAvailableByDateRange(start, end);
        ResponseEntity<List<Embarcacion>> response = new ResponseEntity<>(disponibles, HttpStatus.OK);
        return response;
    }
    //POST /api/alquileres -> crea un nuevo alquiler
    @PostMapping(consumes="application/json")
    public ResponseEntity<Alquiler> postAlquiler(@RequestBody Alquiler alquiler) {
        ResponseEntity<Alquiler> response;

        if(alquiler.getStartDate() == null || alquiler.getEndDate() == null || 
           alquiler.getRegistrationNumber() == null || alquiler.getUserId() == null){
            response = new ResponseEntity<>(alquiler, HttpStatus.BAD_REQUEST);
        } 
        else if(alquiler.getStartDate().isAfter(alquiler.getEndDate()) || 
           alquiler.getStartDate().isBefore(LocalDate.now())){
            response = new ResponseEntity<>(alquiler, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        else {
            boolean isDisponible = embarcacionRepository.isEmbarcacionAvailable(
                alquiler.getRegistrationNumber(), alquiler.getStartDate(), alquiler.getEndDate());
            
            if(!isDisponible){
                response = new ResponseEntity<>(alquiler, HttpStatus.UNPROCESSABLE_ENTITY);
            } else {
                double importeCalculado = alquilerRepository.calculateAmount(
                    alquiler.getStartDate(), alquiler.getEndDate(), alquiler.getNumSeats());
                alquiler.setAmount(importeCalculado);
                
                int idAlquiler = alquilerRepository.addAlquiler(alquiler);
                if(idAlquiler != -1){
                    alquiler.setRentalId(idAlquiler);
                    response = new ResponseEntity<>(alquiler, HttpStatus.CREATED);
                } else {
                    response = new ResponseEntity<>(alquiler, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        return response;
    }
    //DELETE /api/alquileres/{id} -> elimina un alquiler por id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlquiler(@PathVariable Integer id) {
        ResponseEntity<Void> response;
        Alquiler alquiler = alquilerRepository.findById(id);
        
        if (alquiler == null) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            if (alquiler.getStartDate().isBefore(LocalDate.now()) || alquiler.getStartDate().isEqual(LocalDate.now())) {
                response = new ResponseEntity<>(HttpStatus.CONFLICT);
            } else {
                boolean resultOk = alquilerRepository.deleteById(id);
                if (resultOk) {
                    response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else {
                    response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        return response;
    }

    //PATCH /api/alquileres/{id} -> actualiza un alquiler por id
    @PatchMapping(path="/{id}", consumes="application/json")
    public ResponseEntity<Alquiler> patchAlquiler(@PathVariable Integer id, @RequestBody Alquiler updates) {
        Alquiler current = alquilerRepository.findById(id);
        
        if (current == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 1. Validar que es un alquiler futuro
        if (current.getStartDate().isBefore(LocalDate.now())) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // 2. Si nos envían nuevas plazas, actualizamos y recalculamos precio
        if (updates.getNumSeats() > 0) {
            current.setNumSeats(updates.getNumSeats());
            
            // Recalcular precio
            double nuevoImporte = alquilerRepository.calculateAmount(
                current.getStartDate(), current.getEndDate(), current.getNumSeats());
            current.setAmount(nuevoImporte);
        }

        // 3. Guardar cambios
        boolean resultOk = alquilerRepository.update(current); 
        
        if (resultOk) {
            return new ResponseEntity<>(current, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}