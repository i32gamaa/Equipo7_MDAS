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
    
    private final EmbarcacionRepository embarcacionRepository;

    public FindEmbarcacionByTypeController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
    }

    @GetMapping("/findEmbarcacionByType")
    public ModelAndView mostrarFormularioBusquedaPorTipo() {
        return construirVistaFormularioTipo();
    }

    // [CLEAN CODE - SEMANA 3: Nivel único de abstracción. Delega la complejidad técnica]
    @PostMapping("/findEmbarcacionByType")
    public ModelAndView procesarBusquedaPorTipo(@RequestParam("type") EmbarcacionType tipoSeleccionado) {
        List<Embarcacion> embarcacionesEncontradas = embarcacionRepository.findByType(tipoSeleccionado);
        return construirVistaListadoPorTipo(embarcacionesEncontradas, tipoSeleccionado);
    }

    // [CLEAN CODE - SEMANA 3: Extracción de ModelAndView para mantener métodos pequeños]
    private ModelAndView construirVistaFormularioTipo() {
        ModelAndView mav = new ModelAndView("embarcacion/findEmbarcacionByTypeView");
        mav.addObject("types", EmbarcacionType.values());
        return mav;
    }

    // [CLEAN CODE - SEMANA 3: Do One Thing. Gestiona el flujo de éxito/fallo de la vista]
    private ModelAndView construirVistaListadoPorTipo(List<Embarcacion> lista, EmbarcacionType tipo) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("type", tipo);
        
        if (lista != null && !lista.isEmpty()) {
            mav.setViewName("embarcacion/findEmbarcacionByTypeSuccessView");
            mav.addObject("embarcaciones", lista);
        } else {
            mav.setViewName("embarcacion/findEmbarcacionByTypeFailView");
        }
        return mav;
    }
}