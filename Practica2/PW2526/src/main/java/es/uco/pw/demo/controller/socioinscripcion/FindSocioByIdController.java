package es.uco.pw.demo.controller.socioinscripcion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.repository.ISocioRepository;

@Controller
public class FindSocioByIdController {

    private final ISocioRepository socioRepository;

    public FindSocioByIdController(ISocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    @GetMapping("/findSocioById")
    public ModelAndView mostrarFormularioBusqueda() {
        return new ModelAndView("socioinscripcion/findSocioByIdView");
    }

    // [CLEAN CODE - SEMANA 3: Un solo nivel de abstracción]
    @PostMapping("/findSocioById")
    // [REFACTORIZACIÓN AUTOMÁTICA - VS Code Rename Symbol (F2): Se renombró de forma segura la variable genérica 'id' a 'dniBuscado' en todo su ámbito]
    public ModelAndView procesarBusquedaPorId(@RequestParam("id") String dniBuscado) {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        return construirVistaResultado(socioRepository.findById(dniBuscado));
    }

    // [CLEAN CODE - SEMANA 3: Extracción de lógica de construcción de respuesta]
    private ModelAndView construirVistaResultado(Socio socio) {
        if (socio != null) {
            ModelAndView mav = new ModelAndView("socioinscripcion/findSocioByIdSuccessView");
            mav.addObject("socio", socio);
            return mav;
        }
        return new ModelAndView("socioinscripcion/findSocioByIdFailView");
    }
}