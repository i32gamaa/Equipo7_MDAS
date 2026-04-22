package es.uco.pw.demo.controller.reserva;

import es.uco.pw.demo.model.domain.Reserva;
import es.uco.pw.demo.model.repository.ReservaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

@Controller
public class ListReservasController {

    private final ReservaRepository reservaRepository;
    private ModelAndView modelAndView = new ModelAndView();

    public ListReservasController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.reservaRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/listReservas")
    public ModelAndView mostrarTodasLasReservas() {
        this.modelAndView.setViewName("reserva/listReservasView");
        
        List<Reserva> reservasRegistradas = reservaRepository.findAllReservas();
        
        this.modelAndView.addObject("reservas", reservasRegistradas);
        return modelAndView;
    }
}