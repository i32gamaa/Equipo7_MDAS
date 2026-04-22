package es.uco.pw.demo.controller.embarcacion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.EmbarcacionType;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.List;

@Controller
public class AddEmbarcacionController {

    private EmbarcacionRepository embarcacionRepository;
    private ModelAndView modelAndView = new ModelAndView();

    public AddEmbarcacionController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.embarcacionRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/addEmbarcacion")
    public ModelAndView mostrarFormularioRegistro() {
        this.modelAndView.setViewName("embarcacion/addEmbarcacionView");
        this.modelAndView.addObject("embarcacion", new Embarcacion());
        this.modelAndView.addObject("types", EmbarcacionType.values());
        return modelAndView;
    }

    @PostMapping("/addEmbarcacion")
    public String procesarNuevaEmbarcacion(@ModelAttribute Embarcacion nuevaEmbarcacion, SessionStatus estadoSesion) {
        if (nuevaEmbarcacion.getPatronId() == null || nuevaEmbarcacion.getPatronId().isEmpty()) {
            nuevaEmbarcacion.setPatronId(null);
        }
        
        System.out.println("[AddEmbarcacionController] Info recibida: matricula=" + nuevaEmbarcacion.getRegistrationNumber() +
                " plazas=" + nuevaEmbarcacion.getNumberOfSeats() +
                " patronId=" + nuevaEmbarcacion.getPatronId());
        
        boolean existeNombreDuplicado = comprobarNombreDuplicado(nuevaEmbarcacion.getName());
        if (existeNombreDuplicado) {
            return "embarcacion/addEmbarcacionFailView";
        }
                
        boolean registroCompletado = embarcacionRepository.addEmbarcacion(nuevaEmbarcacion);
        String vistaDestino;

        if (registroCompletado) {
            vistaDestino = "embarcacion/addEmbarcacionSuccessView";
        } else {
            vistaDestino = "embarcacion/addEmbarcacionFailView";
        }

        estadoSesion.setComplete();
        return vistaDestino;
    }

    private boolean comprobarNombreDuplicado(String nombreAComprobar) {
        try {
            List<Embarcacion> inventarioEmbarcaciones = embarcacionRepository.findAllEmbarcaciones();
            if (inventarioEmbarcaciones != null) {
                for (Embarcacion embarcacionRegistrada : inventarioEmbarcaciones) {
                    if (embarcacionRegistrada.getName() != null && embarcacionRegistrada.getName().equalsIgnoreCase(nombreAComprobar)) {
                        return true; 
                    }
                }
            }
            return false; 
        } catch (Exception e) {
            return true; 
        }
    }
}