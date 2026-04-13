/**
 * Clase {@code Inscripcion}:
 * Representa la inscripción de un socio y/o sus familiares al club.
 * 
 * <p>Una inscripción puede estar compuesta por uno o varios miembros de una familia,
 * incluyendo adultos y niños. Además, registra la fecha de inscripción, el importe total
 * y el socio titular que realiza la inscripción.</p>
 * 
 * <p>Esta clase permite gestionar la información básica de las inscripciones
 * realizadas por los socios dentro del sistema del club.</p>
 * 
 * @author  
 * @version 1.0
 * @since 2025-10-03
 */
package es.uco.pw.demo.model.domain;

import java.time.LocalDate;

public class Inscripcion {

    private int id;
    // REFACTORIZACIÓN (Regla 1): Se cambia 'date' por 'registrationDate' para especificar el tipo de fecha.
    private LocalDate registrationDate;
    private int totalAmount;
    private String userId;
    private int registeredAdults;
    private int registeredKids;

    public Inscripcion(int id, LocalDate registrationDate, int totalAmount, String userId, int registeredAdults, int registeredKids) {
        this.id = id;
        this.userId = userId;
        this.registrationDate = registrationDate;
        this.totalAmount = totalAmount;
        this.registeredAdults = registeredAdults;
        this.registeredKids = registeredKids;
    }

    public Inscripcion() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getRegisteredAdults() { return registeredAdults; }
    public void setRegisteredAdults(int registeredAdults) { this.registeredAdults = registeredAdults; }

    public int getRegisteredKids() { return registeredKids; }
    public void setRegisteredKids(int registeredKids) { this.registeredKids = registeredKids; }

    @Override
    public String toString() {
        return "Inscripcion{" +
                "Identificador='" + id + '\'' +
                ", Fecha='" + registrationDate + '\'' +
                ", Importe total=" + totalAmount +
                ", Socio titular='" + userId + '\'' +
                ", Adultos registrados=" + registeredAdults +
                ", Niños registrados=" + registeredKids +
                '}';
    }
}