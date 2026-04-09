package es.uco.pw.demo.api;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.uco.pw.demo.model.domain.Patron;
import es.uco.pw.demo.model.repository.PatronRepository;

@RestController
@RequestMapping(path="/api/patrones", produces="application/json")
public class PatronRestController {

    PatronRepository patronRepository;
    
    public PatronRestController(PatronRepository patronRepository){
        this.patronRepository = patronRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.patronRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    // GET /api/patrones --> Lista todos los patrones
    @GetMapping
    public ResponseEntity<List<Patron>> getAllPatrones(){
        List<Patron> patrones = patronRepository.findAllPatrones();
        ResponseEntity<List<Patron>> response = new ResponseEntity<>(patrones, HttpStatus.OK);
        return response;
    }
     //GET /api/patrones/{id} --> Obtiene un patrón por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Patron> getPatronById(@PathVariable String id){
        Patron patron = patronRepository.findById(id); 
        ResponseEntity<Patron> response;
        if(patron != null){
            response = new ResponseEntity<>(patron, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    //POST /api/patrones --> Crea un nuevo patrón
    @PostMapping(consumes="application/json")
    public ResponseEntity<Patron> postPatron(@RequestBody Patron patron) {
        ResponseEntity<Patron> response;

        if(patron.getId() == null || patron.getBirthDate() == null) {
             response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if(patronRepository.findById(patron.getId()) != null){
            response = new ResponseEntity<>(patron, HttpStatus.UNPROCESSABLE_ENTITY);
        } else if(!isAdult(patron.getBirthDate())){
            response = new ResponseEntity<>(patron, HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            boolean resultOk = patronRepository.addPatron(patron);
            if(resultOk){
                response = new ResponseEntity<>(patron, HttpStatus.CREATED);
            } else {
                response = new ResponseEntity<>(patron, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return response;
    }

    //PATCH /api/patrones/{id} --> Actualiza los datos de un patrón
    @PatchMapping(path="/{id}", consumes="application/json")
    public ResponseEntity<Patron> updatePatron(@PathVariable String id, @RequestBody Patron patronUpdates) {
        ResponseEntity<Patron> response;
        Patron currentPatron = patronRepository.findById(id);
        
        if (currentPatron == null) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            if (patronUpdates.getName() != null) currentPatron.setName(patronUpdates.getName());
            if (patronUpdates.getSurname() != null) currentPatron.setSurname(patronUpdates.getSurname());
            if (patronUpdates.getBirthDate() != null) currentPatron.setBirthDate(patronUpdates.getBirthDate());
            if (patronUpdates.getTitleIssueDate() != null) currentPatron.setTitleIssueDate(patronUpdates.getTitleIssueDate());

            boolean resultOk = patronRepository.updatePatron(currentPatron);
            if (resultOk) {
                response = new ResponseEntity<>(currentPatron, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return response;
    }

    //DELETE /api/patrones/{id} --> Elimina un patrón
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatron(@PathVariable String id) {
        ResponseEntity<Void> response;
        
        if (patronRepository.findById(id) == null) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (patronRepository.hasAssignedBoats(id)) {
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            boolean resultOk = patronRepository.deletePatron(id);
            if (resultOk) {
                response = new ResponseEntity<>(HttpStatus.NO_CONTENT); 
            } else {
                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return response;
    }

    private boolean isAdult(LocalDate birthDate) {
        if (birthDate == null) return false;
        return Period.between(birthDate, LocalDate.now()).getYears() >= 18;
    }
}