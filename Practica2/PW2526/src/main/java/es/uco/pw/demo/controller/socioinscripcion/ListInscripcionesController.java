package es.uco.pw.demo.controller.socioinscripcion;

import es.uco.pw.demo.model.domain.Inscripcion;
import es.uco.pw.demo.model.repository.InscripcionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

@Controller
public class ListInscripcionesController {

    private final InscripcionRepository inscripcionRepository;

    public ListInscripcionesController(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
    }

    // [CLEAN CODE - SEMANA 3: Do One Thing. Delega la carga de datos visuales]
    @GetMapping("/listInscripciones")
    public ModelAndView mostrarTodasLasInscripciones() {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        return construirVistaListado(inscripcionRepository.findAllInscripciones());
    }

    private ModelAndView construirVistaListado(List<Inscripcion> inscripciones) {
        ModelAndView mav = new ModelAndView("socioinscripcion/listInscripcionesView");
        mav.addObject("inscripciones", inscripciones);
        return mav;
    }
}