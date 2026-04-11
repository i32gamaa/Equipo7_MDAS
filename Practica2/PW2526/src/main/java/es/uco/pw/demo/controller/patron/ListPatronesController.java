package es.uco.pw.demo.controller.patron;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import es.uco.pw.demo.model.repository.PatronRepository;
import es.uco.pw.demo.model.domain.Patron;

import java.util.List;

@Controller
public class ListPatronesController {

    private final PatronRepository patronRepository;
    private ModelAndView modelAndView = new ModelAndView();

    public ListPatronesController(PatronRepository patronRepository) {
        this.patronRepository = patronRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.patronRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/listPatrones")
    public ModelAndView mostrarTodosLosPatrones() {
        this.modelAndView.setViewName("patron/listPatronesView");
        
        List<Patron> patronesRegistrados = patronRepository.findAllPatrones();
        
        this.modelAndView.addObject("patrones", patronesRegistrados);
        return modelAndView;
    }
}