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
    public ModelAndView getAddPatronView() {
        this.modelAndView.setViewName("patron/addPatronView");
        this.modelAndView.addObject("newPatron", new Patron());
        return modelAndView;
    }

    @PostMapping("/addPatron")
    public String addPatron(@ModelAttribute Patron newPatron, SessionStatus sessionStatus) {

        System.out.println("[AddPatronController] Received info: id=" + newPatron.getId() +
                " name=" + newPatron.getName() +
                " surname=" + newPatron.getSurname() +
                " birth=" + newPatron.getBirthDate() +
                " titleIssueDate=" + newPatron.getTitleIssueDate());

        // Validar que el patrón es mayor de edad (18 años o más)
        if (!isAdult(newPatron.getBirthDate())) {
            System.out.println("[AddPatronController] Error: el patrón es menor de edad");
            return "patron/addPatronFailNOADULTView";
        }

        Patron existingPatron = patronRepository.findById(newPatron.getId());
        if (existingPatron != null) {
            System.out.println("[AddPatronController] Error: ya existe un patron con el ID " + newPatron.getId());
            return "patron/addPatronDuplicateIdView"; 
        }
        Socio existingSocio = socioRepository.findById(newPatron.getId());
        if (existingSocio != null) {
            System.out.println("[AddPatronController] Error: ya existe un socio con el ID " + newPatron.getId());
            return "patron/addPatronDuplicateIdView";
        }

        boolean success = patronRepository.addPatron(newPatron);
        String nextPage;

        if (success) {
            nextPage = "patron/addPatronSuccessView";
        } else
            nextPage = "patron/addPatronFailView";

        sessionStatus.setComplete();
        return nextPage;
    }

    /**
     * Verifica si una persona es mayor de edad (18 años o más)
     * @param birthDate la fecha de nacimiento
     * @return true si es mayor de edad, false en caso contrario
     */
    private boolean isAdult(LocalDate birthDate) {
        if (birthDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        Period age = Period.between(birthDate, today);
        return age.getYears() >= 18;
    }
}