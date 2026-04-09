package es.uco.pw.demo.controller.patron;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import es.uco.pw.demo.model.repository.PatronRepository;

@Controller
public class AssignPatronToBoatController {

    private PatronRepository patronRepository;
    private EmbarcacionRepository embarcacionRepository;
    private ModelAndView modelAndView = new ModelAndView();

    public AssignPatronToBoatController(PatronRepository patronRepository, EmbarcacionRepository embarcacionRepository) {
        this.patronRepository = patronRepository;
        this.embarcacionRepository = embarcacionRepository;
    }

    @GetMapping("/assignPatronToBoat")
    public ModelAndView showAssignForm() {
        this.modelAndView.setViewName("patron/assignPatronToBoatView");
        this.modelAndView.addObject("embarcaciones", embarcacionRepository.findAllEmbarcaciones());
        this.modelAndView.addObject("patrones", patronRepository.findAllPatrones());
        return modelAndView;
    }

    @PostMapping("/assignPatronToBoat")
    public String assignPatronToBoat(@RequestParam String registrationNumber, @RequestParam String patronId) {

        System.out.println("[AssignPatronToBoatController] Asignando patrón " + patronId + " a embarcación " + registrationNumber);

        boolean success = patronRepository.assignPatronToBoat(patronId, registrationNumber);
        String nextPage;

        if (success) {
            nextPage = "patron/assignPatronToBoatSuccessView";
        } else {
            nextPage = "patron/assignPatronToBoatFailView";
        }

        return nextPage;
    }

    // Nuevo método para desasignar patrón
    @PostMapping("/unassignPatronFromBoat")
    public String unassignPatronFromBoat(@RequestParam String registrationNumber) {

        System.out.println("[AssignPatronToBoatController] Desasignando patrón de la embarcación " + registrationNumber);

        boolean success = patronRepository.unassignPatronFromBoat(registrationNumber);
        String nextPage;

        if (success) {
            nextPage = "patron/unassignPatronSuccessView";
        } else {
            nextPage = "patron/unassignPatronFailView";
        }

        return nextPage;
    }
}