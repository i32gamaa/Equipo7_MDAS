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
    private ModelAndView modelAndView = new ModelAndView();

    public ListInscripcionesController(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.inscripcionRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/listInscripciones")
    public ModelAndView mostrarTodasLasInscripciones() {
        this.modelAndView.setViewName("socioinscripcion/listInscripcionesView");
        List<Inscripcion> inscripcionesRegistradas = inscripcionRepository.findAllInscripciones();
        this.modelAndView.addObject("inscripciones", inscripcionesRegistradas);
        return modelAndView;
    }
}