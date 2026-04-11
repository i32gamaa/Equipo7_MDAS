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

    private PatronRepository patronRepository;
    private SocioRepository socioRepository;
    private ModelAndView modelAndView = new ModelAndView();

    public AddPatronController(PatronRepository patronRepository, SocioRepository socioRepository) {
        this.patronRepository = patronRepository;
        this.socioRepository = socioRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.patronRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/addPatron")
    public ModelAndView mostrarFormularioRegistro() {
        this.modelAndView.setViewName("patron/addPatronView");
        this.modelAndView.addObject("newPatron", new Patron());
        return modelAndView;
    }

    @PostMapping("/addPatron")
    public String procesarNuevoPatron(@ModelAttribute("newPatron") Patron patronSolicitado, SessionStatus estadoSesion) {

        System.out.println("[AddPatronController] Received info: id=" + patronSolicitado.getId() +
                " name=" + patronSolicitado.getName() +
                " surname=" + patronSolicitado.getSurname() +
                " birth=" + patronSolicitado.getBirthDate() +
                " titleIssueDate=" + patronSolicitado.getTitleIssueDate());

        if (!esMayorDeEdad(patronSolicitado.getBirthDate())) {
            System.out.println("[AddPatronController] Error: el patrón es menor de edad");
            return "patron/addPatronFailNOADULTView";
        }

        Patron patronExistente = patronRepository.findById(patronSolicitado.getId());
        if (patronExistente != null) {
            System.out.println("[AddPatronController] Error: ya existe un patron con el ID " + patronSolicitado.getId());
            return "patron/addPatronDuplicateIdView"; 
        }
        
        Socio socioExistente = socioRepository.findById(patronSolicitado.getId());
        if (socioExistente != null) {
            System.out.println("[AddPatronController] Error: ya existe un socio con el ID " + patronSolicitado.getId());
            return "patron/addPatronDuplicateIdView";
        }

        boolean registroCompletado = patronRepository.addPatron(patronSolicitado);
        String vistaDestino;

        if (registroCompletado) {
            vistaDestino = "patron/addPatronSuccessView";
        } else {
            vistaDestino = "patron/addPatronFailView";
        }

        estadoSesion.setComplete();
        return vistaDestino;
    }

    private boolean esMayorDeEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return false;
        }
        LocalDate fechaActual = LocalDate.now();
        Period edad = Period.between(fechaNacimiento, fechaActual);
        return edad.getYears() >= 18;
    }
}