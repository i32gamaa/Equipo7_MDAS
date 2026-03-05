package Practica1.Composite;

import java.util.ArrayList;
import java.util.List;

// ClaseComposite 
public class ContenedorEnergia implements ContadorEnergia {
    private String nombre;
    private List<ContadorEnergia> hijos = new ArrayList<>(); // Agregación 

    public ContenedorEnergia(String nombre) {
        this.nombre = nombre;
    }

    public void añadir(ContadorEnergia elemento) {
        hijos.add(elemento);
    }

    @Override
    public double calcularConsumo() {
        // Suma el consumo de todo lo que tiene dentro 
        return hijos.stream().mapToDouble(ContadorEnergia::calcularConsumo).sum();
    }

    @Override
    public double calcularCoste(double precioKWh) {
        return hijos.stream().mapToDouble(h -> h.calcularCoste(precioKWh)).sum();
    }

    @Override
    public String getNombre() { return nombre; }
}
