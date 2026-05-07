package es.uco.pw.demo.controller.alquiler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import es.uco.pw.demo.model.domain.Alquiler;
import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.repository.AlquilerRepository;
import es.uco.pw.demo.model.repository.SocioRepository;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
public class AddAlquilerController {

    // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Replace Magic Number with Symbolic Constant]
    private static final double PRECIO_POR_PLAZA_DIA = 20.0;
    private static final double PRECIO_POR_DEFECTO_ERROR = 100.0;
    private static final int MAXIMO_DIAS_INVIERNO = 3;
    private static final int MINIMO_DIAS_VERANO = 7;
    private static final int MAXIMO_DIAS_VERANO = 14;
    private static final int MES_INICIO_VERANO = 5; 
    private static final int MES_FIN_VERANO = 9;   

    private final AlquilerRepository alquilerRepository;
    private final SocioRepository socioRepository;
    private final EmbarcacionRepository embarcacionRepository;

    public AddAlquilerController(AlquilerRepository alquilerRepository, SocioRepository socioRepository, EmbarcacionRepository embarcacionRepository) {
        this.alquilerRepository = alquilerRepository;
        this.socioRepository = socioRepository;
        this.embarcacionRepository = embarcacionRepository;
    }

    // [CLEAN CODE - SEMANA 3: Un solo nivel de abstracción. El método se lee como una historia indicando QUÉ hace, ocultando el CÓMO]
    @GetMapping("/addAlquiler")
    public ModelAndView mostrarFormularioAlquiler(@RequestParam(value = "registrationNumber", required = false) String matricula,
                                         @RequestParam(value = "startDate", required = false) String fechaInicioTexto,
                                         @RequestParam(value = "endDate", required = false) String fechaFinTexto) {
        
        Alquiler alquilerSolicitado = inicializarAlquilerConParametros(matricula, fechaInicioTexto, fechaFinTexto);
        return construirVistaFormulario(alquilerSolicitado, matricula);
    }

    // [CLEAN CODE - SEMANA 3: Un solo nivel de abstracción y Do One Thing. Procesa el alquiler en pasos lógicos claros]
    @PostMapping("/addAlquiler")
    public ModelAndView procesarNuevoAlquiler(@ModelAttribute("newAlquiler") Alquiler alquilerSolicitado, SessionStatus estadoSesion) {
        
        String mensajeErrorValidacion = validarReglasAlquiler(alquilerSolicitado);
        
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Replace Nested Conditional with Guard Clauses]
        if (mensajeErrorValidacion != null) {
            estadoSesion.setComplete();
            return construirVistaErrorValidacion(mensajeErrorValidacion, alquilerSolicitado);
        }

        asignarImporteCalculado(alquilerSolicitado);
        int filasInsertadasDb = alquilerRepository.addAlquiler(alquilerSolicitado);
        estadoSesion.setComplete();

