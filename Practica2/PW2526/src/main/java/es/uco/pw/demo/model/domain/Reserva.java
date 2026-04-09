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
    private LocalDate date;
    private int numSeats;
    private int totalAmount;
    private String userId;
    private String registrationNumber;

    /**
     * Constructor vacío de la clase {@code Reserva}.
     * Permite crear un objeto sin inicializar sus atributos.
     */
    public Reserva() {
    }

    /**
     * Constructor completo de la clase {@code Reserva}.
     * 
     * @param id Identificador único de la reserva.
     * @param purpose Motivo o propósito de la reserva.
     * @param date Fecha en la que se realiza la reserva.
     * @param numSeats Número de plazas reservadas.
     * @param userId Identificador del socio que realiza la reserva.
     * @param registrationNumber Matrícula de la embarcación reservada.
     */
    public Reserva(int id, String purpose, LocalDate date, int numSeats, String userId, String registrationNumber) {
        this.id = id;
        this.purpose = purpose;
        this.date = date;
        this.numSeats = numSeats;
        this.userId = userId;
        this.registrationNumber = registrationNumber;
        this.totalAmount = numSeats * 40;
    }

    /**
     * Constructor alternativo sin identificador.
     * 
     * @param userId Identificador del socio que realiza la reserva.
     * @param registrationNumString Matrícula de la embarcación.
     * @param date Fecha de la reserva.
     * @param numSeats Número de plazas reservadas.
     * @param purpose Motivo o propósito de la reserva.
     */
    public Reserva(String userId, String registrationNumString, LocalDate date, int numSeats, String purpose) {
        this.purpose = purpose;
        this.date = date;
        this.numSeats = numSeats;
        this.userId = userId;
        this.registrationNumber = registrationNumString;
        this.totalAmount=numSeats*40;
    }

    /** @return Identificador único de la reserva. */
    public Integer getId() {
        return id;
    }

    /** @param id Nuevo identificador único de la reserva. */
    public void setId(Integer id) {
        this.id = id;
    }

    /** @return Propósito o motivo de la reserva. */
    public String getPurpose() {
        return purpose;
    }

    /** @param purpose Nuevo propósito o motivo de la reserva. */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /** @return Fecha en la que se realiza o usa la reserva. */
    public LocalDate getDate() {
        return date;
    }

    /** @param date Nueva fecha de reserva. */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /** @return Número de plazas reservadas en la embarcación. */
    public int getNumSeats() {
        return numSeats;
    }

    /** @param numSeats Nuevo número de plazas reservadas. */
    public void setNumSeats(int numSeats) {
        this.numSeats = numSeats;
        this.totalAmount=numSeats*40;
    }

    /** @return Importe total de la reserva. */
    public int getTotalAmount() {
        return totalAmount;
    }

    /** @param totalAmount Nuevo importe total de la reserva. */
    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    /** @return Identificador del socio que realiza la reserva. */
    public String getUserId() {
        return userId;
    }

    /** @param userId Nuevo identificador del socio que realiza la reserva. */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** @return Matrícula o número de registro de la embarcación reservada. */
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    /** @param registrationNumber Nueva matrícula de la embarcación reservada. */
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    /**
     * Devuelve una representación textual del objeto {@code Reserva}.
     *
     * @return Cadena con los valores de los atributos de la reserva.
     */
    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", purpose='" + purpose + '\'' +
                ", date=" + date +
                ", numSeats=" + numSeats +
                ", totalAmount=" + totalAmount +
                ", userId='" + userId + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                '}';
    }
}

