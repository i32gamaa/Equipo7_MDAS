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
    //GET /api/embarcaciones -> lista todas las embarcaciones
    @GetMapping
    public ResponseEntity<List<Embarcacion>> getAllEmbarcaciones(){
        List<Embarcacion> embarcaciones = embarcacionRepository.findAllEmbarcaciones();
        ResponseEntity<List<Embarcacion>> response = new ResponseEntity<>(embarcaciones, HttpStatus.OK);
        return response;
    }
    //GET /api/embarcaciones?type=TYPE -> lista las embarcaciones por tipo
    @GetMapping(params="type")
    public ResponseEntity<List<Embarcacion>> getEmbarcacionesByType(@RequestParam String type) {
        ResponseEntity<List<Embarcacion>> response;
        try {
            EmbarcacionType enumType = EmbarcacionType.valueOf(type.toUpperCase());
            List<Embarcacion> embarcaciones = embarcacionRepository.findByType(enumType);
            response = new ResponseEntity<>(embarcaciones, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return response;
    }
    //GET /api/embarcaciones/{matricula} -> obtiene una embarcacion por matricula
    @GetMapping("/{matricula}")
    public ResponseEntity<Embarcacion> getEmbarcacionByMatricula(@PathVariable String matricula){
        Embarcacion embarcacion = embarcacionRepository.findByRegistration(matricula);
        ResponseEntity<Embarcacion> response;
        
        if(embarcacion != null){
            response = new ResponseEntity<>(embarcacion, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }
    //POST /api/embarcaciones -> crea una nueva embarcacion
    @PostMapping(consumes="application/json")
    public ResponseEntity<Embarcacion> postEmbarcacion(@RequestBody Embarcacion embarcacion) {
        ResponseEntity<Embarcacion> response;

        if (embarcacion.getRegistrationNumber() == null || embarcacion.getName() == null) {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if(embarcacionRepository.findByRegistration(embarcacion.getRegistrationNumber()) != null){
            response = new ResponseEntity<>(embarcacion, HttpStatus.UNPROCESSABLE_ENTITY);
        } else if(embarcacionRepository.findByName(embarcacion.getName()) != null){
            response = new ResponseEntity<>(embarcacion, HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            boolean resultOk = embarcacionRepository.addEmbarcacion(embarcacion);
            if(resultOk){
                response = new ResponseEntity<>(embarcacion, HttpStatus.CREATED);
            } else {
                response = new ResponseEntity<>(embarcacion, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return response;
    }
    //PATCH /api/embarcaciones/{matricula} -> actualiza una embarcacion por matricula
    @PatchMapping(path="/{matricula}", consumes="application/json")
    public ResponseEntity<Embarcacion> updateEmbarcacion(@PathVariable String matricula, @RequestBody Embarcacion updates) {
        ResponseEntity<Embarcacion> response;
        Embarcacion current = embarcacionRepository.findByRegistration(matricula);
        
        if (current == null) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            if (updates.getName() != null) current.setName(updates.getName());
            if (updates.getType() != null) current.setType(updates.getType());
            if (updates.getNumSeats() > 0) current.setNumSeats(updates.getNumSeats());
            if (updates.getLength() > 0) current.setLength(updates.getLength());
            if (updates.getWidth() > 0) current.setWidth(updates.getWidth());
            if (updates.getHeight() > 0) current.setHeight(updates.getHeight());

            if (updates.getPatronId() != null) {
                 current.setPatronId(updates.getPatronId());
            }

            boolean resultOk = embarcacionRepository.update(current);
            if (resultOk) {
                response = new ResponseEntity<>(current, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return response;
    }
    //DELETE /api/embarcaciones/{matricula} -> elimina una embarcacion por matricula
    @DeleteMapping("/{matricula}")
    public ResponseEntity<Void> deleteEmbarcacion(@PathVariable String matricula) {
        ResponseEntity<Void> response;
        
        if (embarcacionRepository.findByRegistration(matricula) == null) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (embarcacionRepository.hasAlquileres(matricula)) {
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            boolean resultOk = embarcacionRepository.deleteByRegistration(matricula);
            if (resultOk) {
                response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return response;
    }
}