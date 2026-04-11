package es.uco.pw.demo.controller.embarcacion;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FindEmbarcacionByRegistrartionController {
    
    private EmbarcacionRepository embarcacionRepository;
    private ModelAndView modelAndView = new ModelAndView();

    public FindEmbarcacionByRegistrartionController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.embarcacionRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/findEmbarcacionByRegistration")
    public ModelAndView mostrarFormularioBusquedaPorMatricula() {
        modelAndView.setViewName("embarcacion/findEmbarcacionByRegistrationView");
        modelAndView.addObject("embarcacionId", "12345678A");
        return modelAndView;
    }

    @PostMapping("/findEmbarcacionByRegistration")
    public ModelAndView procesarBusquedaPorMatricula(@RequestParam("registrationNumber") String matriculaBuscada) {
        System.out.println("[FindEmbarcacionByRegistrationController] Matricula recibida: " + matriculaBuscada);

        Embarcacion embarcacionEncontrada = embarcacionRepository.findByRegistration(matriculaBuscada);

        if (embarcacionEncontrada != null) {
            System.out.println("[FindEmbarcacionByRegistrationController] Embarcacion encontrada: matricula=" + embarcacionEncontrada.getRegistrationNumber());
            modelAndView.setViewName("embarcacion/findEmbarcacionByRegistrationSuccessView");
            modelAndView.addObject("embarcacion", embarcacionEncontrada);
        } else {
            modelAndView.setViewName("embarcacion/findEmbarcacionByRegistrationFailView");
        }

        return modelAndView;
    }
}