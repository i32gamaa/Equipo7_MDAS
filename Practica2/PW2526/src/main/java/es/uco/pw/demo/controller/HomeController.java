package es.uco.pw.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/homeAlquiler")
    public String homeAlquiler() {
        return "alquiler/homeAlquiler"; // -> templates/alquiler/homeAlquiler.html
    }

    @GetMapping("/homeEmbarcacion")
    public String homeEmbarcacion() {
        return "embarcacion/homeEmbarcacion"; // -> templates/embarcacion/homeEmbarcacion.html
    }

    @GetMapping("/findEmbarcacionMenu")
    public String homeFindEmbarcacion() {
        return "embarcacion/findEmbarcacionMenuView"; // -> templates/embarcacion/findEmbarcacionMenuView.html
    }

    @GetMapping("/homePatron")
    public String homePatron() {
        return "patron/homePatron"; // -> templates/patron/homePatron.html
    }

    @GetMapping("/homeReserva")
    public String homeReserva() {
        return "reserva/homeReserva"; // -> templates/reserva/homeReserva.html
    }

    @GetMapping("/homeSocioInscripcion")
    public String homeSocioInscripcion() {
        return "socioinscripcion/homeSocioInscripcion"; // -> templates/socioinscripcion/homeSocioInscripcion.html
    }
}

