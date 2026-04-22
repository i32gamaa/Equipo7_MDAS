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

    private ReservaRepository reservaRepository;

    public FindReservaByIdController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.reservaRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/findReservaById")
    public ModelAndView mostrarFormularioBusqueda() {
        return new ModelAndView("reserva/findReservaByIdView");
    }

    @PostMapping("/findReservaById")
    public ModelAndView procesarBusquedaPorId(@RequestParam("id") int idBuscado) {
        Reserva reservaEncontrada = reservaRepository.findById(idBuscado);
        ModelAndView vistaResultados;

        if (reservaEncontrada != null) {
            vistaResultados = new ModelAndView("reserva/findReservaByIdSuccessView");
            vistaResultados.addObject("reserva", reservaEncontrada);
        } else {
            vistaResultados = new ModelAndView("reserva/findReservaByIdFailView");
        }

        return vistaResultados;
    }
}