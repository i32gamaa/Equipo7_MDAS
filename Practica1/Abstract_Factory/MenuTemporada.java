package Practica1.Abstract_Factory;

public class MenuTemporada extends Menu {

    public MenuTemporada() {
        super(); 
    }

    @Override
    public float calcular_precio() {
        double total = 0;
        
        // 1. Sumamos los precios base de todos los platos
        for (Plato p : platos) {
            total += p.getPrecio();
        }
        
        // 2. Si es para llevar, le metemos el sablazo del 2% al total
        if (this.paraLlevar) {
            total = total * 1.02f; 
        }
        
        return (float) total;
    }

    @Override
    public String toString() {
        return "--- Menú de Temporada ---";
    }
}