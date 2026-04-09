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

    ReservaRepository reservaRepository;

    public FindReservaByIdController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.reservaRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/findReservaById")
    public ModelAndView showForm() {
        return new ModelAndView("reserva/findReservaByIdView");
    }

    @PostMapping("/findReservaById")
    public ModelAndView findReserva(@RequestParam("id") int id) {
        Reserva r = reservaRepository.findById(id);
        ModelAndView mav;

        if (r != null) {
            mav = new ModelAndView("reserva/findReservaByIdSuccessView");
            mav.addObject("reserva", r);
        } else {
            mav = new ModelAndView("reserva/findReservaByIdFailView");
        }

        return mav;
    }

}