/**
 * Clase {@code Alquiler}:
 * Representa el alquiler de una embarcación realizado por un socio del club náutico.
 * 
 * <p>Esta clase almacena la información de cada operación de alquiler, incluyendo
 * las fechas de inicio y fin, la embarcación alquilada, el número de plazas ocupadas,
 * el importe total, el identificador del alquiler y el socio que lo realiza.</p>
 * 
 * <p>Los objetos de esta clase se utilizan para registrar y gestionar las operaciones
 * de alquiler dentro del sistema del club.</p>
 * 
 * @author  
 * @version 1.0
 * @since 2025-10-03
 */
package es.uco.pw.demo.model.domain;

import java.time.LocalDate;

public class Alquiler {

    private LocalDate startDate;
    private LocalDate endDate;
    private String registrationNumber;
    // REFACTORIZACIÓN (Regla 1 y 5): Se cambia 'numSeats' por 'numberOfSeats' para evitar abreviaturas y mejorar la legibilidad.
    private int numberOfSeats;
    private double amount;
    private int rentalId;
    private String userId;

    public Alquiler(LocalDate startDate, LocalDate endDate, String registrationNumber,
                    int numberOfSeats, double amount, int rentalId, String userId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.registrationNumber = registrationNumber;
        this.numberOfSeats = numberOfSeats;
        this.amount = amount;
        this.rentalId = rentalId;
        this.userId = userId;
    }

    public Alquiler() {}

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public int getRentalId() { return rentalId; }
    public void setRentalId(int rentalId) { this.rentalId = rentalId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "Alquiler{" +
                "id=" + rentalId +
                ", socio='" + userId + '\'' +
                ", matricula='" + registrationNumber + '\'' +
                ", fechaInicio=" + startDate +
                ", fechaFin=" + endDate +
                ", n_plazas=" + numberOfSeats +
                ", importe=" + amount +
                '}';
    }
}