package es.uco.pw.demo.controller.socioinscripcion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.domain.Inscripcion;
import es.uco.pw.demo.model.domain.Patron;
import es.uco.pw.demo.model.repository.SocioRepository;
import es.uco.pw.demo.model.repository.InscripcionRepository;
import es.uco.pw.demo.model.repository.PatronRepository;
import java.time.LocalDate;
import java.time.Period;

@Controller
public class AddInscripcionController {

    // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Replace Magic Number with Symbolic Constant]
    private static final int EDAD_MAYORIA = 18;
    private static final int CUOTA_BASE_INSCRIPCION = 300;

    private final SocioRepository socioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final PatronRepository patronRepository;

    public AddInscripcionController(SocioRepository socioRepository, InscripcionRepository inscripcionRepository, PatronRepository patronRepository) {
        this.socioRepository = socioRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.patronRepository = patronRepository;
    }

    @GetMapping("/addInscripcion")
    public ModelAndView mostrarFormularioInscripcion() {
        return construirVistaFormulario();
    }

    // [CLEAN CODE - SEMANA 3: Método principal con un solo nivel de abstracción. 
    // Divide el proceso complejo en pasos lógicos de alto nivel]
    @PostMapping("/addInscripcion")
    public String procesarNuevaInscripcion(@ModelAttribute("newSocio") Socio socioTitular, SessionStatus estadoSesion) {

        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Guard Clauses]
        if (comprobarDuplicadosEnSistema(socioTitular.getSocioId())) {
            return "socioinscripcion/addInscripcionDuplicateIdView"; 
        }

        if (!validarMayoriaEdad(socioTitular)) {
            return "socioinscripcion/addInscripcionFailNotAdultView";
        }

        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        String vistaResultado = determinarVistaResultado(ejecutarProcesoCreacionInscripcion(socioTitular));
        estadoSesion.setComplete();
        
        return vistaResultado;
    }

    // ====================================================================================================
    // MÉTODOS PRIVADOS EXTRAÍDOS
    // ====================================================================================================

    private ModelAndView construirVistaFormulario() {
        ModelAndView mav = new ModelAndView("socioinscripcion/addInscripcionView");
        mav.addObject("newSocio", new Socio());
        return mav;
    }

    private boolean comprobarDuplicadosEnSistema(String id) {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Consolidate Conditional Expression]
        return socioRepository.findById(id) != null || patronRepository.findById(id) != null;
    }

    private boolean validarMayoriaEdad(Socio socio) {
        // [REFACTORIZACIÓN MANUAL - Uso de constante]
        boolean esAdulto = Period.between(socio.getBirthdate(), LocalDate.now()).getYears() >= EDAD_MAYORIA;
        socio.setAdult(esAdulto);
        socio.setHolderInscription(true);
        socio.setInscriptionDate(LocalDate.now());
        return esAdulto;
    }

    // [CLEAN CODE - SEMANA 3: Encapsula el proceso secuencial de creación de entidades relacionadas]
    private boolean ejecutarProcesoCreacionInscripcion(Socio socio) {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Guard Clauses]
        if (!socioRepository.addSocioAdult(socio)) return false;
        if (!crearInscripcionAsociada(socio)) return false;

        return vincularSocioConNuevaInscripcion(socio);
    }

    private boolean crearInscripcionAsociada(Socio titular) {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setRegistrationDate(LocalDate.now());
        // [REFACTORIZACIÓN MANUAL - Uso de constante]
        inscripcion.setTotalAmount(CUOTA_BASE_INSCRIPCION);
        inscripcion.setUserId(titular.getSocioId());
        inscripcion.setRegisteredAdults(1);
        inscripcion.setRegisteredKids(0);
        return inscripcionRepository.addInscripcion(inscripcion);
    }

    private boolean vincularSocioConNuevaInscripcion(Socio socio) {
        Inscripcion creada = inscripcionRepository.findByUserId(socio.getSocioId());
        if (creada == null) return false;
        
        socio.setInscriptionId(creada.getId());
        return socioRepository.updateInscriptionId(socio);
    }

    private String determinarVistaResultado(boolean exito) {
        return exito ? "socioinscripcion/addSocioSuccessView" : "socioinscripcion/addSocioFailView";
    }
}