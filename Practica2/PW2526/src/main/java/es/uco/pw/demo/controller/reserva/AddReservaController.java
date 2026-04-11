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
        System.out.println("[AddReservaController] Info recibida: userId=" + reservaSolicitada.getUserId() +
                " matricula=" + reservaSolicitada.getRegistrationNumber() +
                " fecha=" + reservaSolicitada.getDate() +
                " plazas=" + reservaSolicitada.getNumSeats() +
                " proposito=" + reservaSolicitada.getPurpose() +
                " importeTotal=" + reservaSolicitada.getTotalAmount());

        boolean tienePatronAsignado = reservaRepository.patronAssigned(reservaSolicitada.getRegistrationNumber());
        boolean tieneCapacidadSuficiente = reservaRepository.hasCapacity(reservaSolicitada.getRegistrationNumber(),
                reservaSolicitada.getNumSeats());
        boolean estaDisponibleReserva = reservaRepository.isAvailable(reservaSolicitada.getRegistrationNumber(), reservaSolicitada.getDate());
        boolean estaDisponibleAlquiler = reservaRepository.isAvailableInAlquiler(reservaSolicitada.getRegistrationNumber(),
                reservaSolicitada.getDate());
        boolean esSocioMayorDeEdad = reservaRepository.isAdult(reservaSolicitada.getUserId());
        
        if (!tienePatronAsignado) {
            System.out.println("[AddReservaController] Error: no tiene patrón asignado la embarcación con matrícula "
                    + reservaSolicitada.getRegistrationNumber());
            return "reserva/addReservaFailNOPATRONView";
        } else if (!tieneCapacidadSuficiente) {
            System.out.println("[AddReservaController] Error: La embarcación no tiene suficiente capacidad para "
                    + reservaSolicitada.getNumSeats() + " plazas.");
            return "reserva/addReservaFailNOCAPACITYView";
        } else if (!estaDisponibleReserva || !estaDisponibleAlquiler) {
            System.out.println("[AddReservaController] Error: La embarcación no está disponible en la fecha: "
                    + reservaSolicitada.getDate());
            return "reserva/addReservaFailNOAVAILABLEView";
        } else if (!esSocioMayorDeEdad) {
            System.out.println("[AddReservaController] Error: El socio con id: " + reservaSolicitada.getUserId()
                    + " no es mayor de edad.");
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