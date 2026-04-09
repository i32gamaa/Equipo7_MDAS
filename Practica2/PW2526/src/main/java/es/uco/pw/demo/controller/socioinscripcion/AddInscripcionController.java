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
        this.patronRepository=patronRepository;

        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.socioRepository.setSQLQueriesFileName(sqlQueriesFileName);
        this.inscripcionRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/addInscripcion")
    public ModelAndView getAddSocioForm() {
        this.modelAndView.setViewName("socioinscripcion/addInscripcionView");
        this.modelAndView.addObject("newSocio", new Socio());
        return modelAndView;
    }

    @PostMapping("/addInscripcion")
    public String addSocio(@ModelAttribute Socio newSocio, SessionStatus sessionStatus) {

        System.out.println("[AddInscripcionController] Datos recibidos: " +
                "id=" + newSocio.getId() +
                ", name=" + newSocio.getName() +
                ", surname=" + newSocio.getSurname() +
                ", address=" + newSocio.getAddress() +
                ", birthdate=" + newSocio.getBirthdate() +
                ", isBoatDriver=" + newSocio.getIsBoatDriver());
        
         //1. Comprobar si el ID ya existe en la base de datos en socio o en patron
        Socio existingSocio = socioRepository.findById(newSocio.getId());
        if (existingSocio != null) {
            System.out.println("[AddInscripcionController] Error: ya existe un socio con el ID " + newSocio.getId());
            return "socioinscripcion/addInscripcionDuplicateIdView"; 
        }
        Patron existingPatron = patronRepository.findById(newSocio.getId());
        if (existingPatron != null) {
            System.out.println("[AddInscripcionController] Error: ya existe un patron con el ID " + newSocio.getId());
            return "socioinscripcion/addInscripcionDuplicateIdView"; 
        }

        newSocio.setIsAdult(Period.between(newSocio.getBirthdate(), LocalDate.now()).getYears() >= 18);
        newSocio.setIsHolderInscription(true);
        newSocio.setInscriptionDate(LocalDate.now());


        if(newSocio.getIsAdult()==false){
            System.out.println("[AddInscripcionController] El socio es menor de edad. No se puede crear inscripción ni socio.");
            return "socioinscripcion/addInscripcionFailNotAdultView"; // o tu vista específica
        }

        // Crear socio sin inscripción (inscriptionId NULL)
        boolean socioCreated = socioRepository.addSocioAdult(newSocio);
        if (!socioCreated) {
            System.out.println("[AddInscripcionController] Error al crear socio.");
            return "socioinscripcion/addInscripcionFailView";
        }

        // Crear inscripción asociada
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setDate(LocalDate.now());
        inscripcion.setTotalAmount(300);
        inscripcion.setUserId(newSocio.getId());
        inscripcion.setRegisteredAdults(1);
        inscripcion.setRegisteredKids(0);

        boolean inscriptionCreated = inscripcionRepository.addInscripcion(inscripcion);
        if (!inscriptionCreated) {
            System.out.println("[AddInscripcionController] Error al crear inscripción.");
            return "socioinscripcion/addInscripcionFailView";
        }

        // Recuperar ID inscripción creada
        Inscripcion inscripcion2 = inscripcionRepository.findByUserId(newSocio.getId());
        if (inscripcion2 == null) {
            System.out.println("[AddInscripcionController] No se pudo recuperar el ID de inscripción creada.");
            return "socioinscripcion/addInscripcionFailView";
        }

        // 4Actualizar socio con el ID de la inscripción
        newSocio.setInscriptionId(inscripcion2.getId());

        boolean socioUpdated = socioRepository.updateInscriptionId(newSocio);
        String nextPage;

        if (socioUpdated) {
            nextPage = "socioinscripcion/addSocioSuccessView";
        } else {
            nextPage = "socioinscripcion/addSocioFailView";
        }

        sessionStatus.setComplete();
        return nextPage;
    }

}

