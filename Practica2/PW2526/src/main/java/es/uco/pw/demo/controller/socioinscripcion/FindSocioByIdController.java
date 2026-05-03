package es.uco.pw.demo.controller.socioinscripcion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.repository.SocioRepository;

@Controller
public class FindSocioByIdController {

    private final SocioRepository socioRepository;

    public FindSocioByIdController(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    @GetMapping("/findSocioById")
    public ModelAndView mostrarFormularioBusqueda() {
        return new ModelAndView("socioinscripcion/findSocioByIdView");
    }

    // [CLEAN CODE - SEMANA 3: Un solo nivel de abstracción]
    @PostMapping("/findSocioById")
    public ModelAndView procesarBusquedaPorId(@RequestParam("id") String dniBuscado) {
        Socio socioEncontrado = socioRepository.findById(dniBuscado);
        return construirVistaResultado(socioEncontrado);
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