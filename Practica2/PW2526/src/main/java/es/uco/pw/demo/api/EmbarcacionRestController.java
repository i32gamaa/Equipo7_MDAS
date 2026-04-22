package es.uco.pw.demo.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.EmbarcacionType;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;

@RestController
@RequestMapping(path="/api/embarcaciones", produces="application/json")
public class EmbarcacionRestController {

    private final EmbarcacionRepository embarcacionRepository;
    
    public EmbarcacionRestController(EmbarcacionRepository embarcacionRepository){
        this.embarcacionRepository = embarcacionRepository;
    }

    @GetMapping("/{matricula}")
    public Embarcacion getEmbarcacionByMatricula(@PathVariable String matricula){
        try {
            return embarcacionRepository.findByRegistration(matricula);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Embarcación no encontrada", e); // REGLA 19
        }
    }
}