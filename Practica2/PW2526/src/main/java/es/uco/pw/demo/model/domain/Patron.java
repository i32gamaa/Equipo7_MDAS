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

    // REFACTORIZACIÓN (Regla 1 y 4): Se cambia 'id' por 'patronId' para evitar ambigüedades.
    private String patronId;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private LocalDate titleIssueDate;

    public Patron(String patronId, String name, String surname, LocalDate birthDate, LocalDate titleIssueDate) {
        this.patronId = patronId;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.titleIssueDate = titleIssueDate;
    }

    public Patron() {}

    public String getPatronId() { return patronId; }
    public void setPatronId(String patronId) { this.patronId = patronId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public LocalDate getTitleIssueDate() { return titleIssueDate; }
    public void setTitleIssueDate(LocalDate titleIssueDate) { this.titleIssueDate = titleIssueDate; }

    @Override
    public String toString() {
        return "Patron{" +
                "id='" + patronId + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birthDate=" + birthDate +
                ", titleIssueDate=" + titleIssueDate +
                '}';
    }
}