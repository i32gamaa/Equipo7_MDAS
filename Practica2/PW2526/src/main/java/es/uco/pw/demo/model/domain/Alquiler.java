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
    private int numSeats;
    private double amount;
    private int rentalId;
    private String userId;

    /**
     * Constructor completo de la clase {@code Alquiler}.
     * 
     * @param startDate Fecha de inicio del alquiler.
     * @param endDate Fecha de finalización del alquiler.
     * @param registrationNumber Matrícula de la embarcación alquilada.
     * @param numSeats Número de plazas ocupadas.
     * @param amount Importe total del alquiler.
     * @param rentalId Identificador único del alquiler.
     * @param userId Identificador del socio que realiza el alquiler.
     */
    public Alquiler(LocalDate startDate, LocalDate endDate, String registrationNumber,
                    int numSeats, double amount, int rentalId, String userId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.registrationNumber = registrationNumber;
        this.numSeats = numSeats;
        this.amount = amount;
        this.rentalId = rentalId;
        this.userId = userId;
    }

    /**
     * Constructor vacío de la clase {@code Alquiler}.
     * Permite crear un objeto sin inicializar sus atributos.
     */
    public Alquiler() {}

    /** 
     * Devuelve la fecha de inicio del alquiler.
     * 
     * @return Fecha de inicio.
     */
    public LocalDate getStartDate() { 
        return startDate;
    }

    /** 
     * Asigna la fecha de inicio del alquiler.
     * 
     * @param startDate Nueva fecha de inicio.
     */
    public void setStartDate(LocalDate startDate) { 
        this.startDate = startDate; 
    }

    /** 
     * Devuelve la fecha de finalización del alquiler.
     * 
     * @return Fecha de finalización.
     */
    public LocalDate getEndDate() { 
        return endDate; 
    }

    /** 
     * Asigna la fecha de finalización del alquiler.
     * 
     * @param endDate Nueva fecha de finalización.
     */
    public void setEndDate(LocalDate endDate) { 
        this.endDate = endDate; 
    }

    /** 
     * Devuelve la matrícula de la embarcación alquilada.
     * 
     * @return Matrícula o número de registro de la embarcación.
     */
    public String getRegistrationNumber() { 
        return registrationNumber; 
    }

    /** 
     * Asigna la matrícula de la embarcación alquilada.
     * 
     * @param registrationNumber Nueva matrícula de la embarcación.
     */
    public void setRegistrationNumber(String registrationNumber) { 
        this.registrationNumber = registrationNumber; 
    }

    /** 
     * Devuelve el número de plazas ocupadas durante el alquiler.
     * 
     * @return Número de plazas.
     */
    public int getNumSeats() { 
        return numSeats; 
    }

    /** 
     * Asigna el número de plazas ocupadas durante el alquiler.
     * 
     * @param numSeats Nuevo número de plazas.
     */
    public void setNumSeats(int numSeats) { 
        this.numSeats = numSeats; 
    }

    /** 
     * Devuelve el importe total del alquiler.
     * 
     * @return Importe del alquiler.
     */
    public double getAmount() { 
        return amount; 
    }

    /** 
     * Asigna el importe total del alquiler.
     * 
     * @param amount Nuevo importe total.
     */
    public void setAmount(double amount) { 
        this.amount = amount; 
    }

    /** 
     * Devuelve el identificador único del alquiler.
     * 
     * @return Identificador del alquiler.
     */
    public int getRentalId() { 
        return rentalId; 
    }

    /** 
     * Asigna el identificador único del alquiler.
     * 
     * @param rentalId Nuevo identificador del alquiler.
     */
    public void setRentalId(int rentalId) { 
        this.rentalId = rentalId; 
    }

    /** 
     * Devuelve el identificador del socio que realiza el alquiler.
     * 
     * @return Identificador del socio.
     */
    public String getUserId() { 
        return userId; 
    }

    /** 
     * Asigna el identificador del socio que realiza el alquiler.
     * 
     * @param userId Nuevo identificador del socio.
     */
    public void setUserId(String userId) { 
        this.userId = userId; 
    }

    /**
     * Devuelve una representación textual del objeto {@code Alquiler}.
     * 
     * @return Cadena con los valores de los atributos del alquiler.
     */
    @Override
    public String toString() {
        return "Alquiler{" +
                "id=" + rentalId +
                ", socio='" + userId + '\'' +
                ", matricula='" + registrationNumber + '\'' +
                ", fechaInicio=" + startDate +
                ", fechaFin=" + endDate +
                ", n_plazas=" + numSeats +
                ", importe=" + amount +
                '}';
    }
}
