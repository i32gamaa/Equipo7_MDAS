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
    public ModelAndView mostrarFormularioAsignacion() {
        this.modelAndView.setViewName("patron/assignPatronToBoatView");
        this.modelAndView.addObject("embarcaciones", embarcacionRepository.findAllEmbarcaciones());
        this.modelAndView.addObject("patrones", patronRepository.findAllPatrones());
        return modelAndView;
    }

    @PostMapping("/assignPatronToBoat")
    public String procesarAsignacionPatron(@RequestParam String registrationNumber, @RequestParam String patronId) {
        System.out.println("[AssignPatronToBoatController] Asignando patrón " + patronId + " a embarcación " + registrationNumber);

        boolean asignacionExitosa = patronRepository.assignPatronToBoat(patronId, registrationNumber);
        String vistaDestino;

        if (asignacionExitosa) {
            vistaDestino = "patron/assignPatronToBoatSuccessView";
        } else {
            vistaDestino = "patron/assignPatronToBoatFailView";
        }

        return vistaDestino;
    }

    @PostMapping("/unassignPatronFromBoat")
    public String procesarDesasignacionPatron(@RequestParam String registrationNumber) {
        System.out.println("[AssignPatronToBoatController] Desasignando patrón de la embarcación " + registrationNumber);

        boolean desasignacionExitosa = patronRepository.unassignPatronFromBoat(registrationNumber);
        String vistaDestino;

        if (desasignacionExitosa) {
            vistaDestino = "patron/unassignPatronSuccessView";
        } else {
            vistaDestino = "patron/unassignPatronFailView";
        }

        return vistaDestino;
    }
}