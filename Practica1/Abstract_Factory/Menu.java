package Practica1.Abstract_Factory;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu {
   
    protected List<Plato> platos;
    protected boolean paraLlevar;

    public Menu() { 
        this.platos = new ArrayList<>();
        this.paraLlevar = false;
    }

    // Nombres exactos al diagrama UML
    public void asignarPlato(Plato plato) { 
        this.platos.add(plato);
    }

    public void setParaLlevar(boolean paraLlevar) {
        this.paraLlevar = paraLlevar;
    }

    public List<Plato> obtener_platos() { 
        return this.platos;
    }

    public abstract float calcular_precio(); 
}