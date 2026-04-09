/**
 * Clase {@code Socio}:
 * Representa a una persona inscrita en la empresa. 
 * 
 * <p>Esta clase modela los datos personales y de inscripción de un socio,
 * incluyendo información básica como nombre, dirección, fecha de nacimiento,
 * y detalles sobre su relación con la empresa (por ejemplo, si es titular de la inscripción
 * o si posee licencia de patrón de embarcación).</p>
 * 
 * @author  
 * @version 1.0
 * @since 2025-10-03
 */
package es.uco.pw.demo.model.domain;

import java.time.LocalDate;

public class Socio {

    private String id;
    private String name;
    private String surname;
    private String address;
    private LocalDate birthdate;
    private LocalDate inscriptionDate;
    private boolean isHolderInscription;
    private boolean isBoatDriver;
    private boolean isAdult;
    private int inscriptionId;

    /**
     * Constructor principal de la clase {@code Socio}.
     * 
     * @param id Identificador del socio.
     * @param name Nombre del socio.
     * @param surname Apellidos del socio.
     * @param address Dirección del socio.
     * @param birthdate Fecha de nacimiento del socio.
     * @param isBoatDriver Indica si el socio posee licencia de patrón.
     */
    public Socio(String id, String name, String surname, String address, LocalDate birthdate, boolean isBoatDriver) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.birthdate = birthdate;
        this.inscriptionDate = LocalDate.now();
        this.isHolderInscription = true;
        this.isBoatDriver = isBoatDriver;
        this.isAdult = true;
    }

    /**
     * Constructor vacío de la clase {@code Socio}.
     * Permite crear un objeto sin inicializar sus atributos.
     */
    public Socio() {
    }

    /** @return Identificador del socio. */
    public String getId() {
        return id;
    }

    /** @param id Nuevo identificador del socio. */
    public void setId(String id) {
        this.id = id;
    }

    /** @return Nombre del socio. */
    public String getName() {
        return name;
    }

    /** @param name Nuevo nombre del socio. */
    public void setName(String name) {
        this.name = name;
    }

    /** @return Apellidos del socio. */
    public String getSurname() {
        return surname;
    }

    /** @param surname Nuevos apellidos del socio. */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /** @return Dirección del socio. */
    public String getAddress() {
        return address;
    }

    /** @param address Nueva dirección del socio. */
    public void setAddress(String address) {
        this.address = address;
    }

    /** @return Fecha de nacimiento del socio. */
    public LocalDate getBirthdate() {
        return birthdate;
    }

    /** @param birthdate Nueva fecha de nacimiento del socio. */
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    /** @return Fecha de inscripción del socio. */
    public LocalDate getInscriptionDate() {
        return inscriptionDate;
    }

    /** @param inscriptionDate Nueva fecha de inscripción. */
    public void setInscriptionDate(LocalDate inscriptionDate) {
        this.inscriptionDate = inscriptionDate;
    }

    /** @return {@code true} si el socio es titular de la inscripción; {@code false} en caso contrario. */
    public boolean getIsHolderInscription() {
        return isHolderInscription;
    }

    /** @param isHolderInscription Valor que indica si el socio es titular de la inscripción. */
    public void setIsHolderInscription(boolean isHolderInscription) {
        this.isHolderInscription = isHolderInscription;
    }

    /** @return {@code true} si el socio tiene licencia de patrón; {@code false} en caso contrario. */
    public boolean getIsBoatDriver() {
        return isBoatDriver;
    }

    /** @param isBoatDriver Valor que indica si el socio tiene licencia de patrón. */
    public void setIsBoatDriver(boolean isBoatDriver) {
        this.isBoatDriver = isBoatDriver;
    }

    /** @return {@code true} si el socio es mayor de edad; {@code false} en caso contrario. */
    public boolean getIsAdult() {
        return isAdult;
    }

    /** @param isAdult Valor que indica si el socio es mayor de edad. */
    public void setIsAdult(boolean isAdult) {
        this.isAdult = isAdult;
    }

    /** @return Identificador de la inscripción asociada al socio. */
    public int getInscriptionId() {
        return inscriptionId;
    }

    /** @param inscriptionId Nuevo identificador de la inscripción asociada. */
    public void setInscriptionId(int inscriptionId) {
        this.inscriptionId = inscriptionId;
    }

    /**
     * Devuelve una representación textual del objeto {@code Socio}.
     *
     * @return Cadena con los valores de los atributos del socio.
     */
    @Override
    public String toString() {
        return "Socio{" +
                "DNI='" + id + '\'' +
                ", Nombre='" + name + '\'' +
                ", Apellidos='" + surname + '\'' +
                ", Dirección='" + address + '\'' +
                ", Fecha de nacimiento=" + birthdate +
                ", Titular de inscripción=" + isHolderInscription +
                ", Patrón de embarcación=" + isBoatDriver +
                ", Es adulto=" + isAdult +
                ", ID Inscripción=" + inscriptionId +
                '}';
    }
}
