package es.uco.pw.demo.controller.socioinscripcion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import es.uco.pw.demo.model.domain.Inscripcion;
import es.uco.pw.demo.model.repository.InscripcionRepository;

@Controller
public class FindInscripcionByIdController {

    private final InscripcionRepository inscripcionRepository;

    public FindInscripcionByIdController(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
    }

    @GetMapping("/findInscripcionById")
    public ModelAndView mostrarFormularioBusqueda() {
        return new ModelAndView("socioinscripcion/findInscripcionByIdView");
    }

    // [CLEAN CODE - SEMANA 3: Estructura de historia: buscar -> responder]
    @PostMapping("/findInscripcionById")
    public ModelAndView procesarBusquedaPorId(@RequestParam("id") int idBuscado) {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        return construirVistaResultado(inscripcionRepository.findById(idBuscado));
    }

    // [CLEAN CODE - SEMANA 3: Do One Thing. Encapsula la lógica de vista]
    private ModelAndView construirVistaResultado(Inscripcion inscripcion) {
        if (inscripcion != null) {
            ModelAndView mav = new ModelAndView("socioinscripcion/findInscripcionByIdSuccessView");
            mav.addObject("inscripcion", inscripcion);
            return mav;
        }
        return new ModelAndView("socioinscripcion/findInscripcionByIdFailView");
    }
}