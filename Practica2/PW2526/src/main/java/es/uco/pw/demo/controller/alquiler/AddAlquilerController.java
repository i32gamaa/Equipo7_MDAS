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

    private final AlquilerRepository alquilerRepository;
    private final SocioRepository socioRepository;
    private final EmbarcacionRepository embarcacionRepository;
    private ModelAndView modelAndView = new ModelAndView();

    public AddAlquilerController(AlquilerRepository alquilerRepository, SocioRepository socioRepository, EmbarcacionRepository embarcacionRepository) {
        this.alquilerRepository = alquilerRepository;
        this.socioRepository = socioRepository;
        this.embarcacionRepository = embarcacionRepository;
        System.out.println("=== AddAlquilerController INICIALIZADO ===");
    }

    @GetMapping("/addAlquiler")
    public ModelAndView mostrarFormularioAlquiler(@RequestParam(value = "registrationNumber", required = false) String matricula,
                                         @RequestParam(value = "startDate", required = false) String fechaInicioTexto,
                                         @RequestParam(value = "endDate", required = false) String fechaFinTexto) {
        
        this.modelAndView = new ModelAndView();
        this.modelAndView.setViewName("alquiler/addAlquilerView");
        
        Alquiler alquilerSolicitado = new Alquiler();
        
        if (matricula != null && !matricula.isEmpty()) {
            alquilerSolicitado.setRegistrationNumber(matricula);
            Embarcacion embarcacionEncontrada = embarcacionRepository.findByRegistration(matricula);
            if (embarcacionEncontrada != null) {
                this.modelAndView.addObject("embarcacion", embarcacionEncontrada);
            }
        }
        
        if (fechaInicioTexto != null && !fechaInicioTexto.isEmpty()) {
            try {
                LocalDate fechaInicio = LocalDate.parse(fechaInicioTexto);
                alquilerSolicitado.setStartDate(fechaInicio);
            } catch (Exception e) {
                System.err.println("Error parsing startDate");
            }
        }
        
        if (fechaFinTexto != null && !fechaFinTexto.isEmpty()) {
            try {
                LocalDate fechaFin = LocalDate.parse(fechaFinTexto);
                alquilerSolicitado.setEndDate(fechaFin);
            } catch (Exception e) {
                System.err.println("Error parsing endDate");
            }
        }
        
        this.modelAndView.addObject("newAlquiler", alquilerSolicitado); 
        String mensajeInformativoRestricciones = obtenerRestriccionesTemporales();
        this.modelAndView.addObject("infoMessage", mensajeInformativoRestricciones);
        
        return modelAndView;
    }

    @PostMapping("/addAlquiler")
    public ModelAndView procesarNuevoAlquiler(@ModelAttribute("newAlquiler") Alquiler alquilerSolicitado, SessionStatus estadoSesion) {
        this.modelAndView = new ModelAndView();

        String mensajeErrorValidacion = validarReglasAlquiler(alquilerSolicitado);
        if (mensajeErrorValidacion != null) {
            this.modelAndView.setViewName("alquiler/addAlquilerFailView");
            this.modelAndView.addObject("error", mensajeErrorValidacion);
            this.modelAndView.addObject("newAlquiler", alquilerSolicitado);
            
            if (alquilerSolicitado.getRegistrationNumber() != null) {
                Embarcacion embarcacionEncontrada = embarcacionRepository.findByRegistration(alquilerSolicitado.getRegistrationNumber());
                if (embarcacionEncontrada != null) {
                    this.modelAndView.addObject("embarcacion", embarcacionEncontrada);
                }
            }
            
            estadoSesion.setComplete();
            return modelAndView;
        }

        double importeTotalCalculado = calcularImporteTotal(alquilerSolicitado.getStartDate(), alquilerSolicitado.getEndDate(), alquilerSolicitado.getNumberOfSeats());
        alquilerSolicitado.setAmount(importeTotalCalculado);

        int filasInsertadasDb = alquilerRepository.addAlquiler(alquilerSolicitado);

        if (filasInsertadasDb != -1) {
            this.modelAndView.setViewName("alquiler/addAlquilerSuccessView");
            this.modelAndView.addObject("alquiler", alquilerSolicitado);
            
            Socio socio = socioRepository.findById(alquilerSolicitado.getUserId());
            Embarcacion embarcacionEncontrada = embarcacionRepository.findByRegistration(alquilerSolicitado.getRegistrationNumber());
            long diasAlquiler = ChronoUnit.DAYS.between(alquilerSolicitado.getStartDate(), alquilerSolicitado.getEndDate()) + 1;
            
            this.modelAndView.addObject("socio", socio);
            this.modelAndView.addObject("embarcacion", embarcacionEncontrada);
            this.modelAndView.addObject("days", diasAlquiler);
        } else {
            this.modelAndView.setViewName("alquiler/addAlquilerFailView");
            this.modelAndView.addObject("error", "Error al guardar en la base de datos.");
            this.modelAndView.addObject("newAlquiler", alquilerSolicitado);
        }

        estadoSesion.setComplete();
        return modelAndView;
    }

    private String validarReglasAlquiler(Alquiler alquiler) {
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
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        int startMonth = startDate.getMonthValue();
        
        if ((startMonth >= 10 || startMonth <= 4) && days > 3) {
            return "Entre octubre y abril sólo se pueden alquilar embarcaciones por un máximo de 3 días.";
        }
        if (startMonth >= 5 && startMonth <= 9) {
            if (days < 7) return "Entre mayo y septiembre el alquiler mínimo es de 1 semana (7 días).";
            if (days > 14) return "Entre mayo y septiembre el alquiler máximo es de 2 semanas (14 días).";
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

    private double calcularImporteTotal(LocalDate startDate, LocalDate endDate, int numSeats) {
        try {
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            if (days <= 0) days = 1; 
            return days * numSeats * 20.0;
        } catch (Exception e) {
            return 100.0; 
        }
    }

    private String obtenerRestriccionesTemporales() {
        return "INFORMACIÓN IMPORTANTE:\n" +
               "• Octubre a Abril: Alquiler máximo de 3 días\n" +
               "• Mayo a Septiembre: Alquiler de 1 a 2 semanas (7-14 días)\n" +
               "• Precio: 20 EUR por plaza por día\n" +
               "• Requisitos: Socio mayor de edad con licencia de patrón";
    }
}