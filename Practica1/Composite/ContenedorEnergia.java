package practica1.composite;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase Composite: Agrupa elementos y calcula gastos acumulados.
 */
public class ContenedorEnergia implements ContadorEnergia {
    private String nombre;
    // Agregación para contener cualquier objeto ContadorEnergia [cite: 8]
    private List<ContadorEnergia> subContadores = new ArrayList<>();

    public ContenedorEnergia(String nombre) {
        this.nombre = nombre;
    }

    public void añadir(ContadorEnergia elemento) {
        subContadores.add(elemento);
    }

    @Override
    public double calcularConsumo() {
        // Calcula el gasto acumulado de todos los hijos 
        return subContadores.stream()
                .mapToDouble(ContadorEnergia::calcularConsumo)
                .sum();
    }

    @Override
    public double calcularCoste(double precioKWh) {
        return subContadores.stream()
                .mapToDouble(c -> c.calcularCoste(precioKWh))
                .sum();
    }

    @Override
    public String getNombre() {
        return nombre;
    }
}