package es.uco.pw.demo.controller.embarcacion;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FindEmbarcacionByRegistrartionController {
    
    private final EmbarcacionRepository embarcacionRepository;

    public FindEmbarcacionByRegistrartionController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
    }

    @GetMapping("/findEmbarcacionByRegistration")
    public ModelAndView mostrarFormularioBusquedaPorMatricula() {
        return construirVistaBusqueda();
    }

    // [CLEAN CODE - SEMANA 3: Se lee como una historia. Delega la lógica de búsqueda y carga de vista]
    @PostMapping("/findEmbarcacionByRegistration")
    public ModelAndView procesarBusquedaPorMatricula(@RequestParam("registrationNumber") String matricula) {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        return construirVistaResultado(embarcacionRepository.findByRegistration(matricula));
    }

    // [CLEAN CODE - SEMANA 3: Extracción de carga de ModelAndView]
    private ModelAndView construirVistaBusqueda() {
        ModelAndView mav = new ModelAndView("embarcacion/findEmbarcacionByRegistrationView");
        mav.addObject("embarcacionId", "12345678A");
        return mav;
    }

    // [CLEAN CODE - SEMANA 3: Do One Thing. Construye la vista basándose en el resultado del negocio]
    private ModelAndView construirVistaResultado(Embarcacion embarcacion) {
        if (embarcacion != null) {
            ModelAndView mav = new ModelAndView("embarcacion/findEmbarcacionByRegistrationSuccessView");
            mav.addObject("embarcacion", embarcacion);
            return mav;
        }
        return new ModelAndView("embarcacion/findEmbarcacionByRegistrationFailView");
    }
}