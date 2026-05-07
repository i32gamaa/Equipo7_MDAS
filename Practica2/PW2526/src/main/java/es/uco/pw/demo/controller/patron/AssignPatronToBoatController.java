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

    public AssignPatronToBoatController(PatronRepository patronRepository, EmbarcacionRepository embarcacionRepository) {
        this.patronRepository = patronRepository;
        this.embarcacionRepository = embarcacionRepository;
    }

    // [CLEAN CODE - SEMANA 3: Nivel único de abstracción. Delega la complejidad de cargar listas]
    @GetMapping("/assignPatronToBoat")
    public ModelAndView mostrarFormularioAsignacion() {
        return construirVistaAsignacion();
    }

    // [CLEAN CODE - SEMANA 3: Do One Thing. Solicita la asignación y delega la respuesta]
    @PostMapping("/assignPatronToBoat")
    public String procesarAsignacionPatron(@RequestParam String registrationNumber, @RequestParam String patronId) {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        return determinarVistaAsignacion(patronRepository.assignPatronToBoat(patronId, registrationNumber));
    }

    // [CLEAN CODE - SEMANA 3: Do One Thing. Solicita la desasignación y delega la respuesta]
    @PostMapping("/unassignPatronFromBoat")
    public String procesarDesasignacionPatron(@RequestParam String registrationNumber) {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        return determinarVistaDesasignacion(patronRepository.unassignPatronFromBoat(registrationNumber));
    }

    // ====================================================================================================
    // [CLEAN CODE - SEMANA 3: Extracción a métodos privados]
    // ====================================================================================================

    // [CLEAN CODE - SEMANA 3: Extracción de carga de datos para el ModelAndView]
    private ModelAndView construirVistaAsignacion() {
        ModelAndView mav = new ModelAndView("patron/assignPatronToBoatView");
        mav.addObject("embarcaciones", embarcacionRepository.findAllEmbarcaciones());
        mav.addObject("patrones", patronRepository.findAllPatrones());
        return mav;
    }

    // [CLEAN CODE - SEMANA 3: DRY, gestión de vistas separada de la lógica]
    private String determinarVistaAsignacion(boolean exito) {
        return exito ? "patron/assignPatronToBoatSuccessView" : "patron/assignPatronToBoatFailView";
    }

    // [CLEAN CODE - SEMANA 3: DRY, gestión de vistas separada de la lógica]
    private String determinarVistaDesasignacion(boolean exito) {
        return exito ? "patron/unassignPatronSuccessView" : "patron/unassignPatronFailView";
    }
}