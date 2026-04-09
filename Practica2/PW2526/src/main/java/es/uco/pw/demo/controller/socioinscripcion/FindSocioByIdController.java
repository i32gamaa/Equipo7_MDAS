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

    SocioRepository socioRepository;

    public FindSocioByIdController(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.socioRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/findSocioById")
    public ModelAndView showForm() {
        return new ModelAndView("socioinscripcion/findSocioByIdView");
    }

    @PostMapping("/findSocioById")
    public ModelAndView findSocioById(@RequestParam("id") String id) {
        Socio s = socioRepository.findById(id);
        ModelAndView mav;

        if (s != null) {
            mav = new ModelAndView("socioinscripcion/findSocioByIdSuccessView");
            mav.addObject("socio", s);
        } else {
            mav = new ModelAndView("socioinscripcion/findSocioByIdFailView");
        }

        return mav;
    }
}