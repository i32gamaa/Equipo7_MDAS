/**
 * Clase {@code Reserva}:
 * Representa la reserva de un barco para una actividad dentro del club.
 * 
 * <p>Una reserva está asociada a un socio, una embarcación y una fecha concreta.
 * Además, incluye información sobre el propósito de la reserva, el número de plazas
 * ocupadas y el importe total asociado.</p>
 * 
 * <p>Esta clase forma parte del dominio del sistema de gestión del club,
 * permitiendo administrar las reservas de embarcaciones realizadas por los socios.</p>
 * 
 * @author  
 * @version 1.0
 * @since 2025-10-03
 */
package es.uco.pw.demo.model.domain;

import java.time.LocalDate;

public class Reserva {

    private int id;
    private String purpose;
    // REFACTORIZACIÓN (Regla 1): Se cambia 'date' por 'reservationDate' para ser explícito.
    private LocalDate reservationDate;
    // REFACTORIZACIÓN (Regla 1 y 5): Se cambia 'numSeats' por 'numberOfSeats'.
    private int numberOfSeats;
    private int totalAmount;
    private String userId;
    private String registrationNumber;

    public Reserva() {
    }

    public Reserva(int id, String purpose, LocalDate reservationDate, int numberOfSeats, String userId, String registrationNumber) {
        this.id = id;
        this.purpose = purpose;
        this.reservationDate = reservationDate;
        this.numberOfSeats = numberOfSeats;
        this.userId = userId;
        this.registrationNumber = registrationNumber;
        this.totalAmount = numberOfSeats * 40;
    }

    // REFACTORIZACIÓN (Regla 1): Se cambia el parámetro 'registrationNumString' por 'registrationNumber' para mantener la consistencia con el atributo de la clase.
    public Reserva(String userId, String registrationNumber, LocalDate reservationDate, int numberOfSeats, String purpose) {
        this.purpose = purpose;
        this.reservationDate = reservationDate;
        this.numberOfSeats = numberOfSeats;
        this.userId = userId;
        this.registrationNumber = registrationNumber;
        this.totalAmount=numberOfSeats*40;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public LocalDate getReservationDate() { return reservationDate; }
    public void setReservationDate(LocalDate reservationDate) { this.reservationDate = reservationDate; }

    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
        this.totalAmount=numberOfSeats*40;
    }

    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", purpose='" + purpose + '\'' +
                ", reservationDate=" + reservationDate +
                ", numberOfSeats=" + numberOfSeats +
                ", totalAmount=" + totalAmount +
                ", userId='" + userId + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                '}';
    }
}