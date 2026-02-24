package Practica1.Abstract_Factory;

public class Plato {

    public enum TipoPlato {
        ENTRANTE, PRINCIPAL, POSTRE, TEMPORADA
    }

    public enum TipoAcompanamiento {
        ENSALADA, PATATAS, NINGUNO 
    }

    private String nombre;
    private float precio; 
    private TipoPlato tipo;
    private TipoAcompanamiento acompanamiento;

    public Plato(String nombre, float precio, TipoPlato tipo) {
        this.nombre = nombre;
        this.precio = precio;
        this.tipo = tipo;
        this.acompanamiento = TipoAcompanamiento.NINGUNO;
    }
    
    
    public void asignarPrecio(float nuevoPrecio) { 
        this.precio = nuevoPrecio;
    }

    public void setAcompanamiento(TipoAcompanamiento acompanamiento) {
        this.acompanamiento = acompanamiento;
    }

    public float getPrecio() {
        return this.precio;
    }

    public String getNombre() {
        return this.nombre;
    }

    public TipoPlato getTipo() {
        return this.tipo;
    }

    @Override 
    public String toString() {
        String texto = nombre + " (" + precio + "€)";
        if (acompanamiento != TipoAcompanamiento.NINGUNO) {
            texto += " con " + acompanamiento.toString().toLowerCase();
        }
        return texto;
    }
}