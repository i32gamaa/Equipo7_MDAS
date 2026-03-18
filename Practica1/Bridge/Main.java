package Practica1.Bridge;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // 1. Instanciamos las empresas proveedoras en la "nube" de nuestro sistema
        IEmpresaProveedora empresaA = new EmpresaA(); // Vende solo Sofás
        IEmpresaProveedora empresaB = new EmpresaB(); // Vende solo Mesas
        IEmpresaProveedora empresaC = new EmpresaC(); // Vende Mesas y Sofás

        boolean salir = false;
        
        System.out.println("==================================================");
        System.out.println("  🛋️ BIENVENIDO AL COMPARADOR DE MUEBLES V1.0 🪑");
        System.out.println("==================================================");

        while (!salir) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Buscar Sofás");
            System.out.println("2. Buscar Mesas");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");
            
            int opcion = leerEntero();

            switch (opcion) {
                case 1 -> menuSofas(empresaA, empresaB, empresaC);
                case 2 -> menuMesas(empresaA, empresaB, empresaC);
                case 3 -> {
                    salir = true;
                    System.out.println("Cerrando el comparador... ¡Hasta la próxima!");
                }
                default -> System.out.println("Opción no válida. Por favor, elige 1, 2 o 3.");
            }
        }
    }

    // MENÚ SOFÁS 
    private static void menuSofas(IEmpresaProveedora empA, IEmpresaProveedora empB, IEmpresaProveedora empC) {
        System.out.print("\n¿De cuántas plazas buscas el sofá? (ej. 2, 3, 4): ");
        int plazas = leerEntero();

        BuscadorSofas buscador = new BuscadorSofas(plazas);
        // Conectamos las empresas al buscador (El Puente)
        buscador.addProveedor(empA);
        buscador.addProveedor(empB);
        buscador.addProveedor(empC);

        mostrarResultados(buscador);
    }

    // MENÚ MESAS
    private static void menuMesas(IEmpresaProveedora empA, IEmpresaProveedora empB, IEmpresaProveedora empC) {
        System.out.print("\n¿Qué dimensión buscas para la mesa? (en metros, ej. 2.0 o 1.5): ");
        double dimension = leerDouble();

        BuscadorMesas buscador = new BuscadorMesas(dimension);
        // Conectamos las empresas al buscador (El Puente)
        buscador.addProveedor(empA);
        buscador.addProveedor(empB);
        buscador.addProveedor(empC);

        mostrarResultados(buscador);
    }

    // LÓGICA DE ORDENACIÓN Y MUESTRA 
    private static void mostrarResultados(SistemaProveedor buscador) {
        System.out.println("\n¿Cómo quieres ordenar los resultados?");
        System.out.println("1. Por precio (Más baratos primero)");
        System.out.println("2. Por stock (Mayor disponibilidad primero)");
        System.out.print("Elige una opción: ");
        int orden = leerEntero();

        List<Producto> resultados;
        if (orden == 1) {
            resultados = buscador.buscarOrdenadoPorPrecio();
        } else if (orden == 2) {
            resultados = buscador.buscarOrdenadoPorStock();
        } else {
            System.out.println("Opción no válida, volviendo al menú principal...");
            return;
        }

        System.out.println("\n=== RESULTADOS DE LA BÚSQUEDA ===");
        if (resultados.isEmpty()) {
            System.out.println("No se han encontrado muebles con esas características en ningún proveedor.");
        } else {
            for (Producto p : resultados) {
                System.out.println(p.toString());
            }
        }
    }

    // MÉTODOS DE LECTURA SEGURA (Evitan que el programa falle si se meten letras de mas)
    private static int leerEntero() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    private static double leerDouble() {
        try {
            // Reemplazamos coma por punto por si el usuario escribe "2,5" en vez de "2.5"
            String entrada = sc.nextLine().trim().replace(",", ".");
            return Double.parseDouble(entrada);
        } catch (Exception e) {
            return -1.0;
        }
    }
}
