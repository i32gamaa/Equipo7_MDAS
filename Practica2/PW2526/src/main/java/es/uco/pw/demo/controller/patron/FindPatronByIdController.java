package es.uco.pw.demo.controller.patron;

import es.uco.pw.demo.model.domain.Patron;
import es.uco.pw.demo.model.repository.PatronRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FindPatronByIdController {
    
    private final PatronRepository patronRepository;

    public FindPatronByIdController(PatronRepository patronRepository) {
        this.patronRepository = patronRepository;
    }

    // [CLEAN CODE - SEMANA 3: Delega la inicialización del ModelAndView]
    @GetMapping("/findPatronById")
    public ModelAndView mostrarFormularioBusqueda() {
        return new ModelAndView("patron/findPatronByIdView");
    }

    // [CLEAN CODE - SEMANA 3: Se lee como una historia (Validar entrada -> Buscar -> Mostrar resultado)]
    @PostMapping("/findPatronById")
    public ModelAndView procesarBusquedaPorId(@RequestParam("idNumber") String patronId) {
        
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Guard Clauses]
        if (esEntradaInvalida(patronId)) {
            return construirVistaErrorValidacion();
        }

        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        return construirVistaResultado(patronRepository.findById(patronId), patronId);
    }

    // ====================================================================================================
    // [CLEAN CODE - SEMANA 3: Extracción de lógica a métodos privados]
    // ====================================================================================================

    // [CLEAN CODE - SEMANA 3: Extracción de lógica de validación de RequestParam]
    private boolean esEntradaInvalida(String entrada) {
        return entrada == null || entrada.trim().isEmpty();
    }

    // [CLEAN CODE - SEMANA 3: Extracción de carga de ModelAndView (Error)]
    private ModelAndView construirVistaErrorValidacion() {
        ModelAndView mav = new ModelAndView("patron/findPatronByIdFailView");
        mav.addObject("errorMessage", "El DNI no puede estar vacío");
        return mav;
    }

    // [CLEAN CODE - SEMANA 3: Extracción de carga de ModelAndView (Resultado)]
    private ModelAndView construirVistaResultado(Patron patron, String idBuscado) {
        ModelAndView mav = new ModelAndView();
        if (patron != null) {
            mav.setViewName("patron/findPatronByIdSuccessView");
            mav.addObject("patron", patron);
        } else {
            mav.setViewName("patron/findPatronByIdFailView");
            mav.addObject("errorMessage", "No se encontró ningún patrón con DNI: " + idBuscado);
        }
        return mav;
    }
}