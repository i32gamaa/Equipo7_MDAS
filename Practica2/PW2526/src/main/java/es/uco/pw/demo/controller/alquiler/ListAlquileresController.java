package es.uco.pw.demo.controller.alquiler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;
import es.uco.pw.demo.model.repository.AlquilerRepository;
import es.uco.pw.demo.model.domain.Alquiler;

@Controller
public class ListAlquileresController {

    private final AlquilerRepository alquilerRepository;

    public ListAlquileresController(AlquilerRepository alquilerRepository) {
        this.alquilerRepository = alquilerRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.alquilerRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    // [CLEAN CODE - SEMANA 3: Do One Thing. Recupera los datos y delega la construcción de la vista a un método privado]
    @GetMapping("/listAlquileres")
    public ModelAndView mostrarAlquileresActivosYFuturos() {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        return construirVistaListado(alquilerRepository.findCurrentAndFutureAlquileres());
    }

    // [CLEAN CODE - SEMANA 3: Extracción de carga de ModelAndView]
    private ModelAndView construirVistaListado(List<Alquiler> alquileres) {
        ModelAndView modelAndView = new ModelAndView("alquiler/listAlquileresView");
        modelAndView.addObject("alquileres", alquileres);
        return modelAndView;
    }
}