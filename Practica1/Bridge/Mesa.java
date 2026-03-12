package Practica1.Bridge;

public class Mesa extends Producto {
    private double dimension;

    public Mesa(String nombre, double precio, int stock, double dimension) {
        super(nombre, precio, stock); // Llama al constructor del padre
        this.dimension = dimension;
    }

    public double getDimension() { return dimension; }
    
    @Override
    public String toString() {
        return super.toString() + " | Dimensión: " + dimension + "m";
    }
}