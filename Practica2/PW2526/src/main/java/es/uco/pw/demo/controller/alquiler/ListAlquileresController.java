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
    private ModelAndView modelAndView = new ModelAndView();

    public ListAlquileresController(AlquilerRepository alquilerRepository) {
        this.alquilerRepository = alquilerRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.alquilerRepository.setSQLQueriesFileName(sqlQueriesFileName);
        System.out.println("=== ListAlquileresController INICIALIZADO ===");
    }

    @GetMapping("/listAlquileres")
    public ModelAndView listCurrentAndFutureAlquileres() {
        System.out.println("=== ListAlquileresController.listCurrentAndFutureAlquileres() LLAMADO ===");
        
        this.modelAndView.setViewName("alquiler/listAlquileresView");
        
        // Obtener solo alquileres actuales y futuros
        List<Alquiler> listOfAlquileres = alquilerRepository.findCurrentAndFutureAlquileres();
        
        System.out.println("Resultado del repositorio: " + (listOfAlquileres != null ? listOfAlquileres.size() + " elementos" : "NULL"));
        
        if (listOfAlquileres != null && !listOfAlquileres.isEmpty()) {
            System.out.println("Detalle de alquileres actuales y futuros encontrados:");
            for (int i = 0; i < listOfAlquileres.size(); i++) {
                Alquiler a = listOfAlquileres.get(i);
                System.out.println("  [" + i + "] " + a);
            }
        } else {
            System.out.println("No se encontraron alquileres actuales o futuros");
        }
        
        this.modelAndView.addObject("alquileres", listOfAlquileres);
        
        System.out.println("=== FIN ListAlquileresController ===");
        return modelAndView;
    }
}