package es.uco.pw.demo.controller.patron;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import es.uco.pw.demo.model.domain.Patron;
import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.repository.PatronRepository;
import es.uco.pw.demo.model.repository.SocioRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.time.LocalDate;
import java.time.Period;

@Controller
public class AddPatronController {

    // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Replace Magic Number with Symbolic Constant]
    private static final int MAYORIA_EDAD = 18;

    private PatronRepository patronRepository;
    private SocioRepository socioRepository;

    public AddPatronController(PatronRepository patronRepository, SocioRepository socioRepository) {
        this.patronRepository = patronRepository;
        this.socioRepository = socioRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.patronRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    // [CLEAN CODE - SEMANA 3: Un solo nivel de abstracción. Se delega la construcción de la vista]
    @GetMapping("/addPatron")
    public ModelAndView mostrarFormularioRegistro() {
        return construirVistaFormulario();
    }

    // [CLEAN CODE - SEMANA 3: Do One Thing. El flujo principal se lee como una historia (Validar -> Guardar -> Responder)]
    @PostMapping("/addPatron")
    public String procesarNuevoPatron(@ModelAttribute("newPatron") Patron patronSolicitado, SessionStatus estadoSesion) {
        
        String vistaError = validarNuevoPatron(patronSolicitado);
        if (vistaError != null) {
            return vistaError;
        }

        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        String vistaResultado = determinarVistaResultado(patronRepository.addPatron(patronSolicitado));
        estadoSesion.setComplete();
        
        return vistaResultado;
    }

    // ====================================================================================================
    // [CLEAN CODE - SEMANA 3: Extracción a métodos privados (Stepdown Rule)]
    // ====================================================================================================

    // [CLEAN CODE - SEMANA 3: Extracción de carga del ModelAndView]
    private ModelAndView construirVistaFormulario() {
        ModelAndView modelAndView = new ModelAndView("patron/addPatronView");
        modelAndView.addObject("newPatron", new Patron());
        return modelAndView;
    }

    // [CLEAN CODE - SEMANA 3: Extracción de validaciones complejas de negocio]
    private String validarNuevoPatron(Patron patron) {
        if (!esMayorDeEdad(patron.getBirthDate())) {
            return "patron/addPatronFailNOADULTView";
        }
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Consolidate Conditional Expression]
        if (patronRepository.findById(patron.getPatronId()) != null || socioRepository.findById(patron.getPatronId()) != null) {
            return "patron/addPatronDuplicateIdView"; 
        }
        return null;
    }

    // [CLEAN CODE - SEMANA 3: Delega la decisión de enrutamiento a un método específico]
    private String determinarVistaResultado(boolean exito) {
        return exito ? "patron/addPatronSuccessView" : "patron/addPatronFailView";
    }

    // [CLEAN CODE - SEMANA 3: Función pura, pequeña y de una sola responsabilidad]
    private boolean esMayorDeEdad(LocalDate fechaNacimiento) {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Guard Clauses]
        if (fechaNacimiento == null) {
            return false;
        }
        // [REFACTORIZACIÓN MANUAL - Uso de la constante extraída]
        return Period.between(fechaNacimiento, LocalDate.now()).getYears() >= MAYORIA_EDAD;
    }
}