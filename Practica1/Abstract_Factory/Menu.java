package Practica1.Abstract_Factory;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu {
   
    protected List<Plato> platos; 

    public Menu() { 
        this.platos = new ArrayList<>();
    }

    // Nombres exactos al diagrama UML
    public void asignarPlato(Plato plato) { 
        this.platos.add(plato);
    }

    public List<Plato> obtener_platos() { 
        return this.platos;
    }

    public abstract float calcular_precio(); 
}