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
    public ModelAndView getAddEmbarcacionView() {
        this.modelAndView.setViewName("embarcacion/addEmbarcacionView");
        this.modelAndView.addObject("embarcacion", new Embarcacion());
        this.modelAndView.addObject("types", EmbarcacionType.values());
        return modelAndView;
    }

    @PostMapping("/addEmbarcacion")
    public String addEmbarcacion(@ModelAttribute Embarcacion embarcacion, SessionStatus sessionStatus) {
        
        if (embarcacion.getPatronId() == null || embarcacion.getPatronId().isEmpty()) {
            embarcacion.setPatronId(null);
        }
        
        System.out.println("[AddEmbarcacionController] Received info: registrationNumber=" + embarcacion.getRegistrationNumber() +
                " type=" + embarcacion.getType().toString() +
                " name=" + embarcacion.getName() +
                " numSeats=" + embarcacion.getNumSeats() +
                " patronId=" + embarcacion.getPatronId() +
                " length=" + embarcacion.getLength() +
                " width=" + embarcacion.getWidth() +
                " height=" + embarcacion.getHeight());
        
        // Verificar si ya existe una embarcación con el mismo nombre
        boolean nameExists = checkIfNameExists(embarcacion.getName());
        if (nameExists) {
            System.out.println("[AddEmbarcacionController] Error: Name '" + embarcacion.getName() + "' already exists");
            return "embarcacion/addEmbarcacionFailView";
        }
                
        boolean success = embarcacionRepository.addEmbarcacion(embarcacion);
        String nextPage;

        if (success) {
            nextPage = "embarcacion/addEmbarcacionSuccessView";
        } else {
            nextPage = "embarcacion/addEmbarcacionFailView";
        }

        sessionStatus.setComplete();
        return nextPage;
    }

    /**
     * Verifica si ya existe una embarcación con el mismo nombre en la base de datos
     * @param name El nombre a verificar
     * @return true si el nombre ya existe, false en caso contrario
     */
    private boolean checkIfNameExists(String name) {
        try {
            // Obtener todas las embarcaciones
            java.util.List<Embarcacion> allEmbarcaciones = embarcacionRepository.findAllEmbarcaciones();
            
            if (allEmbarcaciones != null) {
                for (Embarcacion embarcacion : allEmbarcaciones) {
                    if (embarcacion.getName() != null && embarcacion.getName().equalsIgnoreCase(name)) {
                        return true; // Nombre ya existe
                    }
                }
            }
            return false; // Nombre no existe
        } catch (Exception e) {
            System.err.println("Error checking if name exists: " + e.getMessage());
            e.printStackTrace();
            return true; // En caso de error, asumimos que existe para prevenir duplicados
        }
    }
}