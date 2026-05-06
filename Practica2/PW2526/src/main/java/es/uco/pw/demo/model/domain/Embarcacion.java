/**
 * Clase {@code Embarcacion}:
 * Representa una embarcación perteneciente al club náutico.
 * ...
 * @author  
 * @version 1.0
 * @since 2025-10-03
 */
package es.uco.pw.demo.model.domain;

public class Embarcacion {

    private String registrationNumber;
    private EmbarcacionType type;
    private String name;
    // REFACTORIZACIÓN (Regla 1 y 5): Se cambia 'numSeats' por 'numberOfSeats' para usar un nombre completo y descriptivo.
    private int numberOfSeats;
    private String patronId;
    
    // SEMANA 4: Extraer Clase. Sustituimos las primitivas sueltas por el nuevo objeto agrupado. [cite: 13, 581]
    private Dimensiones dimensiones;

    public Embarcacion(String registrationNumber, EmbarcacionType type, String name, int numberOfSeats,
                       double length, double width, double height) {
        this.registrationNumber = registrationNumber;
        this.type = type;
        this.name = name;
        this.numberOfSeats = numberOfSeats;
        this.patronId = null;
        // SEMANA 4: Instanciamos el objeto de dimensiones encapsulado. [cite: 13, 581]
        this.dimensiones = new Dimensiones(length, width, height);
    }

    public Embarcacion() {
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public EmbarcacionType getType() { return type; }
    public void setType(EmbarcacionType type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }

    // SEMANA 4: Ocultar Delegación. Mantenemos la interfaz pública pero delegamos la gestión a Dimensiones. [cite: 13, 631]
    public double getLength() { return dimensiones != null ? dimensiones.getLength() : 0; }
    public void setLength(double length) { if(this.dimensiones != null) this.dimensiones.setLength(length); }

    public double getWidth() { return dimensiones != null ? dimensiones.getWidth() : 0; }
    public void setWidth(double width) { if(this.dimensiones != null) this.dimensiones.setWidth(width); }

    public double getHeight() { return dimensiones != null ? dimensiones.getHeight() : 0; }
    public void setHeight(double height) { if(this.dimensiones != null) this.dimensiones.setHeight(height); }

    public String getPatronId() { return patronId; }
    public void setPatronId(String patronId) { this.patronId = patronId; }

    @Override
    public String toString() {
        return "Embarcacion{" +
                "registrationNumber='" + registrationNumber + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", numberOfSeats=" + numberOfSeats +
                ", length=" + getLength() +
                ", width=" + getWidth() +
                ", height=" + getHeight() +
                ", patronId='" + patronId + '\'' +
                '}';
    }
}