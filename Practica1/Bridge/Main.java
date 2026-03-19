package Practica1.Bridge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // 1. Instanciamos las empresas proveedoras 
        IEmpresaProveedora empresaA = new EmpresaA(); 
        IEmpresaProveedora empresaB = new EmpresaB(); 
        IEmpresaProveedora empresaC = new EmpresaC(); 

        boolean salir = false;
        
        System.out.println("==================================================");
        System.out.println("  🛋️ BIENVENIDO AL COMPARADOR DE MUEBLES V1.0 🪑");
        System.out.println("==================================================");

        while (!salir) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Buscar Sofás (Une empresas B y C)");
            System.out.println("2. Buscar Mesas (Une empresas A y B)");
            System.out.println("3. Buscar TODO el catálogo (Une empresas A, B y C)");
            System.out.println("4. Salir");
            System.out.print("Elige una opción: ");
            
            int opcion = leerEntero();

            switch (opcion) {
                case 1 -> menuSofas(empresaB, empresaC); 
                case 2 -> menuMesas(empresaA, empresaB);
                case 3 -> menuGeneral(empresaA, empresaB, empresaC);
                case 4 -> {
                    salir = true;
                    System.out.println("Cerrando el comparador... ¡Mucha suerte con el 10, manito!");
                }
                default -> System.out.println("❌ Opción no válida. Por favor, elige 1, 2, 3 o 4.");
            }
        }
    }

    private static void menuSofas(IEmpresaProveedora empB, IEmpresaProveedora empC) {
        System.out.print("\n¿De cuántas plazas buscas el sofá? (ej. 2, 3, 4): ");
        int plazas = leerEntero();

        BuscadorSofas buscador = new BuscadorSofas(plazas);
        buscador.addProveedor(empB);
        buscador.addProveedor(empC);

        mostrarResultados(buscador);
    }

    private static void menuMesas(IEmpresaProveedora empA, IEmpresaProveedora empB) {
        System.out.print("\n¿Qué dimensión buscas para la mesa? (en metros, ej. 2.0 o 1.5): ");
        double dimension = leerDouble();

        BuscadorMesas buscador = new BuscadorMesas(dimension);
        buscador.addProveedor(empA);
        buscador.addProveedor(empB);

        mostrarResultados(buscador);
    }

    private static void menuGeneral(IEmpresaProveedora empA, IEmpresaProveedora empB, IEmpresaProveedora empC) {
        System.out.println("\nBuscando todos los muebles del catálogo (Mesas y Sofás)...");
        
        BuscadorGeneral buscador = new BuscadorGeneral();
        buscador.addProveedor(empA);
        buscador.addProveedor(empB);
        buscador.addProveedor(empC);

        mostrarResultados(buscador);
    }

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
            System.out.println("❌ Opción no válida, volviendo al menú principal...");
            return;
        }

        System.out.println("\n=== RESULTADOS DE LA BÚSQUEDA ===");
        if (resultados.isEmpty()) {
            System.out.println("No se han encontrado muebles con esas características en los proveedores conectados.");
        } else {
            for (Producto p : resultados) {
                System.out.println(" ✅ " + p.toString());
            }
        }
    }

    private static int leerEntero() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    private static double leerDouble() {
        try {
            String entrada = sc.nextLine().trim().replace(",", ".");
            return Double.parseDouble(entrada);
        } catch (Exception e) {
            return -1.0;
        }
    }
}

// =======================================================================
// CLASE BUSCADOR GENERAL (Oculta para no tener que crear otro archivo)
// =======================================================================
class BuscadorGeneral extends SistemaProveedor {

    public BuscadorGeneral() {}

    // Método privado adaptado para agrupar TODOS los productos mezclados
    private List<Producto> agruparStock(List<Producto> listaBruta) {
        Map<String, Producto> mapa = new HashMap<>();
        for (Producto p : listaBruta) {
            if (mapa.containsKey(p.getNombre())) {
                mapa.get(p.getNombre()).sumarStock(p.getStock());
            } else {
                // Clonamos el producto para no alterar la BD de la empresa
                if (p instanceof Mesa) {
                    Mesa m = (Mesa) p;
                    mapa.put(m.getNombre(), new Mesa(m.getNombre(), m.getPrecio(), m.getStock(), m.getDimension()));
                } else if (p instanceof Sofa) {
                    Sofa s = (Sofa) p;
                    mapa.put(s.getNombre(), new Sofa(s.getNombre(), s.getPrecio(), s.getStock(), s.getPlazas()));
                }
            }
        }
        return new ArrayList<>(mapa.values());
    }

    @Override
    public List<Producto> buscarOrdenadoPorPrecio() {
        List<Producto> agrupados = agruparStock(buscarGeneral());
        agrupados.sort(Comparator.comparingDouble(Producto::getPrecio));
        return agrupados;
    }

    @Override
    public List<Producto> buscarOrdenadoPorStock() {
        List<Producto> agrupados = agruparStock(buscarGeneral());
        agrupados.sort((p1, p2) -> Integer.compare(p2.getStock(), p1.getStock()));
        return agrupados;
    }
}