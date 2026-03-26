package Practica1.Composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static final double PRECIO_KWH = 0.18;
    
    // Variables globales para la configuracion precargada
    private static ContenedorEnergia campus;
    private static List<ContenedorEnergia> listaEdificios = new ArrayList<>();
    private static List<ContenedorEnergia> listaSalas = new ArrayList<>();

    public static void main(String[] args) {
        // 1. Cargamos toda la configuracion por defecto añadida
        inicializarDatos();

        System.out.println("==================================================");
        System.out.println("  SISTEMA DE GESTION ENERGETICA UCO (COMPOSITE)   ");
        System.out.println("==================================================");

        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Ver Gasto Acumulado (Elegir Campus, Edificio o Sala)");
            System.out.println("2. Salir");
            System.out.print("Selecciona una opcion: ");

            int opcion = leerEntero();

            switch (opcion) {
                case 1 -> verGastoAcumulado();
                case 2 -> {
                    salir = true;
                    System.out.println("Cerrando sistema... A por el 10, hermano.");
                }
                default -> System.out.println("[ERROR] Opcion no valida. Elige 1 o 2.");
            }
        }
    }

    // --- METODO QUE CARGA LOS DATOS  ---
    private static void inicializarDatos() {
        campus = new ContenedorEnergia("Campus Rabanales");

        // --- EDIFICIO 1: AULARIO AVERROES ---
        ContenedorEnergia edificio1 = new ContenedorEnergia("Aulario Averroes");
        
        ContenedorEnergia sala1 = new ContenedorEnergia("Aula 15");
        sala1.añadir(new AparatoElectrico("Proyector Principal", 0.4, 6.0));
        sala1.añadir(new AparatoElectrico("Ordenador Profesor", 0.3, 8.0));
        
        ContenedorEnergia sala2 = new ContenedorEnergia("Aula 16");
        sala2.añadir(new AparatoElectrico("Proyector Principal", 0.4, 4.0));
        sala2.añadir(new AparatoElectrico("Aire Acondicionado", 2.0, 5.0));

        // Aparato fuera de las salas (maquina de vending)
        edificio1.añadir(new AparatoElectrico("Maquina Vending Pasillo", 0.5, 24.0));
        
        edificio1.añadir(sala1);
        edificio1.añadir(sala2);
        
        // --- EDIFICIO 2: EDIFICIO LEONARDO DA VINCI ---
        ContenedorEnergia edificio2 = new ContenedorEnergia("Edificio Leonardo Da Vinci");
        
        ContenedorEnergia sala3 = new ContenedorEnergia("Laboratorio Informatica");
        sala3.añadir(new AparatoElectrico("Servidor Principal", 1.2, 24.0));
        sala3.añadir(new AparatoElectrico("Switch Red", 0.1, 24.0));
        sala3.añadir(new AparatoElectrico("PC Alumno 01", 0.25, 4.0));
        sala3.añadir(new AparatoElectrico("PC Alumno 02", 0.25, 4.0));

        edificio2.añadir(sala3);
        edificio2.añadir(new AparatoElectrico("Maquina Cafe Entrada", 0.3, 24.0)); // Fuera de sala

        // --- ENGANCHAR TODO AL CAMPUS Y A LAS LISTAS ---
        campus.añadir(edificio1);
        campus.añadir(edificio2);

        listaEdificios.add(edificio1);
        listaEdificios.add(edificio2);
        
        listaSalas.add(sala1);
        listaSalas.add(sala2);
        listaSalas.add(sala3);
    }

    // --- OPCION 1: CALCULAR GASTO Y MOSTRAR TABLA ---
    private static void verGastoAcumulado() {
        System.out.println("\n¿De qué nivel quieres ver la tabla de consumo?");
        System.out.println("1. Todo el Campus");
        System.out.println("2. Un Edificio concreto");
        System.out.println("3. Una Sala concreta");
        System.out.print("Elige una opcion: ");
        int nivel = leerEntero();

        switch (nivel) {
            case 1 -> imprimirReporte(campus);
            
            case 2 -> {
                System.out.println("\nElige el edificio:");
                for (int i=0; i<listaEdificios.size(); i++) {
                    System.out.println((i+1) + ". " + listaEdificios.get(i).getNombre());
                }
                System.out.print("Numero: ");
                int index = leerEntero() - 1;
                if (index >= 0 && index < listaEdificios.size()) {
                    imprimirReporte(listaEdificios.get(index));
                } else {
                    System.out.println("[ERROR] Seleccion no valida.");
                }
            }
            
            case 3 -> {
                System.out.println("\nElige la sala:");
                for (int i=0; i<listaSalas.size(); i++) {
                    System.out.println((i+1) + ". " + listaSalas.get(i).getNombre());
                }
                System.out.print("Numero: ");
                int index = leerEntero() - 1;
                if (index >= 0 && index < listaSalas.size()) {
                    imprimirReporte(listaSalas.get(index));
                } else {
                    System.out.println("[ERROR] Seleccion no valida.");
                }
            }
            
            default -> System.out.println("[ERROR] Opcion no valida.");
        }
    }

    // --- METODOS DE IMPRESION (LA TABLA Y EL COMPOSITE) ---
    private static void imprimirReporte(ContadorEnergia sitio) {
        System.out.println("\n===========================================================");
        System.out.println(" REPORTE DE CONSUMO: " + sitio.getNombre().toUpperCase());
        System.out.println("===========================================================");
        System.out.printf("%-25s | %-12s | %-12s%n", "APARATO", "CONSUMO/DIA", "COSTE/DIA");
        System.out.println("-----------------------------------------------------------");
        
        imprimirFilaTabla(sitio);
        
        System.out.println("-----------------------------------------------------------");
        System.out.printf("%-25s | %-8.2f kWh | %-8.2f EUR%n", 
            "TOTAL ACUMULADO:", 
            sitio.calcularConsumo(), 
            sitio.calcularCoste(PRECIO_KWH));
        System.out.println("===========================================================");
    }

    private static void imprimirFilaTabla(ContadorEnergia componente) {
        if (componente instanceof AparatoElectrico) {
            AparatoElectrico ap = (AparatoElectrico) componente;
            System.out.printf("%-25s | %-8.2f kWh | %-8.2f EUR%n", 
                ap.getNombre(), 
                ap.calcularConsumo(), 
                ap.calcularCoste(PRECIO_KWH));
        } else if (componente instanceof ContenedorEnergia) {
            ContenedorEnergia contenedor = (ContenedorEnergia) componente;
            for (ContadorEnergia hijo : contenedor.getComponentes()) {
                imprimirFilaTabla(hijo);
            }
        }
    }

    private static int leerEntero() { 
        try { return Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { return -1; }
    }
}