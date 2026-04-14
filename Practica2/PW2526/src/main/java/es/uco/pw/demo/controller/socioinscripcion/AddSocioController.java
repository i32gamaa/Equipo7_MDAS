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

       Socio socioExistente = socioRepository.findById(socioSolicitado.getSocioId());
       if (socioExistente != null) {
           return "socioinscripcion/addSocioDuplicateIdView"; 
       }
       Patron patronExistente = patronRepository.findById(socioSolicitado.getSocioId());
        if (patronExistente != null) {
            return "socioinscripcion/addInscripcionDuplicateIdView"; 
        }

        boolean esAdulto = Period.between(socioSolicitado.getBirthdate(), LocalDate.now()).getYears() >= 18;
        socioSolicitado.setAdult(esAdulto);
        socioSolicitado.setInscriptionDate(LocalDate.now());

        if (!esAdulto) {
            socioSolicitado.setBoatDriver(false);
        }

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
                return "socioinscripcion/addSocioFailView"; 
            }

            inscripcionRepository.update(inscripcionEncontrada);
            socioSolicitado.setInscriptionId(inscripcionEncontrada.getId());

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