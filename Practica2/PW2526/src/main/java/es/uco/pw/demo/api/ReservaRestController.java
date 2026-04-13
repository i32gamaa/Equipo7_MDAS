package es.uco.pw.demo.api;

import java.util.List;
import java.time.LocalDate;

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

    @GetMapping
    public ResponseEntity<List<Reserva>> getAllReservas() {
        List<Reserva> reservas = reservaRepository.findAllReservas();
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> getReservaById(@PathVariable Integer id) {
        Reserva reserva = reservaRepository.findById(id);
        if (reserva != null) {
            return new ResponseEntity<>(reserva, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(params = "date")
    public ResponseEntity<List<Reserva>> getReservasByDate( // REFACTORIZACIÓN: getReservaByDate -> getReservasByDate (devuelve lista)
            @RequestParam LocalDate date) {
        List<Reserva> reservas = reservaRepository.findByDate(date);
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva createReserva(@RequestBody Reserva reserva) { // REFACTORIZACIÓN (Regla 11): postReserva -> createReserva
        int generatedReservaId = reservaRepository.addReserva(reserva); // REFACTORIZACIÓN: resultOk -> generatedReservaId
        if (generatedReservaId > 0)
            return reserva;
        else
            return null;
    }

    @PatchMapping(path = "/{id}/fecha", consumes = "application/json")
    public Reserva updateReservaDate(@PathVariable int id, @RequestBody Reserva requestReserva) { // REFACTORIZACIÓN: patchReservaDate -> updateReservaDate
        try {
            Reserva existingReserva = this.reservaRepository.findById(id); // REFACTORIZACIÓN: currentReserva -> existingReserva
            if (existingReserva != null) {
                String embarcacionAsignada = existingReserva.getRegistrationNumber(); // REFACTORIZACIÓN: embarcacionActual -> embarcacionAsignada

                if (requestReserva.getReservationDate() != null) { // Adaptado (getReservationDate)
                    boolean isBoatAvailable = reservaRepository.isAvailable(embarcacionAsignada, requestReserva.getReservationDate())
                            && reservaRepository.isAvailableInAlquiler(embarcacionAsignada, requestReserva.getReservationDate()); // REFACTORIZACIÓN: disponible -> isBoatAvailable
                    
                    if (isBoatAvailable) {
                        existingReserva.setReservationDate(requestReserva.getReservationDate());
                    }
                }
                // (El resto de actualizaciones se mantienen igual, adaptando los getters/setters del modelo)
                if (requestReserva.getRegistrationNumber() != null) {
                    existingReserva.setRegistrationNumber(requestReserva.getRegistrationNumber());
                }
                if (requestReserva.getPurpose() != null) {
                    existingReserva.setPurpose(requestReserva.getPurpose());
                }
                if (requestReserva.getNumberOfSeats() != 0) { // Adaptado
                    existingReserva.setNumberOfSeats(requestReserva.getNumberOfSeats());
                }
                if (requestReserva.getUserId() != null) {
                    existingReserva.setUserId(requestReserva.getUserId());
                }
                if (requestReserva.getTotalAmount() != 0) {
                    existingReserva.setTotalAmount(requestReserva.getNumberOfSeats() * 40); // Adaptado
                }

                boolean isUpdated = reservaRepository.updateReserva(existingReserva); // REFACTORIZACIÓN: resultOk -> isUpdated
                if (isUpdated) {
                    return existingReserva;
                }
            }
        } catch (Exception e) {
            return requestReserva;
        }
        return requestReserva;
    }

    @PatchMapping(path = "/{id}/plazas", consumes = "application/json")
    public Reserva updateReservaPurposeAndSeats(@PathVariable int id, @RequestBody Reserva requestReserva) { // REFACTORIZACIÓN: patchReservaPurposeYNumSeats -> updateReservaPurposeAndSeats
        try {
            Reserva existingReserva = this.reservaRepository.findById(id);
            if (existingReserva != null) {
                String embarcacionAsignada = existingReserva.getRegistrationNumber();

                if (requestReserva.getNumberOfSeats() != 0) { // Adaptado
                    boolean hasEnoughCapacity = reservaRepository.hasCapacity(embarcacionAsignada, requestReserva.getNumberOfSeats()); // REFACTORIZACIÓN: disponible -> hasEnoughCapacity
                    if (hasEnoughCapacity) {
                        existingReserva.setNumberOfSeats(requestReserva.getNumberOfSeats());
                    }
                }

                if (requestReserva.getRegistrationNumber() != null) {
                    existingReserva.setRegistrationNumber(requestReserva.getRegistrationNumber());
                }
                if (requestReserva.getPurpose() != null) {
                    existingReserva.setPurpose(requestReserva.getPurpose());
                }
                if (requestReserva.getReservationDate() != null) { // Adaptado
                    existingReserva.setReservationDate(requestReserva.getReservationDate());
                }
                if (requestReserva.getUserId() != null) {
                    existingReserva.setUserId(requestReserva.getUserId());
                }
                if (requestReserva.getTotalAmount() != 0) {
                    existingReserva.setTotalAmount(requestReserva.getNumberOfSeats() * 40); // Adaptado
                }

                boolean isUpdated = reservaRepository.updateReserva(existingReserva);
                if (isUpdated) {
                    return existingReserva;
                }
            }
        } catch (Exception e) {
            return requestReserva;
        }
        return requestReserva;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReserva(@PathVariable int id) {
        Reserva reserva = this.reservaRepository.findById(id);
        if (reserva != null)
            this.reservaRepository.deleteReserva(id);
    }
}