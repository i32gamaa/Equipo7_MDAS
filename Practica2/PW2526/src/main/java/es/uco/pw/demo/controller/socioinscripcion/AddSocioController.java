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
public class AddSocioController {

    private final SocioRepository socioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final PatronRepository patronRepository;

    public AddSocioController(SocioRepository socioRepository, InscripcionRepository inscripcionRepository, PatronRepository patronRepository) {
        this.socioRepository = socioRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.patronRepository = patronRepository;
    }

    // [CLEAN CODE - SEMANA 3: Un solo nivel de abstracción. Delega la creación de la vista]
    @GetMapping("/addSocio")
    public ModelAndView mostrarFormularioRegistroSocio() {
        return construirVistaFormulario();
    }

    // [CLEAN CODE - SEMANA 3: Función principal que se lee como una historia. 
    // Los pasos de validación, procesamiento de datos y actualización de cuotas están encapsulados]
    @PostMapping("/addSocio")
    public String procesarNuevoSocio(@ModelAttribute("newSocio") Socio socioSolicitado, SessionStatus estadoSesion) {
        
        if (comprobarSocioOPatronExistente(socioSolicitado.getSocioId())) {
            return "socioinscripcion/addSocioDuplicateIdView"; 
        }

        prepararDatosPerfilSocio(socioSolicitado);
        actualizarEstadoInscripcionFamiliar(socioSolicitado);

        boolean registroCompletado = socioRepository.addSocio(socioSolicitado);
        estadoSesion.setComplete();
        
        return determinarVistaResultado(registroCompletado);
    }

    // ====================================================================================================
    // MÉTODOS PRIVADOS EXTRAÍDOS
    // ====================================================================================================

    // [CLEAN CODE - SEMANA 3: Extracción de carga de ModelAndView]
    private ModelAndView construirVistaFormulario() {
        ModelAndView mav = new ModelAndView("socioinscripcion/addSocioView");
        mav.addObject("newSocio", new Socio());
        return mav;
    }

    // [CLEAN CODE - SEMANA 3: Do One Thing. Encapsula la lógica de búsqueda de duplicados]
    private boolean comprobarSocioOPatronExistente(String id) {
        return socioRepository.findById(id) != null || patronRepository.findById(id) != null;
    }

    // [CLEAN CODE - SEMANA 3: Extrae la lógica de bajo nivel sobre el estado del objeto Socio]
    private void prepararDatosPerfilSocio(Socio socio) {
        boolean esAdulto = Period.between(socio.getBirthdate(), LocalDate.now()).getYears() >= 18;
        socio.setAdult(esAdulto);
        socio.setInscriptionDate(LocalDate.now());
        if (!esAdulto) {
            socio.setBoatDriver(false);
        }
    }

    // [CLEAN CODE - SEMANA 3: Extrae la gestión compleja de cuotas y miembros de la inscripción]
    private void actualizarEstadoInscripcionFamiliar(Socio socio) {
        Inscripcion inscripcion = inscripcionRepository.findById(socio.getInscriptionId());
        if (inscripcion != null) {
            aplicarRecargosYActualizarMiembros(inscripcion, socio.isAdult());
            inscripcionRepository.update(inscripcion);
        }
    }

    // [CLEAN CODE - SEMANA 3: Sub-función para cumplir con un único nivel de abstracción en el proceso de inscripción]
    private void aplicarRecargosYActualizarMiembros(Inscripcion inscripcion, boolean esAdulto) {
        int cuotaExtra = esAdulto ? 250 : 100;
        inscripcion.setTotalAmount(inscripcion.getTotalAmount() + cuotaExtra);
        if (esAdulto) {
            inscripcion.setRegisteredAdults(inscripcion.getRegisteredAdults() + 1);
        } else {
            inscripcion.setRegisteredKids(inscripcion.getRegisteredKids() + 1);
        }
    }

    private String determinarVistaResultado(boolean exito) {
        return exito ? "socioinscripcion/addSocioSuccessView" : "socioinscripcion/addSocioFailView";
    }
}