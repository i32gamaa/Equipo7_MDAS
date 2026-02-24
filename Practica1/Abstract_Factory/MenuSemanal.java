package Practica1.Abstract_Factory;

import java.util.List;

public class MenuSemanal extends Menu {

    public MenuSemanal() {
        super(); // Llama al constructor del padre para preparar la lista vacía
    }

    @Override
    public double calcularPrecio() {
        double total = 0;
        // Recorremos la lista de platos y sumamos sus precios
        for (Plato plato : this.platos) {
            total += plato.getPrecio();
        }
        return total;
    }
    
    @Override
    public String toString() {
        return "--- Menú Semanal ---";
    }
}
