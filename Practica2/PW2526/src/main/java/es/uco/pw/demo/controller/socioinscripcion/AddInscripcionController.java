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

    private final SocioRepository socioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final PatronRepository patronRepository;
    private ModelAndView modelAndView = new ModelAndView();

    public AddInscripcionController(SocioRepository socioRepository, InscripcionRepository inscripcionRepository, PatronRepository patronRepository) {
        this.socioRepository = socioRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.patronRepository = patronRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.socioRepository.setSQLQueriesFileName(sqlQueriesFileName);
        this.inscripcionRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/addInscripcion")
    public ModelAndView mostrarFormularioInscripcion() {
        this.modelAndView.setViewName("socioinscripcion/addInscripcionView");
        this.modelAndView.addObject("newSocio", new Socio());
        return modelAndView;
    }

    @PostMapping("/addInscripcion")
    public String procesarNuevaInscripcion(@ModelAttribute("newSocio") Socio socioTitular, SessionStatus estadoSesion) {

        Socio socioExistente = socioRepository.findById(socioTitular.getSocioId());
        if (socioExistente != null) {
            return "socioinscripcion/addInscripcionDuplicateIdView"; 
        }
        
        Patron patronExistente = patronRepository.findById(socioTitular.getSocioId());
        if (patronExistente != null) {
            return "socioinscripcion/addInscripcionDuplicateIdView"; 
        }

        socioTitular.setAdult(Period.between(socioTitular.getBirthdate(), LocalDate.now()).getYears() >= 18);
        socioTitular.setHolderInscription(true);
        socioTitular.setInscriptionDate(LocalDate.now());

        if (!socioTitular.isAdult()) {
            return "socioinscripcion/addInscripcionFailNotAdultView";
        }

        boolean socioCreadoCorrectamente = socioRepository.addSocioAdult(socioTitular);
        if (!socioCreadoCorrectamente) {
            return "socioinscripcion/addInscripcionFailView";
        }

        Inscripcion nuevaInscripcion = new Inscripcion();
        nuevaInscripcion.setRegistrationDate(LocalDate.now());
        nuevaInscripcion.setTotalAmount(300);
        nuevaInscripcion.setUserId(socioTitular.getSocioId());
        nuevaInscripcion.setRegisteredAdults(1);
        nuevaInscripcion.setRegisteredKids(0);

        boolean inscripcionCreadaCorrectamente = inscripcionRepository.addInscripcion(nuevaInscripcion);
        if (!inscripcionCreadaCorrectamente) {
            return "socioinscripcion/addInscripcionFailView";
        }

        Inscripcion inscripcionRecuperada = inscripcionRepository.findByUserId(socioTitular.getSocioId());
        if (inscripcionRecuperada == null) {
            return "socioinscripcion/addInscripcionFailView";
        }

        socioTitular.setInscriptionId(inscripcionRecuperada.getId());
        boolean socioActualizadoCorrectamente = socioRepository.updateInscriptionId(socioTitular);
        
        String vistaDestino = socioActualizadoCorrectamente ? "socioinscripcion/addSocioSuccessView" : "socioinscripcion/addSocioFailView";

        estadoSesion.setComplete();
        return vistaDestino;
    }
}