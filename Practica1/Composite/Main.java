package practica1.composite;

public class Main {
    public static void main(String[] args) {
        final double PRECIO_LUZ = 0.22; // €/kWh

        // 1. Aparatos en una Sala
        AparatoElectrico pc = new AparatoElectrico("PC Gaming Lab", 0.5, 6);
        AparatoElectrico proyector = new AparatoElectrico("Proyector", 0.3, 3);
        
        ContenedorEnergia sala1 = new ContenedorEnergia("Sala 101");
        sala1.añadir(pc);
        sala1.añadir(proyector);

        // 2. Aparato fuera de sala (Máquina de venta) [cite: 19]
        AparatoElectrico vending = new AparateElectrico("Máquina de Vending", 1.2, 24);

        // 3. Edificio que contiene salas y aparatos sueltos [cite: 19]
        ContenedorEnergia edificio = new ContenedorEnergia("Edificio L6");
        edificio.añadir(sala1);
        edificio.añadir(vending);

        // 4. Campus
        ContenedorEnergia campus = new ContenedorEnergia("Campus Rabanales");
        campus.añadir(edificio);

        // Mostrar resultados [cite: 24]
        System.out.println("--- REPORTE ENERGÉTICO ---");
        imprimir(sala1, PRECIO_LUZ);
        imprimir(edificio, PRECIO_LUZ);
        imprimir(campus, PRECIO_LUZ);
    }

    private static void imprimir(ContadorEnergia c, double p) {
        System.out.printf("Elemento: %-20s | Consumo: %7.2f kWh | Coste: %7.2f€%n", 
                          c.getNombre(), c.calcularConsumo(), c.calcularCoste(p));
    }
}