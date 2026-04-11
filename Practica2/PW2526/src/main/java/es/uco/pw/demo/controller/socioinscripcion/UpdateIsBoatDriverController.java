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

    private SocioRepository socioRepository;

    public UpdateIsBoatDriverController(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.socioRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/updateIsBoatDriver")
    public ModelAndView mostrarFormularioActualizacion() {
        return new ModelAndView("socioinscripcion/updateIsBoatDriverView");
    }

    @PostMapping("/updateIsBoatDriver")
    public ModelAndView procesarActualizacionPatron(@RequestParam("id") String dniBuscado) {
        Socio socioEncontrado = socioRepository.findById(dniBuscado);
        ModelAndView vistaResultados;

        if (socioEncontrado != null) {
            if (!socioEncontrado.getIsBoatDriver()) {
                socioEncontrado.setIsBoatDriver(true);
                socioRepository.updateIsBoatDriver(socioEncontrado.getId(), socioEncontrado.getIsBoatDriver());

                System.out.println("[UpdateIsBoatDriverController] Socio " + socioEncontrado.getId() +
                        " actualizado: isBoatDriver = true");

                vistaResultados = new ModelAndView("socioinscripcion/updateIsBoatDriverSuccessView");
                vistaResultados.addObject("socio", socioEncontrado);
            } else {
                System.out.println("[UpdateIsBoatDriverController] Socio " + socioEncontrado.getId() +
                        " ya tenía isBoatDriver = true, no se actualiza.");
                vistaResultados = new ModelAndView("socioinscripcion/updateIsBoatDriverFailView");
                vistaResultados.addObject("socio", socioEncontrado);
            }
        } else {
            vistaResultados = new ModelAndView("socioinscripcion/updateIsBoatDriverFailView");
        }

        return vistaResultados;
    }
}