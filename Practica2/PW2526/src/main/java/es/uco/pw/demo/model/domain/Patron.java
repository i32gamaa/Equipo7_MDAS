/**
 * Clase {@code Patron}:
 * Representa a un patrón del club náutico, es decir, un empleado con titulación
 * que cuenta con la licencia necesaria para pilotar embarcaciones del club.
 * 
 * <p>La clase almacena información personal del patrón, así como la fecha
 * de expedición de su título náutico.</p>
 * 
 * @author  
 * @version 1.0
 * @since 2025-10-03
 */
package es.uco.pw.demo.model.domain;

import java.time.LocalDate;

public class Patron {

    private String id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private LocalDate titleIssueDate;

    /**
     * Constructor completo de la clase {@code Patron}.
     * 
     * @param id Identificador del patrón (DNI o código interno).
     * @param name Nombre del patrón.
     * @param surname Apellidos del patrón.
     * @param birthDate Fecha de nacimiento del patrón.
     * @param titleIssueDate Fecha de expedición del título de patrón.
     */
    public Patron(String id, String name, String surname, LocalDate birthDate, LocalDate titleIssueDate) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.titleIssueDate = titleIssueDate;
    }

    /**
     * Constructor vacío de la clase {@code Patron}.
     * Permite crear un objeto sin inicializar sus atributos.
     */
    public Patron() {}

    /** 
     * Devuelve el identificador del patrón.
     * 
     * @return Identificador del patrón (DNI o número de empleado).
     */
    public String getId() {
        return id;
    }

    /** 
     * Asigna el identificador del patrón.
     * 
     * @param id Nuevo identificador del patrón.
     */
    public void setId(String id) {
        this.id = id;
    }

    /** 
     * Devuelve el nombre del patrón.
     * 
     * @return Nombre del patrón.
     */
    public String getName() {
        return name;
    }

    /** 
     * Asigna el nombre del patrón.
     * 
     * @param name Nuevo nombre del patrón.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** 
     * Devuelve los apellidos del patrón.
     * 
     * @return Apellidos del patrón.
     */
    public String getSurname() {
        return surname;
    }

    /** 
     * Asigna los apellidos del patrón.
     * 
     * @param surname Nuevos apellidos del patrón.
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /** 
     * Devuelve la fecha de nacimiento del patrón.
     * 
     * @return Fecha de nacimiento del patrón.
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /** 
     * Asigna la fecha de nacimiento del patrón.
     * 
     * @param birthDate Nueva fecha de nacimiento.
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /** 
     * Devuelve la fecha de expedición del título de patrón.
     * 
     * @return Fecha de expedición del título náutico.
     */
    public LocalDate getTitleIssueDate() {
        return titleIssueDate;
    }

    /** 
     * Asigna la fecha de expedición del título de patrón.
     * 
     * @param titleIssueDate Nueva fecha de expedición del título.
     */
    public void setTitleIssueDate(LocalDate titleIssueDate) {
        this.titleIssueDate = titleIssueDate;
    }

    /**
     * Devuelve una representación textual del objeto {@code Patron}.
     * 
     * @return Cadena con los valores de los atributos del patrón.
     */
    @Override
    public String toString() {
        return "Patron{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birthDate=" + birthDate +
                ", titleIssueDate=" + titleIssueDate +
                '}';
    }
}

