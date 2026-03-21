package Practica1.Facade;

public class Actividad {
    private String nombre;
    private String tipo;
    private String fecha;

    public Actividad(String nombre, String tipo, String fecha) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "- " + nombre + " | Tipo: " + tipo + " | Fecha: " + fecha;
    }
}