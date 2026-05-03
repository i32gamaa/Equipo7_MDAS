package es.uco.pw.demo.controller.embarcacion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import es.uco.pw.demo.model.domain.Embarcacion;

@Controller
public class ListEmbarcacionesController {

    private final EmbarcacionRepository embarcacionRepository;

    public ListEmbarcacionesController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
    }

    // [CLEAN CODE - SEMANA 3: Función pura y simple. Delega la carga de datos a privados]
    @GetMapping("/listEmbarcacion")
    public ModelAndView mostrarTodasLasEmbarcaciones() {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        return construirVistaCatalogo(embarcacionRepository.findAllEmbarcaciones());
    }

    // [CLEAN CODE - SEMANA 3: Extracción de carga de ModelAndView para seguir DRY]
    private ModelAndView construirVistaCatalogo(List<Embarcacion> embarcaciones) {
        ModelAndView mav = new ModelAndView("embarcacion/listEmbarcacionesView");
        mav.addObject("listOfEmbarcaciones", embarcaciones);
        return mav;
    }
}