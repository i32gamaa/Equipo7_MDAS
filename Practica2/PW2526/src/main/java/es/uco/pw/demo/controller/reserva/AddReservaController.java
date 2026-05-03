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

    private final ReservaRepository reservaRepository;

    public AddReservaController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    // [CLEAN CODE - SEMANA 3: Un solo nivel de abstracción. Delega la creación de la vista a una función privada]
    @GetMapping("/addReserva")
    public ModelAndView mostrarFormularioRegistro() {
        return construirVistaFormulario();
    }

    // [CLEAN CODE - SEMANA 3: Función principal que se lee como una historia. 
    // Separa claramente la validación de negocio, la persistencia y la respuesta visual]
    @PostMapping("/addReserva")
    public String procesarNuevaReserva(@ModelAttribute("newReserva") Reserva reservaSolicitada, SessionStatus estadoSesion) {
        
        String vistaError = validarRequisitosReserva(reservaSolicitada);
        if (vistaError != null) {
            return vistaError;
        }

        int resultadoId = reservaRepository.addReserva(reservaSolicitada);
        estadoSesion.setComplete();
        
        return determinarVistaResultado(resultadoId);
    }

    // ====================================================================================================
    // MÉTODOS PRIVADOS EXTRAÍDOS (Stepdown Rule)
    // ====================================================================================================

    // [CLEAN CODE - SEMANA 3: Do One Thing. Encapsula la inicialización del ModelAndView del formulario]
    private ModelAndView construirVistaFormulario() {
        ModelAndView mav = new ModelAndView("reserva/addReservaView");
        mav.addObject("newReserva", new Reserva());
        return mav;
    }

    // [CLEAN CODE - SEMANA 3: Extrae validaciones complejas. Sigue la regla de ocultar detalles técnicos del mapeo principal]
    private String validarRequisitosReserva(Reserva reserva) {
        if (!reservaRepository.patronAssigned(reserva.getRegistrationNumber())) {
            return "reserva/addReservaFailNOPATRONView";
        } 
        if (!reservaRepository.hasCapacity(reserva.getRegistrationNumber(), reserva.getNumberOfSeats())) {
            return "reserva/addReservaFailNOCAPACITYView";
        } 
        if (!estaDisponibleParaFecha(reserva)) {
            return "reserva/addReservaFailNOAVAILABLEView";
        } 
        if (!reservaRepository.isAdult(reserva.getUserId())) {
            return "reserva/addReservaFailNOADULTView";
        }
        return null;
    }

    // [CLEAN CODE - SEMANA 3: Función de conveniencia para agrupar lógica de disponibilidad relacionada]
    private boolean estaDisponibleParaFecha(Reserva reserva) {
        return reservaRepository.isAvailable(reserva.getRegistrationNumber(), reserva.getReservationDate()) &&
               reservaRepository.isAvailableInAlquiler(reserva.getRegistrationNumber(), reserva.getReservationDate());
    }

    // [CLEAN CODE - SEMANA 3: Separa la lógica de decisión de navegación del proceso de guardado]
    private String determinarVistaResultado(int resultadoId) {
        return (resultadoId != -1) ? "reserva/addReservaSuccessView" : "reserva/addReservaFailView";
    }
}