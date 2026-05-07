/**
 * Clase {@code Alquiler}:
 * Representa el alquiler de una embarcación realizado por un socio del club náutico.
 * ...
 * @author  
 * @version 1.0
 * @since 2025-10-03
 */
package es.uco.pw.demo.model.domain;

import java.time.LocalDate;

public class Alquiler {

    private String registrationNumber;
    // REFACTORIZACIÓN (Regla 1 y 5): Se cambia 'numSeats' por 'numberOfSeats' para evitar abreviaturas y mejorar la legibilidad.
    private int numberOfSeats;
    private double amount;
    private int rentalId;
    private String userId;

    // SEMANA 4: Extraer Clase. Agrupamos startDate y endDate en un objeto Periodo. [cite: 13, 581]
    private Periodo periodo;

    public Alquiler(LocalDate startDate, LocalDate endDate, String registrationNumber,
                    int numberOfSeats, double amount, int rentalId, String userId) {
        this.registrationNumber = registrationNumber;
        this.numberOfSeats = numberOfSeats;
        this.amount = amount;
        this.rentalId = rentalId;
        this.userId = userId;
        // SEMANA 4: Instanciamos el periodo encapsulado. [cite: 13, 581]
        this.periodo = new Periodo(startDate, endDate);
    }

    public Alquiler() {}

    // SEMANA 4: Ocultar Delegación de las fechas al objeto periodo. [cite: 13, 631]
    public LocalDate getStartDate() { return periodo != null ? periodo.getStartDate() : null; }
    public void setStartDate(LocalDate startDate) { if(this.periodo != null) this.periodo.setStartDate(startDate); }

    public LocalDate getEndDate() { return periodo != null ? periodo.getEndDate() : null; }
    public void setEndDate(LocalDate endDate) { if(this.periodo != null) this.periodo.setEndDate(endDate); }

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
                ", fechaInicio=" + getStartDate() +
                ", fechaFin=" + getEndDate() +
                ", n_plazas=" + numberOfSeats +
                ", importe=" + amount +
                '}';
    }
}