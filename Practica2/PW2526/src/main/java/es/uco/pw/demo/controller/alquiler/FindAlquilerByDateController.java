package es.uco.pw.demo.controller.alquiler;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.time.LocalDate;
import java.util.List;

@Controller
public class FindAlquilerByDateController {
    
    @Autowired
    private EmbarcacionRepository embarcacionRepository;

    public FindAlquilerByDateController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
    }

    @GetMapping("/findAlquilerByDate")
    public ModelAndView mostrarFormularioBusqueda() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("alquiler/findAlquilerByDateView");
        return modelAndView;
    }

    @PostMapping("/findAlquilerByDate")
    public ModelAndView procesarBusquedaEmbarcaciones(
            @RequestParam("startDate") String fechaInicioTexto,
            @RequestParam("endDate") String fechaFinTexto) {
        
        ModelAndView modelAndView = new ModelAndView();
        System.out.println("[FindAlquilerByDateController] Fechas recibidas - Inicio: " + fechaInicioTexto + ", Fin: " + fechaFinTexto);

        try {
            LocalDate fechaInicioParsed = LocalDate.parse(fechaInicioTexto);
            LocalDate fechaFinParsed = LocalDate.parse(fechaFinTexto);
            
            if (fechaInicioParsed.isAfter(fechaFinParsed)) {
                modelAndView.setViewName("alquiler/findAlquilerByDateFailView");
                modelAndView.addObject("error", "La fecha de inicio no puede ser posterior a la fecha de fin");
                return modelAndView;
            }
            
            if (fechaInicioParsed.isBefore(LocalDate.now())) {
                modelAndView.setViewName("alquiler/findAlquilerByDateFailView");
                modelAndView.addObject("error", "La fecha de inicio no puede ser anterior a la fecha actual");
                return modelAndView;
            }

            List<Embarcacion> embarcacionesDisponibles = embarcacionRepository.findAvailableByDateRange(fechaInicioParsed, fechaFinParsed);

            if (embarcacionesDisponibles != null && !embarcacionesDisponibles.isEmpty()) {
                System.out.println("[FindAlquilerByDateController] Encontradas " + embarcacionesDisponibles.size() + " embarcaciones");
                modelAndView.setViewName("alquiler/findAlquilerByDateSuccessView");
                modelAndView.addObject("embarcaciones", embarcacionesDisponibles);
                modelAndView.addObject("startDate", fechaInicioParsed);
                modelAndView.addObject("endDate", fechaFinParsed);
            } else {
                modelAndView.setViewName("alquiler/findAlquilerByDateFailView");
                modelAndView.addObject("error", "No se encontraron embarcaciones disponibles para el rango de fechas seleccionado");
            }

        } catch (Exception e) {
            System.err.println("[FindAlquilerByDateController] Error processing dates: " + e.getMessage());
            modelAndView.setViewName("alquiler/findAlquilerByDateFailView");
            modelAndView.addObject("error", "Error en el formato de las fechas. Use el formato YYYY-MM-DD");
        }

        return modelAndView;
    }
}