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

    private final SocioRepository socioRepository;

    public UpdateIsBoatDriverController(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    @GetMapping("/updateIsBoatDriver")
    public ModelAndView mostrarFormularioActualizacion() {
        return new ModelAndView("socioinscripcion/updateIsBoatDriverView");
    }

    // [CLEAN CODE - SEMANA 3: Historia clara: buscar -> intentar actualizar -> responder]
    @PostMapping("/updateIsBoatDriver")
    public ModelAndView procesarActualizacionPatron(@RequestParam("id") String dniBuscado) {
        Socio socio = socioRepository.findById(dniBuscado);
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        return construirVistaResultado(socio, intentarOtorgarLicencia(socio));
    }

    // ====================================================================================================
    // MÉTODOS PRIVADOS EXTRAÍDOS
    // ====================================================================================================

    // [CLEAN CODE - SEMANA 3: Do One Thing. Encapsula la lógica de negocio y persistencia]
    private boolean intentarOtorgarLicencia(Socio socio) {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Guard Clauses]
        if (socio == null || socio.isBoatDriver()) {
            return false;
        }
        socio.setBoatDriver(true);
        socioRepository.updateIsBoatDriver(socio.getSocioId(), true);
        return true;
    }

    // [CLEAN CODE - SEMANA 3: Encapsula la lógica de selección de vista de éxito/error]
    private ModelAndView construirVistaResultado(Socio socio, boolean actualizado) {
        if (socio != null && actualizado) {
            ModelAndView mav = new ModelAndView("socioinscripcion/updateIsBoatDriverSuccessView");
            mav.addObject("socio", socio);
            return mav;
        }
        return new ModelAndView("socioinscripcion/updateIsBoatDriverFailView");
    }
}