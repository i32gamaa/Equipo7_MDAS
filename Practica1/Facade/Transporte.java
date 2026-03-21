package Practica1.Facade;

public class Transporte {
    private String aerolinea;
    private String origen;
    private String destino;
    private double precio;

    public Transporte(String aerolinea, String origen, String destino, double precio) {
        this.aerolinea = aerolinea;
        this.origen = origen;
        this.destino = destino;
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "- " + aerolinea + " | " + origen + " -> " + destino + " | Precio: " + precio + " EUR";
    }
}