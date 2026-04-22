package es.uco.pw.demo.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import es.uco.pw.demo.model.domain.Alquiler;
import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.repository.AlquilerRepository;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;

@RestController
@RequestMapping(path="/api/alquileres", produces="application/json")
public class AlquilerRestController {

    private final AlquilerRepository alquilerRepository;
    
    public AlquilerRestController(AlquilerRepository alquilerRepository){
        this.alquilerRepository = alquilerRepository;
    }

    @GetMapping("/{id}")
    public Alquiler getAlquilerById(@PathVariable Integer id){
        try {
            Alquiler alquiler = alquilerRepository.findById(id);
            if (alquiler == null) throw new IllegalArgumentException(); // Forzamos si el repo no lanzara excepción
            return alquiler;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alquiler no encontrado", e); // REGLA 19
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlquiler(@PathVariable Integer id) {
        try {
            // El repo ya lanzará excepción si no existe (Regla 19)
            alquilerRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el alquiler a borrar", e);
        }
    }
}