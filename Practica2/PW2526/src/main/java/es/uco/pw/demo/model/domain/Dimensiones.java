package es.uco.pw.demo.model.domain;

// SEMANA 4: Extraer Clase. Agrupamos los datos primitivos de dimensiones en un objeto cohesivo.
public class Dimensiones {
    private double length;
    private double width;
    private double height;

    public Dimensiones(double length, double width, double height) {
        this.length = length;
        this.width = width;
        this.height = height;
    }

    public double getLength() { return length; }
    public void setLength(double length) { this.length = length; }

    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
}