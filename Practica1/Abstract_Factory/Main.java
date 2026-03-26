package Practica1.Abstract_Factory;

import java.util.Scanner;
import Practica1.Abstract_Factory.Plato.TipoAcompanamiento;

public class Main {

    public static void main(String[] args) {
        // Creamos el Scanner para leer lo que escribe el usuario
        Scanner teclado = new Scanner(System.in);
        boolean seguirPidiendo = true;

        System.out.println("\n*************************************************");
        System.out.println(" BIENVENIDOS AL SISTEMA DE GESTIÓN DE RESTAURANTE");
        System.out.println("*************************************************");

        // Iniciamos el bucle para poder hacer varios pedidos
        while (seguirPidiendo) {
            
            // ---------------------------------------------------------
            // PASO 1: ¿DÓNDE VA A COMER? (Elegimos la Factoría)
            // ---------------------------------------------------------
            System.out.println("\n¿Dónde vas a disfrutar de tu comida?");
            System.out.println("1. En el restaurante");
            System.out.println("2. Para llevar");
            System.out.print("Elige una opción: ");
            int opcionLugar = teclado.nextInt();

            FactoriaAbstracta factoria; // Preparamos la variable
            
            if (opcionLugar == 2) {
                factoria = new FactoriaParaLlevar();
            } else {
                factoria = new FactoriaRestaurante();
            }

            // ---------------------------------------------------------
            // PASO 2: ¿QUÉ MENÚ QUIERE? (Elegimos el Producto)
            // ---------------------------------------------------------
            System.out.println("\n¿Qué menú te apetece hoy?");
            System.out.println("1. Menú Semanal");
            System.out.println("2. Menú de Temporada");
            System.out.print("Elige una opción: ");
            int opcionMenu = teclado.nextInt();

            Menu menuElegido = null;

            if (opcionMenu == 1) {
                // Si elige Semanal, le preguntamos la guarnición
                System.out.println("\n¿Qué guarnición quieres para tu plato principal?");
                System.out.println("1. Ensalada");
                System.out.println("2. Patatas");
                System.out.print("Elige una opción: ");
                int opcionGuarnicion = teclado.nextInt();

                // Convertimos el número al Enum
                TipoAcompanamiento acompanamiento;
                if (opcionGuarnicion == 1) {
                    acompanamiento = TipoAcompanamiento.ENSALADA;
                } else {
                    acompanamiento = TipoAcompanamiento.PATATAS;
                }
                
                // Fabricamos el menú semanal con la guarnición elegida
                menuElegido = factoria.crearMenuSemanal(acompanamiento);
                
            } else {
                // Si elige el de temporada, se crea directo (no lleva acompañamiento)
                menuElegido = factoria.crearMenuTemporada();
            }

            // ---------------------------------------------------------
            // PASO 3: IMPRIMIMOS EL TICKET
            // ---------------------------------------------------------
            imprimirTicket(menuElegido);

            // ---------------------------------------------------------
            // PASO 4: ¿OTRO PEDIDO?
            // ---------------------------------------------------------
            System.out.print("\n¿Deseas realizar otro pedido?");
            System.out.println("\n1. Sí");
            System.out.println("2. No");
            int opcionSalir = teclado.nextInt();
            if (opcionSalir == 2) {
                seguirPidiendo = false; // Rompemos el bucle
            }
        }
        
        System.out.println("\n¡Gracias por usar nuestro sistema! ¡Hasta pronto!\n");
        teclado.close();
    }

    // ---------------------------------------------------------
    // MÉTODO AUXILIAR PARA IMPRIMIR LOS TICKETS
    // ---------------------------------------------------------
    public static void imprimirTicket(Menu menu) {
        System.out.println("\n ----------- TICKET DEL PEDIDO -----------");
        System.out.println(" " + menu.toString()); 
        
        for (Plato p : menu.obtener_platos()) {
            System.out.println("  - " + p.toString());
        }
        
        System.out.println(" -----------------------------------------");
        System.out.printf(" PRECIO TOTAL: %.2f EUR\n", menu.calcular_precio());
        
        if (menu.paraLlevar) {
            System.out.println(" *(Incluye 2% de recargo)*");
        }
        
        System.out.println(" =========================================");
    }
}