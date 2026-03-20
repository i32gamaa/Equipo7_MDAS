package Practica1.Facade;

public class Alojamiento {
    private String nombre;
    private String ciudad;
    private int capacidad;
    private double precio;

    public Alojamiento(String nombre, String ciudad, int capacidad, double precio) {
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.capacidad = capacidad;
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "🏨 " + nombre + " (" + ciudad + ") | Capacidad: " + capacidad + " pax | Precio/Noche: " + precio + "€";
    }
}