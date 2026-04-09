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
    private LocalDate date;
    private int totalAmount;
    private String userId;
    private int registeredAdults;
    private int registeredKids;

    /**
     * Constructor principal de la clase {@code Inscripcion}.
     * 
     * @param id Identificador único de la inscripción.
     * @param date Fecha en la que se realiza la inscripción.
     * @param totalAmount Importe total de la inscripción.
     * @param userId Identificador del socio que realiza la inscripción.
     * @param registeredAdults Número de adultos registrados en la inscripción (máximo dos).
     * @param registeredKids Número de niños registrados en la inscripción.
     */
    public Inscripcion(int id, LocalDate date, int totalAmount, String userId, int registeredAdults, int registeredKids) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.totalAmount = totalAmount;
        this.registeredAdults = registeredAdults;
        this.registeredKids = registeredKids;
    }

    /**
     * Constructor vacío de la clase {@code Inscripcion}.
     * Permite crear un objeto sin inicializar sus atributos.
     */
    public Inscripcion() {}

    /** @return Identificador único de la inscripción. */
    public int getId() {
        return id;
    }

    /** @param id Nuevo identificador de la inscripción. */
    public void setId(int id) {
        this.id = id;
    }

    /** @return Fecha en la que se realizó la inscripción. */
    public LocalDate getDate() {
        return date;
    }

    /** @param date Nueva fecha de inscripción. */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /** @return Importe total de la inscripción. */
    public int getTotalAmount() {
        return totalAmount;
    }

    /** @param totalAmount Nuevo importe total de la inscripción. */
    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    /** @return Identificador del socio titular que realiza la inscripción. */
    public String getUserId() {
        return userId;
    }

    /** @param userId Nuevo identificador del socio titular. */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** @return Número de adultos registrados en la inscripción. */
    public int getRegisteredAdults() {
        return registeredAdults;
    }

    /** @param registeredAdults Nuevo número de adultos registrados (máximo dos). */
    public void setRegisteredAdults(int registeredAdults) {
        this.registeredAdults = registeredAdults;
    }

    /** @return Número de niños registrados en la inscripción. */
    public int getRegisteredKids() {
        return registeredKids;
    }

    /** @param registeredKids Nuevo número de niños registrados. */
    public void setRegisteredKids(int registeredKids) {
        this.registeredKids = registeredKids;
    }

    /**
     * Devuelve una representación textual del objeto {@code Inscripcion}.
     *
     * @return Cadena con los valores de los atributos de la inscripción.
     */
    @Override
    public String toString() {
        return "Inscripcion{" +
                "Identificador='" + id + '\'' +
                ", Fecha='" + date + '\'' +
                ", Importe total=" + totalAmount +
                ", Socio titular='" + userId + '\'' +
                ", Adultos registrados=" + registeredAdults +
                ", Niños registrados=" + registeredKids +
                '}';
    }
}
