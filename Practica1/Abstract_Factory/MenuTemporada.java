package Practica1.Abstract_Factory;

public class MenuTemporada extends Menu {

    public MenuTemporada() {
        super(); // Llama al constructor del padre para inicializar la lista de platos
    }

    @Override
    public double calcularPrecio() {
        double total = 0;
        // Recorremos la lista de platos y sumamos sus precios
        for (Plato plato : this.platos) {
            total += plato.getPrecio();
        }
        return total;
    public MenuTemporada(boolean paraLlevar) {
        super(paraLlevar); // Llama al constructor del padre para indicar si es para llevar
    }

    @Override
    public String toString() {
        return "--- Menú de Temporada ---";
    }
}
