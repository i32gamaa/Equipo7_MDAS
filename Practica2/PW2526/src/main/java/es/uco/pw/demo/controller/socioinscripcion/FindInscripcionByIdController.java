package es.uco.pw.demo.controller.socioinscripcion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import es.uco.pw.demo.model.domain.Inscripcion;
import es.uco.pw.demo.model.repository.InscripcionRepository;

@Controller
public class FindInscripcionByIdController {

    private InscripcionRepository inscripcionRepository;

    public FindInscripcionByIdController(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.inscripcionRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/findInscripcionById")
    public ModelAndView mostrarFormularioBusqueda() {
        return new ModelAndView("socioinscripcion/findInscripcionByIdView");
    }

    @PostMapping("/findInscripcionById")
    public ModelAndView procesarBusquedaPorId(@RequestParam("id") int idBuscado) {
        Inscripcion inscripcionEncontrada = inscripcionRepository.findById(idBuscado);
        ModelAndView vistaResultados;

        if (inscripcionEncontrada != null) {
            vistaResultados = new ModelAndView("socioinscripcion/findInscripcionByIdSuccessView");
            vistaResultados.addObject("inscripcion", inscripcionEncontrada);
        } else {
            vistaResultados = new ModelAndView("socioinscripcion/findInscripcionByIdFailView");
        }

        return vistaResultados;
    }
}