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
        return new ModelAndView("alquiler/findAlquilerByDateView");
    }

    // [CLEAN CODE - SEMANA 3: El método principal se lee como una secuencia lógica. El manejo de errores y carga de vistas se ha extraído]
    @PostMapping("/findAlquilerByDate")
    public ModelAndView procesarBusquedaEmbarcaciones(
            @RequestParam("startDate") String fechaInicioTexto,
            @RequestParam("endDate") String fechaFinTexto) {

        try {
            LocalDate fechaInicioParsed = LocalDate.parse(fechaInicioTexto);
            LocalDate fechaFinParsed = LocalDate.parse(fechaFinTexto);
            
            // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Replace Nested Conditional with Guard Clauses]
            String mensajeErrorValidacion = validarRangoFechas(fechaInicioParsed, fechaFinParsed);
            if (mensajeErrorValidacion != null) {
                return construirVistaFallo(mensajeErrorValidacion);
            }

            return buscarEmbarcacionesYConstruirVista(fechaInicioParsed, fechaFinParsed);

        } catch (Exception e) {
            return construirVistaFallo("Error en el formato de las fechas. Use el formato YYYY-MM-DD");
        }
    }

    // ====================================================================================================
    // [CLEAN CODE - SEMANA 3: Funciones pequeñas de un solo propósito extraídas]
    // ====================================================================================================

    // [CLEAN CODE - SEMANA 3: Extracción de validaciones de fechas]
    // [REFACTORIZACIÓN AUTOMÁTICA - VS Code Rename Symbol (F2): Renombrado múltiple de parámetros 'inicio' a 'fechaInicioRango' y 'fin' a 'fechaFinRango']
    private String validarRangoFechas(LocalDate fechaInicioRango, LocalDate fechaFinRango) {
        // [REFACTORIZACIÓN AUTOMÁTICA - VS Code Extract Local Variable: Se ha extraído la llamada a 'LocalDate.now()' para evitar instanciarla repetidas veces internamente]
        LocalDate hoy = LocalDate.now();

        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Consolidate Conditional Expression]
        if (fechaInicioRango.isAfter(fechaFinRango)) return "La fecha de inicio no puede ser posterior a la fecha de fin";
        if (fechaInicioRango.isBefore(hoy)) return "La fecha de inicio no puede ser anterior a la fecha actual";
        return null;
    }

    // [CLEAN CODE - SEMANA 3: Extracción de la lógica de negocio y carga del ModelAndView]
    private ModelAndView buscarEmbarcacionesYConstruirVista(LocalDate inicio, LocalDate fin) {
        List<Embarcacion> embarcacionesDisponibles = embarcacionRepository.findAvailableByDateRange(inicio, fin);

        if (embarcacionesDisponibles != null && !embarcacionesDisponibles.isEmpty()) {
            ModelAndView modelAndView = new ModelAndView("alquiler/findAlquilerByDateSuccessView");
            modelAndView.addObject("embarcaciones", embarcacionesDisponibles);
            modelAndView.addObject("startDate", inicio);
            modelAndView.addObject("endDate", fin);
            return modelAndView;
        } 
        
        return construirVistaFallo("No se encontraron embarcaciones disponibles para el rango de fechas seleccionado");
    }

    // [CLEAN CODE - SEMANA 3: DRY (Don't Repeat Yourself). Un único método para construir vistas de error]
    private ModelAndView construirVistaFallo(String mensajeError) {
        ModelAndView modelAndView = new ModelAndView("alquiler/findAlquilerByDateFailView");
        modelAndView.addObject("error", mensajeError);
        return modelAndView;
    }
}