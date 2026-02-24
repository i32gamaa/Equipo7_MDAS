package Practica1.Abstract_Factory;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu {
   
    protected List<Plato> platos; // Lista de platos en el menú Semanal o Temporada
    protected boolean paraLlevar;

    public Menu() { // Constructor para inicializar la lista de platos
        platos = new ArrayList<>();
        this.paraLlevar = false;
    }

    public Menu(boolean paraLlevar) {
        this.platos = new ArrayList<>();
        this.paraLlevar = paraLlevar;
    }

    public void agregarPlato(Plato plato) { // Método para agregar un plato al menú
        this.platos.add(plato);
    }

    public List<Plato> obtenerPlatos() { // Método para obtener la lista de platos en el menú
        return this.platos;
    }

    // Calcula el precio total del menú, aplicando recargo si es para llevar
    public double calcularPrecio() {
        double total = 0;
        for (Plato plato : platos) {
            total += plato.getPrecio();
        }
        if (paraLlevar) {
            total *= 1.02; // Recargo del 2%
        }
        return total;
    }

}
