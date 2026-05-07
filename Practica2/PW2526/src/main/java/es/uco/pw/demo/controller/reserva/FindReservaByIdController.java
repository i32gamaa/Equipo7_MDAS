package es.uco.pw.demo.controller.reserva;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import es.uco.pw.demo.model.domain.Reserva;
import es.uco.pw.demo.model.repository.ReservaRepository;

@Controller
public class FindReservaByIdController {

    private final ReservaRepository reservaRepository;

    public FindReservaByIdController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @GetMapping("/findReservaById")
    public ModelAndView mostrarFormularioBusqueda() {
        return new ModelAndView("reserva/findReservaByIdView");
    }

    // [CLEAN CODE - SEMANA 3: Un solo nivel de abstracción. El flujo es: buscar -> construir respuesta]
    @PostMapping("/findReservaById")
    // [REFACTORIZACIÓN AUTOMÁTICA - VS Code Rename Symbol (F2): Se renombró la variable 'id' a 'idBuscado' para darle contexto sin romper el código]
    public ModelAndView procesarBusquedaPorId(@RequestParam("id") int idBuscado) {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Inline Temp]
        // En lugar de guardar 'Reserva reservaEncontrada = ...', lo inyectamos directo al constructor.
        return construirVistaResultado(reservaRepository.findById(idBuscado));
    }

    // ====================================================================================================
    // MÉTODOS PRIVADOS EXTRAÍDOS
    // ====================================================================================================

    // [CLEAN CODE - SEMANA 3: Do One Thing. Se encarga exclusivamente de la lógica condicional de la vista]
    private ModelAndView construirVistaResultado(Reserva reserva) {
        if (reserva != null) {
            ModelAndView mav = new ModelAndView("reserva/findReservaByIdSuccessView");
            mav.addObject("reserva", reserva);
            return mav;
        }
        return new ModelAndView("reserva/findReservaByIdFailView");
    }
}