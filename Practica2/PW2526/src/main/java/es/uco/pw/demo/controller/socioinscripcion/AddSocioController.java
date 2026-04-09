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

    private SocioRepository socioRepository;
    private InscripcionRepository inscripcionRepository;
    private PatronRepository patronRepository;
    private ModelAndView modelAndView = new ModelAndView();

    public AddSocioController(SocioRepository socioRepository, InscripcionRepository inscripcionRepository, PatronRepository patronRepository) {
        this.socioRepository = socioRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.patronRepository = patronRepository;

        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.socioRepository.setSQLQueriesFileName(sqlQueriesFileName);
        this.inscripcionRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/addSocio")
    public ModelAndView getAddSocioForm() {
        this.modelAndView.setViewName("socioinscripcion/addSocioView");
        this.modelAndView.addObject("newSocio", new Socio());
        return modelAndView;
    }

    @PostMapping("/addSocio")
    public String addSocio(@ModelAttribute Socio newSocio, SessionStatus sessionStatus) {

        System.out.println("[AddSocioController] Recibido socio: dni=" + newSocio.getId() +
                ", nombre=" + newSocio.getName() +
                ", apellidos=" + newSocio.getSurname() +
                " direccion=" + newSocio.getAddress() +
                ", nacimiento=" + newSocio.getBirthdate() +
                ", inscripción ID=" + newSocio.getInscriptionId());

       Socio existingSocio = socioRepository.findById(newSocio.getId());
       if (existingSocio != null) {
           System.out.println("[AddSocioController] Error: ya existe un socio con el ID " + newSocio.getId());
           return "socioinscripcion/addSocioDuplicateIdView"; 
       }
       Patron existingPatron = patronRepository.findById(newSocio.getId());
        if (existingPatron != null) {
            System.out.println("[AddInscripcionController] Error: ya existe un patron con el ID " + newSocio.getId());
            return "socioinscripcion/addInscripcionDuplicateIdView"; 
        }

        boolean esAdulto = Period.between(newSocio.getBirthdate(), LocalDate.now()).getYears() >= 18;
        newSocio.setIsAdult(esAdulto);
        newSocio.setInscriptionDate(LocalDate.now());

        if (!esAdulto) {
            newSocio.setIsBoatDriver(false);
        }

        System.out.println("[AddSocioController] Es adulto: " + esAdulto);

        Inscripcion inscripcion = inscripcionRepository.findById(newSocio.getInscriptionId());

        if (inscripcion != null) {
            int cuotaExtra = esAdulto ? 250 : 100;
            inscripcion.setTotalAmount(inscripcion.getTotalAmount() + cuotaExtra);
            if(esAdulto){
                inscripcion.setRegisteredAdults(inscripcion.getRegisteredAdults() + 1);
            }else{
                inscripcion.setRegisteredKids(inscripcion.getRegisteredKids() + 1);
            }

            if (inscripcion.getRegisteredAdults() > 2) {
                System.out.println("[AddSocioController] Error: ya existen dos adultos asociados a la inscripcion familiar");
                return "socioinscripcion/addSocioFailView"; 
            }

            inscripcionRepository.update(inscripcion);

            System.out.println("[AddSocioController] Actualizada inscripción " + inscripcion.getId() + " con cuota +" + cuotaExtra + "€. Total: " + inscripcion.getTotalAmount() + "adultos: " + inscripcion.getRegisteredAdults() + "niños: " + inscripcion.getRegisteredKids());

            // Actualizar el inscriptionId del socio con el id de la inscripción actualizada
            newSocio.setInscriptionId(inscripcion.getId());
            System.out.println("[AddSocioController] InscriptionId del socio actualizado a: " + newSocio.getInscriptionId());

        } else {
            System.out.println("[AddSocioController] No se encontró inscripción con ID: " + newSocio.getInscriptionId());
        }

        // Guardar socio
        boolean success = socioRepository.addSocio(newSocio);
        String nextPage;

        if (success) {
            nextPage = "socioinscripcion/addSocioSuccessView";
        } else {
            nextPage = "socioinscripcion/addSocioFailView";
        }

        sessionStatus.setComplete();
        return nextPage;
    }
}


