package es.uco.pw.demo.controller.socioinscripcion;

import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.repository.SocioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

@Controller
public class ListSociosController {

    private final SocioRepository socioRepository;
    private ModelAndView modelAndView = new ModelAndView();

    public ListSociosController(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.socioRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/listSocios")
    public ModelAndView listSocios() {
        this.modelAndView.setViewName("socioinscripcion/listSociosView");
        List<Socio> listOfSocios = socioRepository.findAllSocios();
        this.modelAndView.addObject("socios", listOfSocios);
        return modelAndView;
    }

}
