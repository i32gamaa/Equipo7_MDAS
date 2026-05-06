package es.uco.pw.demo.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import es.uco.pw.demo.model.domain.Alquiler;
import es.uco.pw.demo.model.repository.AlquilerRepository;

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
            // SEMANA 4: Simplificar lógica condicional. Hemos eliminado el chequeo redundante 
            // "if (alquiler == null)" porque el repositorio ahora ya se encarga de lanzar 
            // la excepción directamente, evitando código basura en el controlador.
            return alquilerRepository.findById(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alquiler no encontrado", e);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlquiler(@PathVariable Integer id) {
        try {
            alquilerRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el alquiler a borrar", e);
        }
    }

    @GetMapping("/vigentes")
    public List<Alquiler> getVigentes() {
        return alquilerRepository.findCurrentAndFutureAlquileres();
    }
}