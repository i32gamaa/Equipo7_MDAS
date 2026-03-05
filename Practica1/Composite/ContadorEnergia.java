package Practica1.Composite;



//Interfaz Componente: Define el acceso uniforme para elementos simples y compuestos

public interface ContadorEnergia {
    // Método para estimar el consumo global 
    double calcularConsumo(); 
    // Método para calcular el coste total en base a la estimación del consumo
    double calcularCoste(double precioKWh);
    String getNombre();
}
