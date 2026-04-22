package es.uco.pw.demo.api;

import java.util.List;
import java.time.LocalDate;
import java.time.Period;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.repository.SocioRepository;

@RestController()
@RequestMapping(path="/api/socioinscripcion", produces="application/json")
public class SocioInscripcionRestController {

    private final SocioRepository socioRepository;
    

    public SocioInscripcionRestController(SocioRepository socioRepository /*...*/) {
        this.socioRepository = socioRepository;
    }

    @GetMapping("/socios/{id}")
    public Socio getSocioById(@PathVariable String id){
        try {
            // Si el socio no existe, el repo lanzará IllegalArgumentException
            return socioRepository.findById(id); 
        } catch (IllegalArgumentException e) {
            // REGLA 19: Excepciones para controlar el flujo de error
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Socio no encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", e);
        }
    }

    @PatchMapping(path="/modificarDatosSocio/{id}", consumes="application/json")
    public Socio updateSocioData(@PathVariable String id, @RequestBody Socio requestSocio) {
        try {
            Socio existingSocio = socioRepository.findById(id); 
            
            if (requestSocio.getName() != null) existingSocio.setName(requestSocio.getName());
            if (requestSocio.getSurname() != null) existingSocio.setSurname(requestSocio.getSurname());
            if (requestSocio.getBirthdate() != null) {
                existingSocio.setBirthdate(requestSocio.getBirthdate());
                existingSocio.setAdult(Period.between(requestSocio.getBirthdate(), LocalDate.now()).getYears() >= 18);
            }

            socioRepository.updateSocio(existingSocio); // Si falla, lanza excepción
            return existingSocio;

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El socio a actualizar no existe", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar la base de datos", e);
        }
    }

}