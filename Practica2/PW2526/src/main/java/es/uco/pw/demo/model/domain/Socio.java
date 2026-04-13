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

    // REFACTORIZACIÓN (Regla 1 y 4): Se cambia 'id' por 'socioId' para ser más específicos.
    private String socioId;
    private String name;
    private String surname;
    private String address;
    private LocalDate birthdate;
    private LocalDate inscriptionDate;
    private boolean isHolderInscription;
    private boolean isBoatDriver;
    private boolean isAdult;
    private int inscriptionId;

    public Socio(String socioId, String name, String surname, String address, LocalDate birthdate, boolean isBoatDriver) {
        this.socioId = socioId;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.birthdate = birthdate;
        this.inscriptionDate = LocalDate.now();
        this.isHolderInscription = true;
        this.isBoatDriver = isBoatDriver;
        this.isAdult = true;
    }

    public Socio() {
    }

    public String getSocioId() { return socioId; }
    public void setSocioId(String socioId) { this.socioId = socioId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getBirthdate() { return birthdate; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }

    public LocalDate getInscriptionDate() { return inscriptionDate; }
    public void setInscriptionDate(LocalDate inscriptionDate) { this.inscriptionDate = inscriptionDate; }

    // REFACTORIZACIÓN: Se renombra el getter siguiendo la convención de Java para booleanos (isX en lugar de getIsX).
    public boolean isHolderInscription() { return isHolderInscription; }
    public void setHolderInscription(boolean holderInscription) { this.isHolderInscription = holderInscription; }

    // REFACTORIZACIÓN: Se renombra el getter.
    public boolean isBoatDriver() { return isBoatDriver; }
    public void setBoatDriver(boolean boatDriver) { this.isBoatDriver = boatDriver; }

    // REFACTORIZACIÓN: Se renombra el getter.
    public boolean isAdult() { return isAdult; }
    public void setAdult(boolean adult) { this.isAdult = adult; }

    public int getInscriptionId() { return inscriptionId; }
    public void setInscriptionId(int inscriptionId) { this.inscriptionId = inscriptionId; }

    @Override
    public String toString() {
        return "Socio{" +
                "DNI='" + socioId + '\'' +
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