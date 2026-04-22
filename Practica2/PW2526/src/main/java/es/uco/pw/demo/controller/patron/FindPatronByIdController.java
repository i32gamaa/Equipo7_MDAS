package es.uco.pw.demo.controller.patron;

import es.uco.pw.demo.model.domain.Patron;
import es.uco.pw.demo.model.repository.PatronRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FindPatronByIdController {
    
    private final PatronRepository patronRepository;
    private ModelAndView modelAndView = new ModelAndView();

    public FindPatronByIdController(PatronRepository patronRepository) {
        this.patronRepository = patronRepository;
    }

    @GetMapping("/findPatronById")
    public ModelAndView mostrarFormularioBusqueda() {
        modelAndView.setViewName("patron/findPatronByIdView");
        return modelAndView;
    }

    @PostMapping("/findPatronById")
    public ModelAndView procesarBusquedaPorId(@RequestParam("idNumber") String dniBuscado) {
        System.out.println("[FindPatronByIdController] Searching for patron with ID: " + dniBuscado);

        if (dniBuscado == null || dniBuscado.trim().isEmpty()) {
            modelAndView.setViewName("patron/findPatronByIdFailView");
            modelAndView.addObject("errorMessage", "El DNI no puede estar vacío");
            return modelAndView;
        }

        Patron patronEncontrado = patronRepository.findById(dniBuscado);

        if (patronEncontrado != null) {
            System.out.println("[FindPatronByIdController] Found patron: " + patronEncontrado);
            modelAndView.setViewName("patron/findPatronByIdSuccessView");
            modelAndView.addObject("patron", patronEncontrado);
        } else {
            System.out.println("[FindPatronByIdController] Patron not found with ID: " + dniBuscado);
            modelAndView.setViewName("patron/findPatronByIdFailView");
            modelAndView.addObject("errorMessage", "No se encontró ningún patrón con DNI: " + dniBuscado);
        }

        return modelAndView;
    }
}