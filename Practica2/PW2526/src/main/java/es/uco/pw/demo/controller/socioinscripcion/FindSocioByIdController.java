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

    private SocioRepository socioRepository;

    public FindSocioByIdController(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.socioRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/findSocioById")
    public ModelAndView mostrarFormularioBusqueda() {
        return new ModelAndView("socioinscripcion/findSocioByIdView");
    }

    @PostMapping("/findSocioById")
    public ModelAndView procesarBusquedaPorId(@RequestParam("id") String dniBuscado) {
        Socio socioEncontrado = socioRepository.findById(dniBuscado);
        ModelAndView vistaResultados;

        if (socioEncontrado != null) {
            vistaResultados = new ModelAndView("socioinscripcion/findSocioByIdSuccessView");
            vistaResultados.addObject("socio", socioEncontrado);
        } else {
            vistaResultados = new ModelAndView("socioinscripcion/findSocioByIdFailView");
        }

        return vistaResultados;
    }
}