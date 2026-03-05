package Practica1.Composite;

import java.io.File; // Import añadido para gestionar la creación de carpetas
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static final double PRECIO_KWH = 0.18;

    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE GESTIÓN ENERGÉTICA UCO (INTERACTIVO) ===");
        ContenedorEnergia campus = new ContenedorEnergia("Campus Rabanales");

        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Añadir un Edificio al Campus");
            System.out.println("2. Ver Consumo y Coste en Pantalla");
            System.out.println("3. Generar Informe en Archivo .txt");
            System.out.println("4. Salir");
            System.out.print("Selecciona una opción: ");
            
            int opcion = leerEntero();

            switch (opcion) {
                case 1 -> configurarEdificio(campus);
                case 2 -> mostrarResultados(campus);
                case 3 -> exportarInforme(campus);
                case 4 -> {
                    salir = true;
                    System.out.println("Cerrando sistema... ¡Suerte en la entrega, rey!");
                }
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private static void exportarInforme(ContadorEnergia c) {
        // 1. Definimos la ruta donde queremos guardar el txt
        String rutaCarpeta = "Practica1/Composite"; 
        
        // 2. Nos aseguramos de que la carpeta exista, si no, Java la crea
        File directorio = new File(rutaCarpeta);
        if (!directorio.exists()) {
            directorio.mkdirs(); 
        }

        // 3. Le pegamos la ruta al nombre del archivo
        String nombreArchivo = rutaCarpeta + "/informe_energetico_" + c.getNombre().replace(" ", "_") + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            writer.println("========================================");
            writer.println("   INFORME DE CONSUMO ENERGÉTICO");
            writer.println("========================================");
            writer.println("Entidad: " + c.getNombre());
            writer.printf("Consumo Total: %.2f kWh%n", c.calcularConsumo());
            writer.printf("Coste Estimado (%.2f €/kWh): %.2f €%n", PRECIO_KWH, c.calcularCoste(PRECIO_KWH));
            writer.println("========================================");
            writer.println("Generado por el Sistema Composite v1.0");
            
            System.out.println("Informe generado con éxito en: " + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error al generar el archivo: " + e.getMessage());
        }
    }

    // Métodos de gestión de edificios
    private static void configurarEdificio(ContenedorEnergia campus) {
        System.out.print("Nombre del edificio: ");
        String nombreEd = sc.nextLine();
        ContenedorEnergia edificio = new ContenedorEnergia(nombreEd);

        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- [" + nombreEd + "] ---");
            System.out.println("1. Añadir Sala");
            System.out.println("2. Añadir Aparato suelto");
            System.out.println("3. Guardar Edificio");
            System.out.print("Opción: ");
            int opt = leerEntero();
            if (opt == 1) edificio.añadir(crearSala());
            else if (opt == 2) edificio.añadir(crearAparato());
            else if (opt == 3) { campus.añadir(edificio); volver = true; }
        }
    }

    private static ContenedorEnergia crearSala() {
        System.out.print("Nombre de la sala: ");
        String nombre = sc.nextLine();
        ContenedorEnergia sala = new ContenedorEnergia(nombre);
        System.out.print("¿Cuántos aparatos tiene?: ");
        int num = leerEntero();
        for (int i = 0; i < num; i++) sala.añadir(crearAparato());
        return sala;
    }

    private static AparatoElectrico crearAparato() {
        System.out.print("Nombre aparato: "); String n = sc.nextLine();
        System.out.print("Consumo (kWh): "); double c = leerDouble();
        System.out.print("Horas uso/día: "); double h = leerDouble();
        return new AparatoElectrico(n, c, h);
    }

    private static void mostrarResultados(ContadorEnergia c) {
        System.out.println("\n[REPORTE RÁPIDO]");
        System.out.printf("Entidad: %s | Consumo: %.2f kWh | Coste: %.2f€%n", 
                          c.getNombre(), c.calcularConsumo(), c.calcularCoste(PRECIO_KWH));
    }

    private static int leerEntero() { 
        try { return Integer.parseInt(sc.nextLine()); } catch (Exception e) { return 0; }
    }

    private static double leerDouble() { 
        try { return Double.parseDouble(sc.nextLine()); } catch (Exception e) { return 0.0; }
    }
}