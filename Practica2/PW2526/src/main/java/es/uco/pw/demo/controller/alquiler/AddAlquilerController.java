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

    public AddAlquilerController(AlquilerRepository alquilerRepository, 
                               SocioRepository socioRepository,
                               EmbarcacionRepository embarcacionRepository) {
        this.alquilerRepository = alquilerRepository;
        this.socioRepository = socioRepository;
        this.embarcacionRepository = embarcacionRepository;
        System.out.println("=== AddAlquilerController INICIALIZADO ===");
    }

    @GetMapping("/addAlquiler")
    public ModelAndView getAddAlquilerView(@RequestParam(value = "registrationNumber", required = false) String registrationNumber,
                                         @RequestParam(value = "startDate", required = false) String startDateStr,
                                         @RequestParam(value = "endDate", required = false) String endDateStr) {
        System.out.println("=== AddAlquilerController.getAddAlquilerView() LLAMADO ===");
        
        this.modelAndView = new ModelAndView();
        this.modelAndView.setViewName("alquiler/addAlquilerView");
        
        // Crear nuevo alquiler
        Alquiler newAlquiler = new Alquiler();
        
        // Si vienen parámetros, pre-llenar los datos desde la búsqueda
        if (registrationNumber != null && !registrationNumber.isEmpty()) {
            newAlquiler.setRegistrationNumber(registrationNumber);
            System.out.println("[AddAlquilerController] Pre-llenando matrícula: " + registrationNumber);
            
            // Obtener información de la embarcación para mostrar
            Embarcacion embarcacion = embarcacionRepository.findByRegistration(registrationNumber);
            if (embarcacion != null) {
                this.modelAndView.addObject("embarcacion", embarcacion);
                System.out.println("[AddAlquilerController] Embarcación encontrada: " + embarcacion.getName());
            }
        }
        
        if (startDateStr != null && !startDateStr.isEmpty()) {
            try {
                LocalDate startDate = LocalDate.parse(startDateStr);
                newAlquiler.setStartDate(startDate);
                System.out.println("[AddAlquilerController] Pre-llenando fecha inicio: " + startDate);
            } catch (Exception e) {
                System.err.println("Error parsing startDate: " + e.getMessage());
            }
        }
        
        if (endDateStr != null && !endDateStr.isEmpty()) {
            try {
                LocalDate endDate = LocalDate.parse(endDateStr);
                newAlquiler.setEndDate(endDate);
                System.out.println("[AddAlquilerController] Pre-llenando fecha fin: " + endDate);
            } catch (Exception e) {
                System.err.println("Error parsing endDate: " + e.getMessage());
            }
        }
        
        this.modelAndView.addObject("newAlquiler", newAlquiler);
        
        // Añadir mensaje informativo sobre restricciones temporales
        String infoMessage = getRentalPeriodInfo();
        this.modelAndView.addObject("infoMessage", infoMessage);
        
        System.out.println("=== FIN AddAlquilerController.getAddAlquilerView() ===");
        return modelAndView;
    }

    @PostMapping("/addAlquiler")
    public ModelAndView addAlquiler(@ModelAttribute("newAlquiler") Alquiler newAlquiler, SessionStatus sessionStatus) {
        System.out.println("=== AddAlquilerController.addAlquiler() LLAMADO ===");
        
        this.modelAndView = new ModelAndView();
        
        System.out.println("[AddAlquilerController] Received info: " +
                " startDate=" + newAlquiler.getStartDate() +
                " endDate=" + newAlquiler.getEndDate() +
                " numSeats=" + newAlquiler.getNumSeats() +
                " registrationNumber=" + newAlquiler.getRegistrationNumber() +
                " userId=" + newAlquiler.getUserId() +
                " amount=" + newAlquiler.getAmount());

        // Validaciones completas
        String validationError = isValidAlquiler(newAlquiler);
        if (validationError != null) {
            System.out.println("[AddAlquilerController] Validación fallida: " + validationError);
            this.modelAndView.setViewName("alquiler/addAlquilerFailView");
            this.modelAndView.addObject("error", validationError);
            this.modelAndView.addObject("newAlquiler", newAlquiler);
            
            // Añadir información de la embarcación para mostrar en el error
            if (newAlquiler.getRegistrationNumber() != null) {
                Embarcacion embarcacion = embarcacionRepository.findByRegistration(newAlquiler.getRegistrationNumber());
                if (embarcacion != null) {
                    this.modelAndView.addObject("embarcacion", embarcacion);
                }
            }
            
            sessionStatus.setComplete();
            
            System.out.println("=== FIN AddAlquilerController.addAlquiler() - VALIDACIÓN FALLIDA ===");
            return modelAndView;
        }

        // Calcular el importe automáticamente (20€ x plazas x días)
        double calculatedAmount = calculateAmount(newAlquiler.getStartDate(), newAlquiler.getEndDate(), newAlquiler.getNumSeats());
        newAlquiler.setAmount(calculatedAmount);
        System.out.println("[AddAlquilerController] Importe calculado: " + calculatedAmount);

        int success = alquilerRepository.addAlquiler(newAlquiler);

        if (success!=-1) {
            System.out.println("[AddAlquilerController] Alquiler guardado exitosamente");
            this.modelAndView.setViewName("alquiler/addAlquilerSuccessView");
            this.modelAndView.addObject("alquiler", newAlquiler);
            
            // Añadir información adicional para la vista de éxito
            Socio socio = socioRepository.findById(newAlquiler.getUserId());
            Embarcacion embarcacion = embarcacionRepository.findByRegistration(newAlquiler.getRegistrationNumber());
            long days = ChronoUnit.DAYS.between(newAlquiler.getStartDate(), newAlquiler.getEndDate()) + 1;
            
            this.modelAndView.addObject("socio", socio);
            this.modelAndView.addObject("embarcacion", embarcacion);
            this.modelAndView.addObject("days", days);
            
        } else {
            System.out.println("[AddAlquilerController] Error al guardar alquiler");
            this.modelAndView.setViewName("alquiler/addAlquilerFailView");
            this.modelAndView.addObject("error", 
                "Error al guardar en la base de datos. " +
                "Posibles causas:\n" +
                "- El ID de socio no existe\n" +
                "- La matrícula no existe\n" +
                "- Error de conexión con la base de datos\n\n" +
                "IDs de socio válidos: 11111111C, 22222222D\n" +
                "Matrículas válidas: REG001, REG002");
            this.modelAndView.addObject("newAlquiler", newAlquiler);
        }

        sessionStatus.setComplete();
        
        System.out.println("=== FIN AddAlquilerController.addAlquiler() ===");
        return modelAndView;
    }

    private String isValidAlquiler(Alquiler alquiler) {
        System.out.println("[AddAlquilerController] Validando alquiler...");
        
        // Validaciones básicas de campos obligatorios
        if (alquiler.getStartDate() == null || alquiler.getEndDate() == null) {
            return "Las fechas son obligatorias";
        }

        if (alquiler.getStartDate().isAfter(alquiler.getEndDate())) {
            return "La fecha de inicio no puede ser posterior a la fecha de fin";
        }

        if (alquiler.getRegistrationNumber() == null || alquiler.getRegistrationNumber().trim().isEmpty()) {
            return "La matrícula es obligatoria";
        }

        if (alquiler.getUserId() == null || alquiler.getUserId().trim().isEmpty()) {
            return "El ID de socio es obligatorio";
        }

        if (alquiler.getNumSeats() <= 0) {
            return "El número de plazas debe ser mayor a 0";
        }

        // Validar que las fechas no sean en el pasado
        if (alquiler.getStartDate().isBefore(LocalDate.now())) {
            return "La fecha de inicio no puede ser en el pasado";
        }

        // Validar restricciones temporales
        String periodValidation = validateRentalPeriod(alquiler.getStartDate(), alquiler.getEndDate());
        if (periodValidation != null) {
            return periodValidation;
        }

        // Verificar que el socio existe y es mayor de edad
        String socioValidation = validateSocio(alquiler.getUserId());
        if (socioValidation != null) {
            return socioValidation;
        }

        // Verificar que la embarcación existe
        String embarcacionValidation = validateEmbarcacion(alquiler.getRegistrationNumber());
        if (embarcacionValidation != null) {
            return embarcacionValidation;
        }

        // Verificar capacidad de la embarcación
        if (!hasSufficientCapacity(alquiler.getRegistrationNumber(), alquiler.getNumSeats())) {
            return "La embarcación no tiene suficientes plazas disponibles";
        }

        // Verificar disponibilidad REAL de la embarcación
        if (!isEmbarcacionAvailable(alquiler.getRegistrationNumber(), alquiler.getStartDate(), alquiler.getEndDate())) {
            return "La embarcación no está disponible para las fechas seleccionadas. Ya tiene alquileres en ese período.";
        }

        System.out.println("[AddAlquilerController] Todas las validaciones pasaron correctamente");
        return null; // No hay errores
    }

    private String validateRentalPeriod(LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        int startMonth = startDate.getMonthValue();
        
        System.out.println("[AddAlquilerController] Validando período: " + days + " días, mes: " + startMonth);
        
        // Octubre a Abril (10-4): máximo 3 días
        if ((startMonth >= 10 || startMonth <= 4) && days > 3) {
            return "Entre octubre y abril sólo se pueden alquilar embarcaciones por un máximo de 3 días. " +
                   "Período seleccionado: " + days + " días.";
        }
        
        // Mayo a Septiembre (5-9): 1-2 semanas (7-14 días)
        if (startMonth >= 5 && startMonth <= 9) {
            if (days < 7) {
                return "Entre mayo y septiembre el alquiler mínimo es de 1 semana (7 días). " +
                       "Período seleccionado: " + days + " días.";
            }
            if (days > 14) {
                return "Entre mayo y septiembre el alquiler máximo es de 2 semanas (14 días). " +
                       "Período seleccionado: " + days + " días.";
            }
        }
        
        return null; // No hay errores
    }

    private String validateSocio(String userId) {
        try {
            Socio socio = socioRepository.findById(userId);
            if (socio == null) {
                return "El socio con ID " + userId + " no existe";
            }
            
            if (!socio.getIsAdult()) {
                return "El socio debe ser mayor de edad para realizar alquileres";
            }
            
            if (!socio.getIsBoatDriver()) {
                return "El socio debe tener licencia de patrón para realizar alquileres";
            }

            if(!socio.getIsHolderInscription()){
                return "El socio debe ser titular de la inscripción para realizar alquileres";
            }
            
            return null;
        } catch (Exception e) {
            return "Error al validar el socio: " + e.getMessage();
        }
    }

    private String validateEmbarcacion(String registrationNumber) {
        try {
            Embarcacion embarcacion = embarcacionRepository.findByRegistration(registrationNumber);
            if (embarcacion == null) {
                return "La embarcación con matrícula " + registrationNumber + " no existe";
            }
            return null;
        } catch (Exception e) {
            return "Error al validar la embarcación: " + e.getMessage();
        }
    }

    private boolean isEmbarcacionAvailable(String registrationNumber, LocalDate startDate, LocalDate endDate) {
        try {
            return embarcacionRepository.isEmbarcacionAvailable(registrationNumber, startDate, endDate);
        } catch (Exception e) {
            System.err.println("Error checking availability: " + e.getMessage());
            return false;
        }
    }

    private boolean hasSufficientCapacity(String registrationNumber, int requiredSeats) {
        try {
            Embarcacion embarcacion = embarcacionRepository.findByRegistration(registrationNumber);
            return embarcacion != null && embarcacion.getNumSeats() >= requiredSeats;
        } catch (Exception e) {
            System.err.println("Error checking capacity: " + e.getMessage());
            return false;
        }
    }

    private double calculateAmount(LocalDate startDate, LocalDate endDate, int numSeats) {
        try {
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            if (days <= 0) days = 1; // Mínimo 1 día
            
            // 20€ por plaza por día (como especifica el requerimiento)
            double totalAmount = days * numSeats * 20.0;
            
            System.out.println("[AddAlquilerController] Cálculo: " + days + " días, " + 
                             numSeats + " plazas, importe: " + totalAmount);
            
            return totalAmount;
        } catch (Exception e) {
            System.err.println("[AddAlquilerController] Error calculando importe: " + e.getMessage());
            return 100.0; // Importe por defecto
        }
    }

    private String getRentalPeriodInfo() {
        return "INFORMACIÓN IMPORTANTE:\n" +
               "• Octubre a Abril: Alquiler máximo de 3 días\n" +
               "• Mayo a Septiembre: Alquiler de 1 a 2 semanas (7-14 días)\n" +
               "• Precio: 20€ por plaza por día\n" +
               "• Requisitos: Socio mayor de edad con licencia de patrón";
    }
}