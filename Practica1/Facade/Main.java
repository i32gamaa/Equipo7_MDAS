package Practica1.Facade;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GestorArchivos.inicializarFicheros();
        
        Scanner sc = new Scanner(System.in);
        AgenciaViajesFacade agencia = new AgenciaViajesFacade();
        boolean salir = false;

        System.out.println("=========================================");
        System.out.println("           AGENCIA DE VIAJES ");
        System.out.println("=========================================");

        while (!salir) {
            System.out.println("\n--- PLANIFICADOR DE VIAJES GRUPALES ---");
            System.out.println("1.  Buscar viaje a Madrid (Desde Sevilla)");
            System.out.println("2.  Buscar viaje a Córdoba (Desde Sevilla)");
            System.out.println("3.  Salir");
            System.out.print("Elige tu destino: ");
            
            String opcion = sc.nextLine();

            switch (opcion) {
                case "1":
                    agencia.buscarViajeCompleto("Sevilla", "Madrid", "2026-04-10", "2026-04-15");
                    break;
                case "2":
                    agencia.buscarViajeCompleto("Sevilla", "Cordoba", "2026-05-01", "2026-05-05");
                    break;
                case "3":
                    System.out.println("¡Gracias por confiar en nosotros! ¡Buen viaje!");
                    salir = true;
                    break;
                default:
                    System.out.println(" Opción incorrecta. Por favor, selecciona 1, 2 o 3.");
            }
        }
        sc.close();
    }
}