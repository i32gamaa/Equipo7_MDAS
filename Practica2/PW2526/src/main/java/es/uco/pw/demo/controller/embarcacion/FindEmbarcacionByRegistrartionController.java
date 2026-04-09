package es.uco.pw.demo.controller.embarcacion;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import ch.qos.logback.core.model.Model;

@Controller
public class FindEmbarcacionByRegistrartionController {
    EmbarcacionRepository embarcacionRepository;
    private ModelAndView modelAndView = new ModelAndView();

    public FindEmbarcacionByRegistrartionController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.embarcacionRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/findEmbarcacionByRegistration")
    public ModelAndView getFindEmbarcacionForm() {
        modelAndView.setViewName("embarcacion/findEmbarcacionByRegistrationView");
        modelAndView.addObject("embarcacionId", "12345678A");
        return modelAndView;
    }

    @PostMapping("/findEmbarcacionByRegistration")
    public ModelAndView findEmbarcacionByRegistration(@RequestParam("registrationNumber") String registrationNumber) {
        System.out.println("[FindEmbarcacionByRegistrationController] Received registrationNumber: " + registrationNumber);

        Embarcacion embarcacion = embarcacionRepository.findByRegistration(registrationNumber);

        if (embarcacion != null) {
            System.out.println("[FindEmbarcacionByRegistrationController] Found embarcacion: id=" + embarcacion.getRegistrationNumber());
            modelAndView.setViewName("embarcacion/findEmbarcacionByRegistrationSuccessView");
            modelAndView.addObject("embarcacion", embarcacion);
        } else {
            modelAndView.setViewName("embarcacion/findEmbarcacionByRegistrationFailView");
        }

        return modelAndView;
    }
}
