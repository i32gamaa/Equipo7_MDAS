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
    private ModelAndView modelAndView = new ModelAndView();

    public ListEmbarcacionesController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.embarcacionRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/listEmbarcacion")
    public ModelAndView mostrarTodasLasEmbarcaciones() {
        this.modelAndView.setViewName("embarcacion/listEmbarcacionesView");
        
        List<Embarcacion> catalogoEmbarcaciones = embarcacionRepository.findAllEmbarcaciones();
        
        this.modelAndView.addObject("listOfEmbarcaciones", catalogoEmbarcaciones);
        
        return modelAndView;
    }
}