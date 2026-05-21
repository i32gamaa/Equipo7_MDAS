package es.uco.pw.demo.controller.socioinscripcion;

import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.repository.ISocioRepository;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

@Controller
public class ListSociosController {

    private final ISocioRepository socioRepository;

    public ListSociosController(ISocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    // [CLEAN CODE - SEMANA 3: Nivel único de abstracción]
    @GetMapping("/listSocios")
    public ModelAndView mostrarTodosLosSocios() {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        return construirVistaListado(socioRepository.findAllSocios());
    }

    private ModelAndView construirVistaListado(List<Socio> socios) {
        ModelAndView mav = new ModelAndView("socioinscripcion/listSociosView");
        mav.addObject("socios", socios);
        return mav;
    }
}