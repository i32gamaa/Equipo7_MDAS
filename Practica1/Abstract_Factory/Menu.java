package Practica1.Abstract_Factory;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu {
   
    protected List<Plato> platos; // Lista de platos en el menú Semanal o Temporada

    public Menu() { // Constructor para inicializar la lista de platos
        
        platos = new ArrayList<>();
    
    }

    public void agregarPlato(Plato plato) { // Método para agregar un plato al menú
    
        this.platos.add(plato);
    
    }

    public List<Plato> obtenerPlatos() { // Método para obtener la lista de platos en el menú
    
        return this.platos;
    
    }

    public abstract double calcularPrecio(); // Método abstracto para calcular el precio total del menú

}
