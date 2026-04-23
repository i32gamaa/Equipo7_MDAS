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

    public SocioInscripcionRestController(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    @GetMapping("/socios/{id}")
    public Socio getSocioById(@PathVariable String id){
        try {
            return socioRepository.findById(id); 
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Socio no encontrado", e); // Regla 19
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", e);
        }
    }

    @PatchMapping(path="/modificarDatosSocio/{id}", consumes="application/json")
    public Socio updateSocioData(@PathVariable String id, @RequestBody Socio requestSocio) {
        try {
            Socio existingSocio = socioRepository.findById(id); 
            
            // REGLA S3 (Do One Thing): He extraído la lógica de mapeo de campos a un método privado.
            // Así el controlador solo orquesta (busca, aplica, guarda).
            aplicarNuevosCampos(existingSocio, requestSocio);

            socioRepository.updateSocio(existingSocio); 
            return existingSocio;

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El socio a actualizar no existe", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar la base de datos", e);
        }
    }

    // REGLA S3 (Nivel de abstracción): Separamos la lógica de "seteo" y cálculo matemático 
    // del flujo de respuesta HTTP.
    private void aplicarNuevosCampos(Socio existingSocio, Socio requestSocio) {
        if (requestSocio.getName() != null) existingSocio.setName(requestSocio.getName());
        if (requestSocio.getSurname() != null) existingSocio.setSurname(requestSocio.getSurname());
        if (requestSocio.getBirthdate() != null) {
            existingSocio.setBirthdate(requestSocio.getBirthdate());
            existingSocio.setAdult(calcularSiEsAdulto(requestSocio.getBirthdate()));
        }
    }

    // REGLA S3: Hacer una sola cosa (calcular edad)
    private boolean calcularSiEsAdulto(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears() >= 18;
    }
}