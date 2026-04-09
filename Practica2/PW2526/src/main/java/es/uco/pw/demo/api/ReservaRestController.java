package es.uco.pw.demo.api;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.uco.pw.demo.model.domain.Reserva;
import es.uco.pw.demo.model.repository.ReservaRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController()
@RequestMapping(path = "/api/reservas", produces = "application/json")
public class ReservaRestController {

    ReservaRepository reservaRepository;

    public ReservaRestController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.reservaRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    // ======= Lista completa de reservas ============================
    // GET /api/reservas -> lista todas las reservas
    @GetMapping
    public ResponseEntity<List<Reserva>> getAllReservas() {
        List<Reserva> reservas = reservaRepository.findAllReservas();
        ResponseEntity<List<Reserva>> response = new ResponseEntity<>(reservas, HttpStatus.OK);
        return response;
    }

    // ======= Información completa dado su identificador ======================
    // GET /api/reservas/{id} -> obtiene una reserva por id
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> getReservaById(@PathVariable Integer id) {
        Reserva reserva = reservaRepository.findById(id);
        ResponseEntity<Reserva> response;
        if (reserva != null) {
            response = new ResponseEntity<>(reserva, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    // ======= Listas de reservas futuras apartir de una fecha
    // GET /api/reservas?date=YYYY-MM-DD -> lista las reservas a partir de una fecha
    @GetMapping(params = "date")
    public ResponseEntity<List<Reserva>> getReservaByDate(
            @RequestParam LocalDate date) {
        List<Reserva> reservas = reservaRepository.findByDate(date);
        ResponseEntity<List<Reserva>> response = new ResponseEntity<>(reservas, HttpStatus.OK);
        return response;
    }

    // ======== Crea reserva si la embarcación está disponible y + factores=========
    // POST /api/reservas -> crea una nueva reserva
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva postReserva(@RequestBody Reserva reserva) {
        int resultOk = reservaRepository.addReserva(reserva);
        if (resultOk > 0)
            return reserva;
        else
            return null;
    }

    // ----------Modifica la reserva solo si la embarcación está
    // disponible-----------
    // PATCH /api/reservas/{id}/fecha -> actualiza la fecha de una reserva
    @PatchMapping(path = "/{id}/fecha", consumes = "application/json")
    public Reserva patchReservaDate(@PathVariable int id, @RequestBody Reserva requestReserva) {
        Reserva response = requestReserva;
        try {
            // Get reserva by id
            Reserva currentReserva = this.reservaRepository.findById(id);
            if (currentReserva != null) {
                // Usar siempre la embarcación actual para validar
                String embarcacionActual = currentReserva.getRegistrationNumber();

                // Solo actualizar fecha si la embarcación está disponible
                if (requestReserva.getDate() != null) {
                    boolean disponible = false;
                    if (reservaRepository.isAvailable(embarcacionActual, requestReserva.getDate())
                            && reservaRepository.isAvailableInAlquiler(embarcacionActual, requestReserva.getDate())) {
                        disponible = true;
                    }
                    if (disponible) {
                        currentReserva.setDate(requestReserva.getDate());
                    }
                }
                if (requestReserva.getRegistrationNumber() != null) {
                    currentReserva.setRegistrationNumber(requestReserva.getRegistrationNumber());
                }

                if (requestReserva.getPurpose() != null) {
                    currentReserva.setPurpose(requestReserva.getPurpose());
                }
                if (requestReserva.getNumSeats() != 0) {
                    currentReserva.setNumSeats(requestReserva.getNumSeats());
                }

                if (requestReserva.getUserId() != null) {
                    currentReserva.setUserId(requestReserva.getUserId());
                }
                if (requestReserva.getTotalAmount() != 0) {
                    currentReserva.setTotalAmount(requestReserva.getNumSeats() * 40);
                }
                // Save updated resource
                boolean resultOk = reservaRepository.updateReserva(currentReserva);
                if (resultOk) {
                    response = currentReserva;
                }
            }
        } catch (Exception e) {
            return requestReserva;
        }
        return response;
    }

    // ----------Modifica la reserva si numSeats no es mayor de las
    // disponibles-----------
    // PATCH /api/reservas/{id}/plazas -> actualiza el propósito y número de plazas de una reserva
    @PatchMapping(path = "/{id}/plazas", consumes = "application/json")
    public Reserva patchReservaPurposeYNumSeats(@PathVariable int id, @RequestBody Reserva requestReserva) {
        Reserva response = requestReserva;
        try {
            // Get reserva by id
            Reserva currentReserva = this.reservaRepository.findById(id);
            if (currentReserva != null) {
                // Usar siempre la embarcación actual para validar
                String embarcacionActual = currentReserva.getRegistrationNumber();

                // Solo actualizar numSeats si la embarcación tiene plazas disponibles
                if (requestReserva.getNumSeats() != 0) {
                    boolean disponible = false;
                    if (reservaRepository.hasCapacity(embarcacionActual, requestReserva.getNumSeats())) {
                        disponible = true;
                    }
                    if (disponible) {
                        currentReserva.setNumSeats(requestReserva.getNumSeats());
                    }
                }

                if (requestReserva.getRegistrationNumber() != null) {
                    currentReserva.setRegistrationNumber(requestReserva.getRegistrationNumber());
                }
                if (requestReserva.getPurpose() != null) {
                    currentReserva.setPurpose(requestReserva.getPurpose());
                }
                if (requestReserva.getDate() != null) {
                    currentReserva.setDate(requestReserva.getDate());
                }

                if (requestReserva.getUserId() != null) {
                    currentReserva.setUserId(requestReserva.getUserId());
                }
                if (requestReserva.getTotalAmount() != 0) {
                    currentReserva.setTotalAmount(requestReserva.getNumSeats() * 40);
                }

                // Save updated resource
                boolean resultOk = reservaRepository.updateReserva(currentReserva);
                if (resultOk) {
                    response = currentReserva;
                }
            }
        } catch (Exception e) {
            return requestReserva;
        }
        return response;
    }
    // DELETE /api/reservas/{id} -> elimina una reserva por id
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReserva(@PathVariable int id) {
        Reserva reserva = this.reservaRepository.findById(id);
        if (reserva != null)
            this.reservaRepository.deleteReserva(id);
    }
}