package Practica1.Composite;

/**
 * Interfaz Componente: Define el acceso uniforme para elementos simples y compuestos[cite: 6, 9].
 */
public interface ContadorEnergia {
    // Método para estimar el consumo global [cite: 20]
    double calcularConsumo(); 
    // Método para calcular el coste total en base a la estimación [cite: 20]
    double calcularCoste(double precioKWh);
    String getNombre();
}