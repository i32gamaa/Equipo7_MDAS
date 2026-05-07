package es.uco.pw.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    // [CLEAN CODE - SEMANA 3: Función pura con un solo propósito: dirigir a la raíz]
    @GetMapping("/")
    public String mostrarPaginaInicio() {
        return "home";
    }

    // [CLEAN CODE - SEMANA 3: Aplicación de nombres descriptivos (Semana 1) y nivel único de abstracción]
    @GetMapping("/homeAlquiler")
    public String mostrarMenuAlquiler() {
        return "alquiler/homeAlquiler"; 
    }

    @GetMapping("/homeEmbarcacion")
    public String mostrarMenuEmbarcacion() {
        return "embarcacion/homeEmbarcacion"; 
    }

    @GetMapping("/findEmbarcacionMenu")
    public String mostrarMenuBusquedaEmbarcacion() {
        return "embarcacion/findEmbarcacionMenuView"; 
    }

    @GetMapping("/homePatron")
    public String mostrarMenuPatron() {
        return "patron/homePatron"; 
    }

    @GetMapping("/homeReserva")
    public String mostrarMenuReserva() {
        return "reserva/homeReserva"; 
    }

    @GetMapping("/homeSocioInscripcion")
    public String mostrarMenuSocio() {
        return "socioinscripcion/homeSocioInscripcion"; 
    }
}