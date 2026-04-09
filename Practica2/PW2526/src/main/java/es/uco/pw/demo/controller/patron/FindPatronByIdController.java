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
        // El repositorio ya carga las queries en su constructor
    }

    @GetMapping("/findPatronById")
    public ModelAndView getFindPatronForm() {
        modelAndView.setViewName("patron/findPatronByIdView");
        return modelAndView;
    }

    @PostMapping("/findPatronById")
    public ModelAndView findPatronById(@RequestParam("idNumber") String patronId) {
        
        System.out.println("[FindPatronByIdController] Searching for patron with ID: " + patronId);

        // Validar entrada
        if (patronId == null || patronId.trim().isEmpty()) {
            modelAndView.setViewName("patron/findPatronByIdFailView");
            modelAndView.addObject("errorMessage", "El DNI no puede estar vacío");
            return modelAndView;
        }

        // Buscar el patrón
        Patron patron = patronRepository.findById(patronId);

        if (patron != null) {
            System.out.println("[FindPatronByIdController] Found patron: " + patron);
            modelAndView.setViewName("patron/findPatronByIdSuccessView");
            modelAndView.addObject("patron", patron);
        } else {
            System.out.println("[FindPatronByIdController] Patron not found with ID: " + patronId);
            modelAndView.setViewName("patron/findPatronByIdFailView");
            modelAndView.addObject("errorMessage", "No se encontró ningún patrón con DNI: " + patronId);
        }

        return modelAndView;
    }
}