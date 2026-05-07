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

    public ListPatronesController(PatronRepository patronRepository) {
        this.patronRepository = patronRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.patronRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    // [CLEAN CODE - SEMANA 3: Do One Thing. Recupera los datos y delega el montaje visual a un privado]
    @GetMapping("/listPatrones")
    public ModelAndView mostrarTodosLosPatrones() {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        return construirVistaListado(patronRepository.findAllPatrones());
    }

    // ====================================================================================================
    // [CLEAN CODE - SEMANA 3: Extracción de carga de datos]
    // ====================================================================================================

    // [CLEAN CODE - SEMANA 3: Extrae la carga del ModelAndView]
    private ModelAndView construirVistaListado(List<Patron> patrones) {
        ModelAndView mav = new ModelAndView("patron/listPatronesView");
        mav.addObject("patrones", patrones);
        return mav;
    }
}