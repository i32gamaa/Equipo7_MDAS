package es.uco.pw.demo.controller.socioinscripcion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.repository.SocioRepository;

@Controller
public class UpdateIsBoatDriverController {

    SocioRepository socioRepository;

    public UpdateIsBoatDriverController(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.socioRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/updateIsBoatDriver")
    public ModelAndView showForm() {
        return new ModelAndView("socioinscripcion/updateIsBoatDriverView");
    }

    @PostMapping("/updateIsBoatDriver")
    public ModelAndView findSocioById(@RequestParam("id") String id) {
        Socio s = socioRepository.findById(id);
        ModelAndView mav;

        if (s != null) {
            if (!s.getIsBoatDriver()) {
                s.setIsBoatDriver(true);
                socioRepository.updateIsBoatDriver(s.getId(), s.getIsBoatDriver());

                System.out.println("[UpdateIsBoatDriverController] Socio " + s.getId() +
                        " actualizado: isBoatDriver = true");

                mav = new ModelAndView("socioinscripcion/updateIsBoatDriverSuccessView");
                mav.addObject("socio", s);
            } else {
                System.out.println("[UpdateIsBoatDriverController] Socio " + s.getId() +
                        " ya tenía isBoatDriver = true, no se actualiza.");
                mav = new ModelAndView("socioinscripcion/updateIsBoatDriverFailView");
                mav.addObject("socio", s);
            }
        } else {
            mav = new ModelAndView("socioinscripcion/updateIsBoatDriverFailView");
        }

        return mav;
    }

}