        if (filasInsertadasDb != -1) {
            return construirVistaExito(alquilerSolicitado);
        } else {
            return construirVistaErrorBaseDatos(alquilerSolicitado);
        }
    }

    // ====================================================================================================
    // [CLEAN CODE - SEMANA 3: Extracción de lógica compleja a métodos privados (Stepdown Rule)]
    // ====================================================================================================

    private Alquiler inicializarAlquilerConParametros(String matricula, String fechaInicioTexto, String fechaFinTexto) {
        Alquiler alquiler = new Alquiler();
        if (matricula != null && !matricula.isEmpty()) alquiler.setRegistrationNumber(matricula);
        if (fechaInicioTexto != null && !fechaInicioTexto.isEmpty()) {
            try { alquiler.setStartDate(LocalDate.parse(fechaInicioTexto)); } catch (Exception ignored) {}
        }
        if (fechaFinTexto != null && !fechaFinTexto.isEmpty()) {
            try { alquiler.setEndDate(LocalDate.parse(fechaFinTexto)); } catch (Exception ignored) {}
        }
        return alquiler;
    }

    private ModelAndView construirVistaFormulario(Alquiler alquiler, String matricula) {
        ModelAndView modelAndView = new ModelAndView("alquiler/addAlquilerView");
        modelAndView.addObject("newAlquiler", alquiler);
        modelAndView.addObject("infoMessage", obtenerRestriccionesTemporales());
        
        if (matricula != null && !matricula.isEmpty()) {
            Embarcacion embarcacionEncontrada = embarcacionRepository.findByRegistration(matricula);
            if (embarcacionEncontrada != null) {
                modelAndView.addObject("embarcacion", embarcacionEncontrada);
            }
        }
        return modelAndView;
    }

    private ModelAndView construirVistaErrorValidacion(String error, Alquiler alquiler) {
        ModelAndView modelAndView = new ModelAndView("alquiler/addAlquilerFailView");
        modelAndView.addObject("error", error);
        modelAndView.addObject("newAlquiler", alquiler);
        
        if (alquiler.getRegistrationNumber() != null) {
            Embarcacion embarcacionEncontrada = embarcacionRepository.findByRegistration(alquiler.getRegistrationNumber());
            if (embarcacionEncontrada != null) {
                modelAndView.addObject("embarcacion", embarcacionEncontrada);
            }
        }
        return modelAndView;
    }

    private ModelAndView construirVistaExito(Alquiler alquiler) {
        ModelAndView modelAndView = new ModelAndView("alquiler/addAlquilerSuccessView");
        Socio socio = socioRepository.findById(alquiler.getUserId());
        Embarcacion embarcacion = embarcacionRepository.findByRegistration(alquiler.getRegistrationNumber());
        long diasAlquiler = ChronoUnit.DAYS.between(alquiler.getStartDate(), alquiler.getEndDate()) + 1;
        
        modelAndView.addObject("alquiler", alquiler);
        modelAndView.addObject("socio", socio);
        modelAndView.addObject("embarcacion", embarcacion);
        modelAndView.addObject("days", diasAlquiler);
        return modelAndView;
    }

    private ModelAndView construirVistaErrorBaseDatos(Alquiler alquiler) {
        ModelAndView modelAndView = new ModelAndView("alquiler/addAlquilerFailView");
        modelAndView.addObject("error", "Error al guardar en la base de datos.");
        modelAndView.addObject("newAlquiler", alquiler);
        return modelAndView;
    }

    private void asignarImporteCalculado(Alquiler alquiler) {
        try {
            long diasAlquiler = ChronoUnit.DAYS.between(alquiler.getStartDate(), alquiler.getEndDate());
            if (diasAlquiler <= 0) diasAlquiler = 1; 
            // [REFACTORIZACIÓN MANUAL - Uso de constante simbólica]
            alquiler.setAmount(diasAlquiler * alquiler.getNumberOfSeats() * PRECIO_POR_PLAZA_DIA);
        } catch (Exception e) {
            alquiler.setAmount(PRECIO_POR_DEFECTO_ERROR); 
        }
    }

    private String validarReglasAlquiler(Alquiler alquiler) {
        // [REFACTORIZACIÓN MANUAL - Refactoring Guru: Guard Clauses] 
        if (alquiler.getStartDate() == null || alquiler.getEndDate() == null) return "Las fechas son obligatorias";
        if (alquiler.getStartDate().isAfter(alquiler.getEndDate())) return "La fecha de inicio no puede ser posterior a la fecha de fin";
        if (alquiler.getRegistrationNumber() == null || alquiler.getRegistrationNumber().trim().isEmpty()) return "La matrícula es obligatoria";
        if (alquiler.getUserId() == null || alquiler.getUserId().trim().isEmpty()) return "El ID de socio es obligatorio";
        if (alquiler.getNumberOfSeats() <= 0) return "El número de plazas debe ser mayor a 0";
        if (alquiler.getStartDate().isBefore(LocalDate.now())) return "La fecha de inicio no puede ser en el pasado";

        String periodValidation = validateRentalPeriod(alquiler.getStartDate(), alquiler.getEndDate());
        if (periodValidation != null) return periodValidation;

        String socioValidation = validateSocio(alquiler.getUserId());
        if (socioValidation != null) return socioValidation;

        String embarcacionValidation = validateEmbarcacion(alquiler.getRegistrationNumber());
        if (embarcacionValidation != null) return embarcacionValidation;

        if (!hasSufficientCapacity(alquiler.getRegistrationNumber(), alquiler.getNumberOfSeats())) {
            return "La embarcación no tiene suficientes plazas disponibles";
        }

        if (!isEmbarcacionAvailable(alquiler.getRegistrationNumber(), alquiler.getStartDate(), alquiler.getEndDate())) {
            return "La embarcación no está disponible para las fechas seleccionadas.";
        }

        return null;
    }

    private String validateRentalPeriod(LocalDate startDate, LocalDate endDate) {
        long dias = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        int mesInicio = startDate.getMonthValue();
        
        // [REFACTORIZACIÓN MANUAL - Uso de constantes simbólicas en lugar de números sueltos]
        boolean esEpocaInvernal = (mesInicio < MES_INICIO_VERANO || mesInicio > MES_FIN_VERANO);
        
        if (esEpocaInvernal && dias > MAXIMO_DIAS_INVIERNO) {
            return "Entre octubre y abril sólo se pueden alquilar embarcaciones por un máximo de " + MAXIMO_DIAS_INVIERNO + " días.";
        }
        if (!esEpocaInvernal) {
            if (dias < MINIMO_DIAS_VERANO) return "Entre mayo y septiembre el alquiler mínimo es de " + MINIMO_DIAS_VERANO + " días.";
            if (dias > MAXIMO_DIAS_VERANO) return "Entre mayo y septiembre el alquiler máximo es de " + MAXIMO_DIAS_VERANO + " días.";
        }
        return null;
    }

    private String validateSocio(String userId) {
        try {
            Socio socio = socioRepository.findById(userId);
            if (socio == null) return "El socio con ID " + userId + " no existe";
            if (!socio.isAdult()) return "El socio debe ser mayor de edad para realizar alquileres";
            if (!socio.isBoatDriver()) return "El socio debe tener licencia de patrón para realizar alquileres";
            if (!socio.isHolderInscription()) return "El socio debe ser titular de la inscripción para realizar alquileres";
            return null;
        } catch (Exception e) {
            return "Error al validar el socio";
        }
    }

    private String validateEmbarcacion(String registrationNumber) {
        try {
            Embarcacion embarcacion = embarcacionRepository.findByRegistration(registrationNumber);
            if (embarcacion == null) return "La embarcación no existe";
            return null;
        } catch (Exception e) {
            return "Error al validar la embarcación";
        }
    }

    private boolean isEmbarcacionAvailable(String registrationNumber, LocalDate startDate, LocalDate endDate) {
        try {
            return embarcacionRepository.isEmbarcacionAvailable(registrationNumber, startDate, endDate);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasSufficientCapacity(String registrationNumber, int requiredSeats) {
        try {
            Embarcacion embarcacion = embarcacionRepository.findByRegistration(registrationNumber);
            return embarcacion != null && embarcacion.getNumberOfSeats() >= requiredSeats;
        } catch (Exception e) {
            return false;
        }
    }

    private String obtenerRestriccionesTemporales() {
        return "INFORMACIÓN IMPORTANTE:\n" +
               "• Octubre a Abril: Alquiler máximo de " + MAXIMO_DIAS_INVIERNO + " días\n" +
               "• Mayo a Septiembre: Alquiler de " + MINIMO_DIAS_VERANO + " a " + MAXIMO_DIAS_VERANO + " días\n" +
               "• Precio: " + PRECIO_POR_PLAZA_DIA + " EUR por plaza por día\n" +
               "• Requisitos: Socio mayor de edad con licencia de patrón";
    }
}