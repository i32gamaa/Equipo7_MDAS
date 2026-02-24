package Practica1.Abstract_Factory;

public class MenuTemporada extends Menu {

    public MenuTemporada() {
        super(); 
    }

    @Override
    public float calcular_precio() {
        float total = 0;
        for (Plato plato : this.platos) {
            total += plato.getPrecio();
        }
        return total;
    }

    @Override
    public String toString() {
        return "--- Menú de Temporada ---";
    }
}