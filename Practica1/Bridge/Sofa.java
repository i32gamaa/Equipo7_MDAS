package Practica1.Bridge;

public class Sofa extends Producto {
    private int plazas;

    public Sofa(String nombre, double precio, int stock, int plazas) {
        super(nombre, precio, stock);
        this.plazas = plazas;
    }

    public int getPlazas() { return plazas; }

    @Override
    public String toString() {
        return super.toString() + " | Plazas: " + plazas;
    }
}