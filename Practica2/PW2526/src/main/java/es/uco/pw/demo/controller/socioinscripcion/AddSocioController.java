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
    public ModelAndView mostrarFormularioRegistroSocio() {
        this.modelAndView.setViewName("socioinscripcion/addSocioView");
        this.modelAndView.addObject("newSocio", new Socio());
        return modelAndView;
    }

    @PostMapping("/addSocio")
    public String procesarNuevoSocio(@ModelAttribute("newSocio") Socio socioSolicitado, SessionStatus estadoSesion) {

        System.out.println("[AddSocioController] Recibido socio: dni=" + socioSolicitado.getId() +
                ", nombre=" + socioSolicitado.getName() +
                ", apellidos=" + socioSolicitado.getSurname() +
                " direccion=" + socioSolicitado.getAddress() +
                ", nacimiento=" + socioSolicitado.getBirthdate() +
                ", inscripción ID=" + socioSolicitado.getInscriptionId());

       Socio socioExistente = socioRepository.findById(socioSolicitado.getId());
       if (socioExistente != null) {
           System.out.println("[AddSocioController] Error: ya existe un socio con el ID " + socioSolicitado.getId());
           return "socioinscripcion/addSocioDuplicateIdView"; 
       }
       Patron patronExistente = patronRepository.findById(socioSolicitado.getId());
        if (patronExistente != null) {
            System.out.println("[AddSocioController] Error: ya existe un patron con el ID " + socioSolicitado.getId());
            return "socioinscripcion/addInscripcionDuplicateIdView"; 
        }

        boolean esAdulto = Period.between(socioSolicitado.getBirthdate(), LocalDate.now()).getYears() >= 18;
        socioSolicitado.setIsAdult(esAdulto);
        socioSolicitado.setInscriptionDate(LocalDate.now());

        if (!esAdulto) {
            socioSolicitado.setIsBoatDriver(false);
        }

        System.out.println("[AddSocioController] Es adulto: " + esAdulto);

        Inscripcion inscripcionEncontrada = inscripcionRepository.findById(socioSolicitado.getInscriptionId());

        if (inscripcionEncontrada != null) {
            int cuotaExtra = esAdulto ? 250 : 100;
            inscripcionEncontrada.setTotalAmount(inscripcionEncontrada.getTotalAmount() + cuotaExtra);
            if(esAdulto){
                inscripcionEncontrada.setRegisteredAdults(inscripcionEncontrada.getRegisteredAdults() + 1);
            }else{
                inscripcionEncontrada.setRegisteredKids(inscripcionEncontrada.getRegisteredKids() + 1);
            }

            if (inscripcionEncontrada.getRegisteredAdults() > 2) {
                System.out.println("[AddSocioController] Error: ya existen dos adultos asociados a la inscripcion familiar");
                return "socioinscripcion/addSocioFailView"; 
            }

            inscripcionRepository.update(inscripcionEncontrada);

            System.out.println("[AddSocioController] Actualizada inscripción " + inscripcionEncontrada.getId() + " con cuota +" + cuotaExtra + " Euros. Total: " + inscripcionEncontrada.getTotalAmount() + "adultos: " + inscripcionEncontrada.getRegisteredAdults() + "niños: " + inscripcionEncontrada.getRegisteredKids());

            socioSolicitado.setInscriptionId(inscripcionEncontrada.getId());
            System.out.println("[AddSocioController] InscriptionId del socio actualizado a: " + socioSolicitado.getInscriptionId());

        } else {
            System.out.println("[AddSocioController] No se encontró inscripción con ID: " + socioSolicitado.getInscriptionId());
        }

        boolean registroCompletado = socioRepository.addSocio(socioSolicitado);
        String vistaDestino;

        if (registroCompletado) {
            vistaDestino = "socioinscripcion/addSocioSuccessView";
        } else {
            vistaDestino = "socioinscripcion/addSocioFailView";
        }

        estadoSesion.setComplete();
        return vistaDestino;
    }
}