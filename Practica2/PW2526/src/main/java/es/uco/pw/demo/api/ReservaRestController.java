package es.uco.pw.demo.api;

import java.util.List;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import es.uco.pw.demo.model.domain.Reserva;
import es.uco.pw.demo.model.repository.ReservaRepository;

@RestController
@RequestMapping(path = "/api/reservas", produces = "application/json")
public class ReservaRestController {

    private final ReservaRepository reservaRepository;

    public ReservaRestController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @GetMapping("/{id}")
    public Reserva getReservaById(@PathVariable Integer id) {
        try {
            // REGLA 20 y 19: Si el repo falla, lanza excepción. El controller la atrapa y devuelve un 404 HTTP limpio.
            return reservaRepository.findById(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada", e);
        }
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva createReserva(@RequestBody Reserva reserva) {
        try {
            reservaRepository.addReserva(reserva);
            return reserva;
        } catch (Exception e) {
            // REGLA 19: Lanzamos excepción HTTP para que Spring gestione el error 500/400.
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo crear la reserva", e);
        }
    }

    @PatchMapping(path = "/{id}/fecha", consumes = "application/json")
    public Reserva updateReservaDate(@PathVariable int id, @RequestBody Reserva requestReserva) {
        try {
            Reserva existingReserva = reservaRepository.findById(id);
            
            // REGLA S3: Do One Thing. Extraemos la lógica de validación de disponibilidad a un método privado.
            // El controlador ahora solo busca, aplica y guarda.
            aplicarNuevaFechaSiEstaDisponible(existingReserva, requestReserva.getReservationDate());
            
            reservaRepository.updateReserva(existingReserva);
            return existingReserva;
            
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error actualizando reserva", e);
        }
    }

    // REGLA S3: Nivel de Abstracción. Aislamos la regla de negocio (comprobar si el barco está libre).
    private void aplicarNuevaFechaSiEstaDisponible(Reserva existingReserva, LocalDate nuevaFecha) {
        if (nuevaFecha == null) return;
        
        boolean isBoatAvailable = reservaRepository.isAvailable(existingReserva.getRegistrationNumber(), nuevaFecha);
        if (!isBoatAvailable) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La embarcación no está disponible en esa fecha");
        }
        existingReserva.setReservationDate(nuevaFecha);
    }
}