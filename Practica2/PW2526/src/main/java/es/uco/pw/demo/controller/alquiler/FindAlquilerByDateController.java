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
    public ModelAndView getFindAlquilerForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("alquiler/findAlquilerByDateView");
        return modelAndView;
    }

    @PostMapping("/findAlquilerByDate")
    public ModelAndView findAlquilerByDate(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        
        ModelAndView modelAndView = new ModelAndView();
        
        System.out.println("[FindAlquilerByDateController] Received dates - Start: " + startDateStr + ", End: " + endDateStr);

        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            
            // Validar que las fechas sean válidas
            if (startDate.isAfter(endDate)) {
                modelAndView.setViewName("alquiler/findAlquilerByDateFailView");
                modelAndView.addObject("error", "La fecha de inicio no puede ser posterior a la fecha de fin");
                return modelAndView;
            }
            
            if (startDate.isBefore(LocalDate.now())) {
                modelAndView.setViewName("alquiler/findAlquilerByDateFailView");
                modelAndView.addObject("error", "La fecha de inicio no puede ser anterior a la fecha actual");
                return modelAndView;
            }

            // Buscar embarcaciones disponibles en el rango de fechas
            List<Embarcacion> embarcacionesDisponibles = embarcacionRepository.findAvailableByDateRange(startDate, endDate);

            if (embarcacionesDisponibles != null && !embarcacionesDisponibles.isEmpty()) {
                System.out.println("[FindAlquilerByDateController] Found " + embarcacionesDisponibles.size() + " embarcaciones disponibles");
                modelAndView.setViewName("alquiler/findAlquilerByDateSuccessView");
                modelAndView.addObject("embarcaciones", embarcacionesDisponibles);
                modelAndView.addObject("startDate", startDate);
                modelAndView.addObject("endDate", endDate);
            } else {
                System.out.println("[FindAlquilerByDateController] No embarcaciones found for the date range");
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