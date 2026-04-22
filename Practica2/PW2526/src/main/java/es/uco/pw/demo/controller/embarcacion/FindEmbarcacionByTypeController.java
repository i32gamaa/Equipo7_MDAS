package es.uco.pw.demo.controller.embarcacion;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.EmbarcacionType;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FindEmbarcacionByTypeController {
    
    private EmbarcacionRepository embarcacionRepository;
    private ModelAndView modelAndView = new ModelAndView();

    public FindEmbarcacionByTypeController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.embarcacionRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/findEmbarcacionByType")
    public ModelAndView mostrarFormularioBusquedaPorTipo() {
        modelAndView.setViewName("embarcacion/findEmbarcacionByTypeView");
        modelAndView.addObject("types", EmbarcacionType.values()); 
        return modelAndView;
    }

    @PostMapping("/findEmbarcacionByType")
    public ModelAndView procesarBusquedaPorTipo(@RequestParam("type") EmbarcacionType tipoSeleccionado) {
        System.out.println("[FindEmbarcacionByTypeController] Tipo recibido: " + tipoSeleccionado);

        List<Embarcacion> embarcacionesEncontradas = embarcacionRepository.findByType(tipoSeleccionado);

        if (embarcacionesEncontradas != null && !embarcacionesEncontradas.isEmpty()) {
            System.out.println("[FindEmbarcacionByTypeController] Encontradas " + embarcacionesEncontradas.size() + " embarcaciones");
            modelAndView.setViewName("embarcacion/findEmbarcacionByTypeSuccessView");
            modelAndView.addObject("embarcaciones", embarcacionesEncontradas);
            modelAndView.addObject("type", tipoSeleccionado); 
        } else {
            modelAndView.setViewName("embarcacion/findEmbarcacionByTypeFailView");
            modelAndView.addObject("type", tipoSeleccionado);
        }

        return modelAndView;
    }
}