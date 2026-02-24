package Practica1.Abstract_Factory;

public class Plato {

    public enum TipoPlato{
     
        ENTRANTE, PRINCIPAL, POSTRE, TEMPORADA
   
    }

    public enum TipoAcompanamiento{
     
        ARROZ, PAPAS, VEGETALES, PASTA, NINGUNO
   
    }

    private String nombre;
    private double precio;
    private TipoPlato tipo;
    private TipoAcompanamiento acompanamiento;

    public Plato(String nombre, double precio, TipoPlato tipo) {
            this.nombre = nombre;
            this.precio = precio;
            this.tipo = tipo;
            this.acompanamiento = TipoAcompanamiento.NINGUNO; // Por defecto, sin acompanamiento
    }
    
    
    public void asignarPrecio(double nuevoPrecio) { // Método para asignar o modificar el precio del plato
        this.precio = nuevoPrecio;
    }

    // Método para añadirle un acompanamiento al plato
    public void setAcompanamiento(TipoAcompanamiento acompanamiento) {
        this.acompanamiento = acompanamiento;
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
        if (acompanamiento != TipoAcompanamiento.NINGUNO) {
            texto += " con " + acompanamiento.toString().toLowerCase();
        }
        return texto;
    }

}
