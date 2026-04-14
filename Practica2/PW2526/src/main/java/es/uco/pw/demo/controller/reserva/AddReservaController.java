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
    public ModelAndView mostrarFormularioRegistro() {
        this.modelAndView.setViewName("reserva/addReservaView");
        this.modelAndView.addObject("newReserva", new Reserva());
        return modelAndView;
    }

    @PostMapping("/addReserva")
    public String procesarNuevaReserva(@ModelAttribute("newReserva") Reserva reservaSolicitada, SessionStatus estadoSesion) {
        
        boolean tienePatronAsignado = reservaRepository.patronAssigned(reservaSolicitada.getRegistrationNumber());
        boolean tieneCapacidadSuficiente = reservaRepository.hasCapacity(reservaSolicitada.getRegistrationNumber(),
                reservaSolicitada.getNumberOfSeats());
        boolean estaDisponibleReserva = reservaRepository.isAvailable(reservaSolicitada.getRegistrationNumber(), reservaSolicitada.getReservationDate());
        boolean estaDisponibleAlquiler = reservaRepository.isAvailableInAlquiler(reservaSolicitada.getRegistrationNumber(),
                reservaSolicitada.getReservationDate());
        boolean esSocioMayorDeEdad = reservaRepository.isAdult(reservaSolicitada.getUserId());
        
        if (!tienePatronAsignado) {
            return "reserva/addReservaFailNOPATRONView";
        } else if (!tieneCapacidadSuficiente) {
            return "reserva/addReservaFailNOCAPACITYView";
        } else if (!estaDisponibleReserva || !estaDisponibleAlquiler) {
            return "reserva/addReservaFailNOAVAILABLEView";
        } else if (!esSocioMayorDeEdad) {
            return "reserva/addReservaFailNOADULTView";
        }

        int filasAfectadas = reservaRepository.addReserva(reservaSolicitada);
        String vistaDestino;

        if (filasAfectadas != -1) {
            vistaDestino = "reserva/addReservaSuccessView";
        } else {
            vistaDestino = "reserva/addReservaFailView";
        }

        estadoSesion.setComplete();
        return vistaDestino;
    }
}