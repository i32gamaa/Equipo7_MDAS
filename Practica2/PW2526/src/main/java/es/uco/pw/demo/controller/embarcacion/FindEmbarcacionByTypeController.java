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
    EmbarcacionRepository embarcacionRepository;
    private ModelAndView modelAndView = new ModelAndView();

    public FindEmbarcacionByTypeController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.embarcacionRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/findEmbarcacionByType")
    public ModelAndView getFindEmbarcacionForm() {
        modelAndView.setViewName("embarcacion/findEmbarcacionByTypeView");
        modelAndView.addObject("types", EmbarcacionType.values()); // Añadir los tipos al modelo
        return modelAndView;
    }

    @PostMapping("/findEmbarcacionByType")
    public ModelAndView findEmbarcacionByType(@RequestParam("type") EmbarcacionType type) {
        System.out.println("[FindEmbarcacionByTypeController] Received type: " + type);

        List<Embarcacion> embarcaciones = embarcacionRepository.findByType(type);

        if (embarcaciones != null && !embarcaciones.isEmpty()) {
            System.out.println("[FindEmbarcacionByTypeController] Found " + embarcaciones.size() + " embarcaciones");
            modelAndView.setViewName("embarcacion/findEmbarcacionByTypeSuccessView");
            modelAndView.addObject("embarcaciones", embarcaciones);
            modelAndView.addObject("type", type); 
        } else {
            modelAndView.setViewName("embarcacion/findEmbarcacionByTypeFailView");
            modelAndView.addObject("type", type);
        }

        return modelAndView;
    }
}