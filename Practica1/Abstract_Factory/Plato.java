package Practica1.Abstract_Factory;

public class Plato {

    public enum TipoPlato{
     
        ENTRANTE, PRINCIPAL, POSTRE, TEMPORADA
   
    }

    public enum TipoAcompañamiento{
     
        ARROZ, PAPAS, VEGETALES, PASTA, NINGUNO
   
    }

    private String nombre;
    private double precio;
    private TipoPlato tipo;
    private TipoAcompañamiento acompañamiento;

    public Plato(String nombre, double precio, TipoPlato tipo) {
            this.nombre = nombre;
            this.precio = precio;
            this.tipo = tipo;
            this.acompañamiento = TipoAcompañamiento.NINGUNO; // Por defecto, sin acompañamiento
    }
    
    
    public void asignarPrecio(double nuevoPrecio) { // Método para asignar o modificar el precio del plato
        this.precio = nuevoPrecio;
    }

    // Método para añadirle un acompañamiento al plato
    public void setAcompanamiento(TipoAcompañamiento acompanamiento) {
        this.acompañamiento = acompanamiento;
    }

    // Getters básicos que necesitará el menú para calcular el total y mostrar el ticket
    public double getPrecio() {
        return this.precio;
    }

    public String getNombre() {
        return this.nombre;
    }

    public TipoPlato getTipo() {
        return this.tipo;
    }

    @Override //Para mostrar el plato con su nombre, precio y guarnición si la tiene
    public String toString() {
        String texto = nombre + " (" + precio + "€)";
        if (acompañamiento != TipoAcompañamiento.NINGUNO) {
            texto += " con " + acompañamiento.toString().toLowerCase();
        }
        return texto;
    }

}
