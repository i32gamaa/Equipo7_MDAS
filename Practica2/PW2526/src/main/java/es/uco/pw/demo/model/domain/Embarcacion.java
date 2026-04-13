/**
 * Clase {@code Embarcacion}:
 * Representa una embarcación perteneciente al club náutico.
 * 
 * <p>Cada embarcación está identificada de forma única mediante su matrícula
 * y cuenta con información técnica (longitud, anchura, altura, número de plazas)
 * además del tipo de embarcación y el patrón asignado para su manejo.</p>
 * 
 * <p>Esta clase forma parte del modelo de dominio del sistema, permitiendo 
 * gestionar las embarcaciones del club y su disponibilidad.</p>
 * 
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
    private double length;
    private double width;
    private double height;
    private String patronId;

    public Embarcacion(String registrationNumber, EmbarcacionType type, String name, int numberOfSeats,
                       double length, double width, double height) {
        this.registrationNumber = registrationNumber;
        this.type = type;
        this.name = name;
        this.numberOfSeats = numberOfSeats;
        this.length = length;
        this.width = width;
        this.height = height;
        this.patronId = null;
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

    public double getLength() { return length; }
    public void setLength(double length) { this.length = length; }

    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public String getPatronId() { return patronId; }
    public void setPatronId(String patronId) { this.patronId = patronId; }

    @Override
    public String toString() {
        return "Embarcacion{" +
                "registrationNumber='" + registrationNumber + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", numberOfSeats=" + numberOfSeats +
                ", length=" + length +
                ", width=" + width +
                ", height=" + height +
                ", patronId='" + patronId + '\'' +
                '}';
    }
}