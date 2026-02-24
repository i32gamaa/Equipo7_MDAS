package Practica1.Abstract_Factory;

public class MenuSemanal extends Menu {

    public MenuSemanal() {
        super(); //Esto quiere decir que se llama al constructor de la clase padre (Menu) para inicializar la lista de platos.  
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
        return "--- Menú Semanal ---";
    }
}