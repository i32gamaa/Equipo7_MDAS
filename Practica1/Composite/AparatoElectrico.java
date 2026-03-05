package Practica1.Composite;


/**
 * Clase Elemento: Representa aparatos eléctricos individuales.
 */
public class AparatoElectrico implements ContadorEnergia {
    private String nombre;
    private double consumoPorHora; // Consumo en kWh
    private double horasUso;      // Horas estimadas en uso

    public AparatoElectrico(String nombre, double consumoPorHora, double horasUso) {
        this.nombre = nombre;
        this.consumoPorHora = consumoPorHora;
        this.horasUso = horasUso;
    }

    @Override
    public double calcularConsumo() {
        return consumoPorHora * horasUso;
    }

    @Override
    public double calcularCoste(double precioKWh) {
        return calcularConsumo() * precioKWh;
    }

    @Override
    public String getNombre() {
        return nombre;
    }
}
