package es.uco.pw.demo.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.EmbarcacionType;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;

@RestController
@RequestMapping(path="/api/embarcaciones", produces="application/json")
public class EmbarcacionRestController {

    EmbarcacionRepository embarcacionRepository;
    
    public EmbarcacionRestController(EmbarcacionRepository embarcacionRepository){
        this.embarcacionRepository = embarcacionRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.embarcacionRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping
    public ResponseEntity<List<Embarcacion>> getAllEmbarcaciones(){
        List<Embarcacion> embarcaciones = embarcacionRepository.findAllEmbarcaciones();
        return new ResponseEntity<>(embarcaciones, HttpStatus.OK);
    }

    @GetMapping(params="type")
    public ResponseEntity<List<Embarcacion>> getEmbarcacionesByType(@RequestParam String type) {
        try {
            EmbarcacionType enumType = EmbarcacionType.valueOf(type.toUpperCase());
            List<Embarcacion> embarcaciones = embarcacionRepository.findByType(enumType);
            return new ResponseEntity<>(embarcaciones, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{matricula}")
    public ResponseEntity<Embarcacion> getEmbarcacionByMatricula(@PathVariable String matricula){
        Embarcacion embarcacion = embarcacionRepository.findByRegistration(matricula);
        if(embarcacion != null){
            return new ResponseEntity<>(embarcacion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(consumes="application/json")
    public ResponseEntity<Embarcacion> createEmbarcacion(@RequestBody Embarcacion embarcacion) { // REFACTORIZACIÓN (Regla 11): postEmbarcacion -> createEmbarcacion
        if (embarcacion.getRegistrationNumber() == null || embarcacion.getName() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if(embarcacionRepository.findByRegistration(embarcacion.getRegistrationNumber()) != null){
            return new ResponseEntity<>(embarcacion, HttpStatus.UNPROCESSABLE_ENTITY);
        } else if(embarcacionRepository.findByName(embarcacion.getName()) != null){
            return new ResponseEntity<>(embarcacion, HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            boolean isSaved = embarcacionRepository.addEmbarcacion(embarcacion); // REFACTORIZACIÓN: resultOk -> isSaved
            if(isSaved){
                return new ResponseEntity<>(embarcacion, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(embarcacion, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PatchMapping(path="/{matricula}", consumes="application/json")
    public ResponseEntity<Embarcacion> updateEmbarcacion(@PathVariable String matricula, @RequestBody Embarcacion updates) {
        Embarcacion existingEmbarcacion = embarcacionRepository.findByRegistration(matricula); // REFACTORIZACIÓN: current -> existingEmbarcacion
        
        if (existingEmbarcacion == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            if (updates.getName() != null) existingEmbarcacion.setName(updates.getName());
            if (updates.getType() != null) existingEmbarcacion.setType(updates.getType());
            if (updates.getNumberOfSeats() > 0) existingEmbarcacion.setNumberOfSeats(updates.getNumberOfSeats()); // Adaptado
            if (updates.getLength() > 0) existingEmbarcacion.setLength(updates.getLength());
            if (updates.getWidth() > 0) existingEmbarcacion.setWidth(updates.getWidth());
            if (updates.getHeight() > 0) existingEmbarcacion.setHeight(updates.getHeight());

            if (updates.getPatronId() != null) {
                 existingEmbarcacion.setPatronId(updates.getPatronId());
            }

            boolean isUpdated = embarcacionRepository.update(existingEmbarcacion); // REFACTORIZACIÓN: resultOk -> isUpdated
            if (isUpdated) {
                return new ResponseEntity<>(existingEmbarcacion, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @DeleteMapping("/{matricula}")
    public ResponseEntity<Void> deleteEmbarcacion(@PathVariable String matricula) {
        if (embarcacionRepository.findByRegistration(matricula) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (embarcacionRepository.hasAlquileres(matricula)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            boolean isDeleted = embarcacionRepository.deleteByRegistration(matricula); // REFACTORIZACIÓN: resultOk -> isDeleted
            if (isDeleted) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}