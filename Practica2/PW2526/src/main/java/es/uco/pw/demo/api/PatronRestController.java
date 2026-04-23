package es.uco.pw.demo.api;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import es.uco.pw.demo.model.domain.Patron;
import es.uco.pw.demo.model.repository.PatronRepository;

@RestController
@RequestMapping(path="/api/patrones", produces="application/json")
public class PatronRestController {

    private final PatronRepository patronRepository;
    
    public PatronRestController(PatronRepository patronRepository){
        this.patronRepository = patronRepository;
    }

    @GetMapping("/{id}")
    public Patron getPatronById(@PathVariable String id){
        try {
            return patronRepository.findById(id); 
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patrón no encontrado", e); // Regla 19
        }
    }

    @PatchMapping(path="/{id}", consumes="application/json")
    public Patron updatePatron(@PathVariable String id, @RequestBody Patron patronUpdates) {
        try {
            Patron existingPatron = patronRepository.findById(id); 
            
            // REGLA S3: Do One Thing. Extracción de mapeo.
            mapearActualizaciones(existingPatron, patronUpdates);

            patronRepository.updatePatron(existingPatron);
            return existingPatron;

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El patrón a actualizar no existe", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", e);
        }
    }

    // REGLA S3: Extraigo el bloque condicional a su propia función (Nivel de abstracción aislado)
    private void mapearActualizaciones(Patron existing, Patron updates) {
        if (updates.getName() != null) existing.setName(updates.getName());
        if (updates.getSurname() != null) existing.setSurname(updates.getSurname());
        if (updates.getBirthDate() != null) existing.setBirthDate(updates.getBirthDate());
        if (updates.getTitleIssueDate() != null) existing.setTitleIssueDate(updates.getTitleIssueDate());
    }
}