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

    private final EmbarcacionRepository embarcacionRepository;

    public AddEmbarcacionController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
    }

    // [CLEAN CODE - SEMANA 3: Un solo nivel de abstracción. Delega la carga del ModelAndView a un método privado]
    @GetMapping("/addEmbarcacion")
    public ModelAndView mostrarFormularioRegistro() {
        return construirVistaRegistro();
    }

    // [CLEAN CODE - SEMANA 3: El método principal procesa la acción en pasos de alto nivel (Validar -> Guardar -> Responder)]
    @PostMapping("/addEmbarcacion")
    public String procesarNuevaEmbarcacion(@ModelAttribute Embarcacion nuevaEmbarcacion, SessionStatus estadoSesion) {
        
        normalizarDatosEntrada(nuevaEmbarcacion);
        
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Guard Clauses]
        if (comprobarNombreDuplicado(nuevaEmbarcacion.getName())) {
            return "embarcacion/addEmbarcacionFailView";
        }
                
        boolean exitoRegistro = embarcacionRepository.addEmbarcacion(nuevaEmbarcacion);
        estadoSesion.setComplete();
        
        return determinarVistaResultado(exitoRegistro);
    }

    // ====================================================================================================
    // [CLEAN CODE - SEMANA 3: Métodos privados para cumplimiento de Stepdown Rule y Do One Thing]
    // ====================================================================================================

    // [CLEAN CODE - SEMANA 3: Extracción de la carga de datos del ModelAndView]
    private ModelAndView construirVistaRegistro() {
        ModelAndView mav = new ModelAndView("embarcacion/addEmbarcacionView");
        mav.addObject("embarcacion", new Embarcacion());
        mav.addObject("types", EmbarcacionType.values());
        return mav;
    }

    // [CLEAN CODE - SEMANA 3: Encapsula lógica de bajo nivel sobre los datos]
    private void normalizarDatosEntrada(Embarcacion embarcacion) {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Consolidate Conditional Expression]
        if (embarcacion.getPatronId() == null || embarcacion.getPatronId().trim().isEmpty()) {
            embarcacion.setPatronId(null);
        }
    }

    // [CLEAN CODE - SEMANA 3: Lógica de selección de vista extraída para mayor claridad]
    private String determinarVistaResultado(boolean exito) {
        return exito ? "embarcacion/addEmbarcacionSuccessView" : "embarcacion/addEmbarcacionFailView";
    }

    // [CLEAN CODE - SEMANA 3: Do One Thing. Este método ya existía pero se mantiene por su buen diseño]
    private boolean comprobarNombreDuplicado(String nombreAComprobar) {
        try {
            // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
            return embarcacionRepository.findAllEmbarcaciones().stream()
                    .anyMatch(e -> e.getName() != null && e.getName().equalsIgnoreCase(nombreAComprobar));
        } catch (Exception e) {
            return true; 
        }
    }
}