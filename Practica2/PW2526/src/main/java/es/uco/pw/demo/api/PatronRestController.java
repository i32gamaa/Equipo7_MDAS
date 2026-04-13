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

    @GetMapping
    public ResponseEntity<List<Patron>> getAllPatrones(){
        List<Patron> patrones = patronRepository.findAllPatrones();
        return new ResponseEntity<>(patrones, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Patron> getPatronById(@PathVariable String id){
        Patron patron = patronRepository.findById(id); 
        if(patron != null){
            return new ResponseEntity<>(patron, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(consumes="application/json")
    public ResponseEntity<Patron> createPatron(@RequestBody Patron patron) { // REFACTORIZACIÓN (Regla 11): postPatron -> createPatron
        if(patron.getPatronId() == null || patron.getBirthDate() == null) { // Adaptado (getPatronId)
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if(patronRepository.findById(patron.getPatronId()) != null){
            return new ResponseEntity<>(patron, HttpStatus.UNPROCESSABLE_ENTITY);
        } else if(!isAdult(patron.getBirthDate())){
            return new ResponseEntity<>(patron, HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            boolean isSaved = patronRepository.addPatron(patron); // REFACTORIZACIÓN: resultOk -> isSaved
            if(isSaved){
                return new ResponseEntity<>(patron, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(patron, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PatchMapping(path="/{id}", consumes="application/json")
    public ResponseEntity<Patron> updatePatron(@PathVariable String id, @RequestBody Patron patronUpdates) {
        Patron existingPatron = patronRepository.findById(id); // REFACTORIZACIÓN: currentPatron -> existingPatron
        
        if (existingPatron == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            if (patronUpdates.getName() != null) existingPatron.setName(patronUpdates.getName());
            if (patronUpdates.getSurname() != null) existingPatron.setSurname(patronUpdates.getSurname());
            if (patronUpdates.getBirthDate() != null) existingPatron.setBirthDate(patronUpdates.getBirthDate());
            if (patronUpdates.getTitleIssueDate() != null) existingPatron.setTitleIssueDate(patronUpdates.getTitleIssueDate());

            boolean isUpdated = patronRepository.updatePatron(existingPatron); // REFACTORIZACIÓN: resultOk -> isUpdated
            if (isUpdated) {
                return new ResponseEntity<>(existingPatron, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatron(@PathVariable String id) {
        if (patronRepository.findById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (patronRepository.hasAssignedBoats(id)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            boolean isDeleted = patronRepository.deletePatron(id); // REFACTORIZACIÓN: resultOk -> isDeleted
            if (isDeleted) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    private boolean isAdult(LocalDate birthDate) {
        if (birthDate == null) return false;
        return Period.between(birthDate, LocalDate.now()).getYears() >= 18;
    }
}