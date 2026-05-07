package es.uco.pw.demo.controller.reserva;

import es.uco.pw.demo.model.domain.Reserva;
import es.uco.pw.demo.model.repository.ReservaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

@Controller
public class ListReservasController {

    private final ReservaRepository reservaRepository;

    public ListReservasController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    // [CLEAN CODE - SEMANA 3: Do One Thing. Obtiene los datos y delega la responsabilidad visual]
    @GetMapping("/listReservas")
    public ModelAndView mostrarTodasLasReservas() {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        // Eliminamos la asignación temporal para devolver la lista directamente a la vista.
        return construirVistaListado(reservaRepository.findAllReservas());
    }

    // ====================================================================================================
    // MÉTODOS PRIVADOS EXTRAÍDOS
    // ====================================================================================================

    // [CLEAN CODE - SEMANA 3: Extracción de la carga del ModelAndView para mantener el mapeo limpio]
    private ModelAndView construirVistaListado(List<Reserva> reservas) {
        ModelAndView mav = new ModelAndView("reserva/listReservasView");
        mav.addObject("reservas", reservas);
        return mav;
    }
}