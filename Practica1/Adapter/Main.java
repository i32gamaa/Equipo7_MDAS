import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("==========================================");
        System.out.println("   SISTEMA DE MAQUETACIÓN - PATRÓN ADAPTER  ");
        System.out.println("==========================================\n");

        try {
            // --- FASE 0: CONFIGURACIÓN DE RUTAS Y LIMPIEZA INICIAL ---
            String rutaBase = "Practica1" + File.separator + "Adapter" + File.separator;
            File directorio = new File("Practica1" + File.separator + "Adapter");
            
            // Creación del directorio si no existe
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            // Definición de todos los archivos implicados
            File f1 = new File(rutaBase + "fichero1.txt");
            File f2 = new File(rutaBase + "fichero2.txt");
            File fUnido = new File(rutaBase + "resultado_unido.txt");
            File fCombinado = new File(rutaBase + "resultado_combinado.txt");
            File fParte1 = new File(rutaBase + "parte1.txt");
            File fParte2 = new File(rutaBase + "parte2.txt");

            // Limpieza de archivos de ejecuciones anteriores
            if (f1.exists()) f1.delete();
            if (f2.exists()) f2.delete();
            if (fUnido.exists()) fUnido.delete();
            if (fCombinado.exists()) fCombinado.delete();
            if (fParte1.exists()) fParte1.delete();
            if (fParte2.exists()) fParte2.delete();
            
            // Generación de los ficheros base con texto formal
            MaquetadorBasico basico = new MaquetadorBasico();
            basico.anadirTexto(f1, "Línea 1 del Fichero 1: Introducción al patrón Adapter.");
            basico.anadirTexto(f1, "Línea 2 del Fichero 1: Este patrón permite la colaboración de clases.");
            basico.anadirTexto(f1, "Línea 3 del Fichero 1: Fin de la primera sección.");
            
            basico.anadirTexto(f2, "Línea 1 del Fichero 2: Detalles de implementación.");
            basico.anadirTexto(f2, "Línea 2 del Fichero 2: Se utiliza composición o herencia.");
            basico.anadirTexto(f2, "Línea 3 del Fichero 2: Conclusión del documento.");

            // --- FASE 1: INSTANCIACIÓN DEL ADAPTADOR ---
            MaquetadorAvanzado maquetador = new AdaptadorMaquetador();
            boolean salir = false;

            // --- FASE 2: MENÚ DE USUARIO ---
            while (!salir) {
                System.out.println("\nSeleccione la operación a realizar:");
                System.out.println("  1. Unir Fichero 1 y Fichero 2");
                System.out.println("  2. Combinar e intercalar Fichero 1 y Fichero 2");
                System.out.println("  3. Separar un fichero (requiere haber ejecutado la opción 1)");
                System.out.println("  4. Salir del programa");
                System.out.print("Introduzca una opción (1-4): ");
                
                String input = scanner.nextLine();
                int opcion = 0;
                
                try {
                    opcion = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println(">> [ADVERTENCIA] Por favor, introduzca un valor numérico válido.");
                    continue;
                }

                switch (opcion) {
                    case 1:
                        maquetador.unirFicheros(f1, f2, fUnido);
                        System.out.println(">> [ÉXITO] Ficheros unidos correctamente. Archivo generado: " + fUnido.getPath());
                        break;
                        
                    case 2:
                        // Rango de extracción: líneas 1 a 2 de cada fichero
                        List<int[]> parrafosF1 = Arrays.asList(new int[]{1, 2}); 
                        List<int[]> parrafosF2 = Arrays.asList(new int[]{1, 2});
                        maquetador.combinarFicheros(f1, f2, parrafosF1, parrafosF2, fCombinado);
                        System.out.println(">> [ÉXITO] Ficheros combinados correctamente. Archivo generado: " + fCombinado.getPath());
                        break;
                        
                    case 3:
                        if (!fUnido.exists()) {
                            System.out.println(">> [ERROR] El fichero origen no existe. Ejecute la opción 1 primero.");
                        } else {
                            List<Integer> puntosDeCorte = Arrays.asList(3); // El corte se realiza en la línea 3
                            List<File> partes = Arrays.asList(fParte1, fParte2);
                            maquetador.separarFichero(fUnido, puntosDeCorte, partes);
                            System.out.println(">> [ÉXITO] Fichero separado correctamente en: '" + fParte1.getName() + "' y '" + fParte2.getName() + "'.");
                        }
                        break;
                        
                    case 4:
                        System.out.println(">> Finalizando la ejecución del programa. Hasta pronto.");
                        salir = true;
                        break;
                        
                    default:
                        System.out.println(">> [ADVERTENCIA] Opción fuera de rango. Seleccione un número entre 1 y 4.");
                }
            }

        } catch (IOException e) {
            System.err.println(">> [ERROR CRÍTICO] Excepción de entrada/salida detectada durante la manipulación de archivos.");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}