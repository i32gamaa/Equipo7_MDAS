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
    private int numSeats;
    private double length;
    private double width;
    private double height;
    private String patronId;

    /**
     * Constructor completo de la clase {@code Embarcacion}.
     * 
     * @param registrationNumber Matrícula o número de registro único de la embarcación.
     * @param type Tipo de embarcación.
     * @param name Nombre de la embarcación.
     * @param numSeats Número total de plazas disponibles.
     * @param length Longitud (eslora) en metros.
     * @param width Anchura (manga) en metros.
     * @param height Altura o calado en metros.
     */
    public Embarcacion(String registrationNumber, EmbarcacionType type, String name, int numSeats,
                       double length, double width, double height) {
        this.registrationNumber = registrationNumber;
        this.type = type;
        this.name = name;
        this.numSeats = numSeats;
        this.length = length;
        this.width = width;
        this.height = height;
        this.patronId = null; // Se asignará posteriormente mediante un método externo
    }

    /**
     * Constructor vacío de la clase {@code Embarcacion}.
     * Permite crear un objeto sin inicializar sus atributos.
     */
    public Embarcacion() {
    }

    /** 
     * Devuelve la matrícula o número de registro de la embarcación.
     * 
     * @return Matrícula de la embarcación.
     */
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    /** 
     * Asigna la matrícula o número de registro de la embarcación.
     * 
     * @param registrationNumber Nueva matrícula de la embarcación.
     */
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    /** 
     * Devuelve el tipo de embarcación.
     * 
     * @return Tipo de embarcación.
     */
    public EmbarcacionType getType() {
        return type;
    }

    /** 
     * Asigna el tipo de embarcación.
     * 
     * @param type Nuevo tipo de embarcación.
     */
    public void setType(EmbarcacionType type) {
        this.type = type;
    }

    /** 
     * Devuelve el nombre de la embarcación.
     * 
     * @return Nombre de la embarcación.
     */
    public String getName() {
        return name;
    }

    /** 
     * Asigna el nombre de la embarcación.
     * 
     * @param name Nuevo nombre de la embarcación.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** 
     * Devuelve el número total de plazas de la embarcación.
     * 
     * @return Número de plazas disponibles.
     */
    public int getNumSeats() {
        return numSeats;
    }

    /** 
     * Asigna el número total de plazas de la embarcación.
     * 
     * @param numSeats Nuevo número de plazas.
     */
    public void setNumSeats(int numSeats) {
        this.numSeats = numSeats;
    }

    /** 
     * Devuelve la longitud (eslora) de la embarcación.
     * 
     * @return Longitud en metros.
     */
    public double getLength() {
        return length;
    }

    /** 
     * Asigna la longitud (eslora) de la embarcación.
     * 
     * @param length Nueva longitud en metros.
     */
    public void setLength(double length) {
        this.length = length;
    }

    /** 
     * Devuelve la anchura (manga) de la embarcación.
     * 
     * @return Anchura en metros.
     */
    public double getWidth() {
        return width;
    }

    /** 
     * Asigna la anchura (manga) de la embarcación.
     * 
     * @param width Nueva anchura en metros.
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /** 
     * Devuelve la altura o calado de la embarcación.
     * 
     * @return Altura o calado en metros.
     */
    public double getHeight() {
        return height;
    }

    /** 
     * Asigna la altura o calado de la embarcación.
     * 
     * @param height Nueva altura o calado en metros.
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /** 
     * Devuelve el identificador del patrón asignado a la embarcación.
     * 
     * @return Identificador del patrón asignado.
     */
    public String getPatronId() {
        return patronId;
    }

    /** 
     * Asigna el identificador del patrón a la embarcación.
     * 
     * @param patronId Nuevo identificador del patrón.
     */
    public void setPatronId(String patronId) {
        this.patronId = patronId;
    }

    /**
     * Devuelve una representación textual del objeto {@code Embarcacion}.
     * 
     * @return Cadena con los valores de los atributos de la embarcación.
     */
    @Override
    public String toString() {
        return "Embarcacion{" +
                "registrationNumber='" + registrationNumber + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", numSeats=" + numSeats +
                ", length=" + length +
                ", width=" + width +
                ", height=" + height +
                ", patronId='" + patronId + '\'' +
                '}';
    }
}
