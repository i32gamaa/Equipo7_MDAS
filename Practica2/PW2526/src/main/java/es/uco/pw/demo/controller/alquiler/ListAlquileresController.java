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
    public ModelAndView mostrarAlquileresActivosYFuturos() {
        System.out.println("=== ListAlquileresController LLAMADO ===");
        
        this.modelAndView.setViewName("alquiler/listAlquileresView");
        List<Alquiler> alquileresVigentes = alquilerRepository.findCurrentAndFutureAlquileres();
        
        System.out.println("Resultado del repositorio: " + (alquileresVigentes != null ? alquileresVigentes.size() + " elementos" : "NULL"));
        
        if (alquileresVigentes != null && !alquileresVigentes.isEmpty()) {
            System.out.println("Detalle de alquileres encontrados:");
            for (int i = 0; i < alquileresVigentes.size(); i++) {
                Alquiler alquilerEncontrado = alquileresVigentes.get(i);
                System.out.println("  [" + i + "] " + alquilerEncontrado);
            }
        } else {
            System.out.println("No se encontraron alquileres");
        }
        
        this.modelAndView.addObject("alquileres", alquileresVigentes);
        
        System.out.println("=== FIN ListAlquileresController ===");
        return modelAndView;
    }
}