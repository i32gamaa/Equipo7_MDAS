package es.uco.pw.demo.controller.reserva;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import es.uco.pw.demo.model.domain.Reserva;
import es.uco.pw.demo.model.repository.ReservaRepository;

@Controller
public class AddReservaController {

    private ReservaRepository reservaRepository;
    private ModelAndView modelAndView = new ModelAndView();

    public AddReservaController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.reservaRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/addReserva")
    public ModelAndView getAddReservaForm() {
        this.modelAndView.setViewName("reserva/addReservaView");
        this.modelAndView.addObject("newReserva", new Reserva());
        return modelAndView;
    }

    @PostMapping("/addReserva")
    public String addReserva(@ModelAttribute Reserva newReserva, SessionStatus sessionStatus) {
        System.out.println("[AddReservaController] Received info: userId=" + newReserva.getUserId() +
                " registrationNumber=" + newReserva.getRegistrationNumber() +
                " date=" + newReserva.getDate() +
                " numSeats=" + newReserva.getNumSeats() +
                " purpose=" + newReserva.getPurpose() +
                "totalAmount= " + newReserva.getTotalAmount());

        // Comprueba que la embarcación tiene patrón asignado, capacidad suficiente,
        // disponibilidad en la fecha y socio mayor de edad
        boolean hasPatron = reservaRepository.patronAssigned(newReserva.getRegistrationNumber());
        boolean hasCapacity = reservaRepository.hasCapacity(newReserva.getRegistrationNumber(),
                newReserva.getNumSeats());
        boolean isAvailable = reservaRepository.isAvailable(newReserva.getRegistrationNumber(), newReserva.getDate());
        boolean isAvailableInAlquiler = reservaRepository.isAvailableInAlquiler(newReserva.getRegistrationNumber(),
                newReserva.getDate());
        boolean isAdult = reservaRepository.isAdult(newReserva.getUserId());
        if (!hasPatron) {
            System.out.println("[AddReservaController] Error: no tiene patrón asignado la embarcación con matrícula "
                    + newReserva.getRegistrationNumber());
            return "reserva/addReservaFailNOPATRONView";
        } else if (!hasCapacity) {
            System.out.println("[AddReservaController] Error: La embarcación no tiene suficiente capacidad para "
                    + newReserva.getNumSeats() + " plazas.");
            return "reserva/addReservaFailNOCAPACITYView";
        } else if (!isAvailable || !isAvailableInAlquiler) {
            System.out.println("[AddReservaController] Error: La embarcación no está disponible en la fecha: "
                    + newReserva.getDate());
            return "reserva/addReservaFailNOAVAILABLEView";
        } else if (!isAdult) {
            System.out.println("[AddReservaController] Error:El socio con id: " + newReserva.getUserId()
                    + " no es mayor de edad.");
            return "reserva/addReservaFailNOADULTView";
        }

        // Guardar socio
        int success = reservaRepository.addReserva(newReserva);
        String nextPage;

        if (success != -1) {
            nextPage = "reserva/addReservaSuccessView";
        } else {
            nextPage = "reserva/addReservaFailView";
        }

        sessionStatus.setComplete();
        return nextPage;
    }
}
