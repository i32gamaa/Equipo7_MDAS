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

    InscripcionRepository inscripcionRepository;

    public FindInscripcionByIdController(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.inscripcionRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/findInscripcionById")
    public ModelAndView showForm() {
        return new ModelAndView("socioinscripcion/findInscripcionByIdView");
    }

    @PostMapping("/findInscripcionById")
    public ModelAndView findInscripcionById(@RequestParam("id") int id) {
        Inscripcion i = inscripcionRepository.findById(id);
        ModelAndView mav;

        if (i != null) {
            mav = new ModelAndView("socioinscripcion/findInscripcionByIdSuccessView");
            mav.addObject("inscripcion", i);
        } else {
            mav = new ModelAndView("socioinscripcion/findInscripcionByIdFailView");
        }

        return mav;
    }
}