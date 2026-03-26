import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
            
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            File f1 = new File(rutaBase + "fichero1.txt");
            File f2 = new File(rutaBase + "fichero2.txt");
            File fUnido = new File(rutaBase + "resultado_unido.txt");
            File fCombinado = new File(rutaBase + "resultado_combinado.txt");
            File fParte1 = new File(rutaBase + "parte1.txt");
            File fParte2 = new File(rutaBase + "parte2.txt");

            if (f1.exists()) f1.delete();
            if (f2.exists()) f2.delete();
            if (fUnido.exists()) fUnido.delete();
            if (fCombinado.exists()) fCombinado.delete();
            if (fParte1.exists()) fParte1.delete();
            if (fParte2.exists()) fParte2.delete();
            
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
                        try {
                            System.out.print("¿Cuántas líneas quieres que tenga cada párrafo al intercalar?: ");
                            int lineasPorParrafo = Integer.parseInt(scanner.nextLine());
                            
                            // 1. Contamos el total de líneas de cada fichero
                            long totalLineasF1 = java.nio.file.Files.readAllLines(f1.toPath()).size();
                            long totalLineasF2 = java.nio.file.Files.readAllLines(f2.toPath()).size();
                            
                            List<int[]> parrafosF1 = new ArrayList<>();
                            List<int[]> parrafosF2 = new ArrayList<>();

                            // 2. Generamos automáticamente los bloques para el Fichero 1
                            for (int inicio = 1; inicio <= totalLineasF1; inicio += lineasPorParrafo) {
                                int fin = (int) Math.min(inicio + lineasPorParrafo - 1, totalLineasF1);
                                parrafosF1.add(new int[]{inicio, fin});
                            }

                            // 3. Generamos automáticamente los bloques para el Fichero 2
                            for (int inicio = 1; inicio <= totalLineasF2; inicio += lineasPorParrafo) {
                                int fin = (int) Math.min(inicio + lineasPorParrafo - 1, totalLineasF2);
                                parrafosF2.add(new int[]{inicio, fin});
                            }
                            
                            // 4. Se los pasamos al adaptador para que haga la magia
                            maquetador.combinarFicheros(f1, f2, parrafosF1, parrafosF2, fCombinado);
                            System.out.println(">> [ÉXITO] Ficheros combinados correctamente. Archivo generado: " + fCombinado.getPath());
                            
                        } catch (NumberFormatException e) {
                            System.out.println(">> [ERROR] Entrada no válida. Debes introducir un número entero.");
                        }
                        break;
                        
                    case 3:
                        if (!fUnido.exists()) {
                            System.out.println(">> [ERROR] El fichero origen no existe. Ejecute la opción 1 primero.");
                        } else {
                            try {
                                System.out.print("Indica la línea de corte (un número) para dividir el fichero en dos: ");
                                int lineaCorte = Integer.parseInt(scanner.nextLine());
                                
                                List<Integer> puntosDeCorte = Arrays.asList(lineaCorte);
                                List<File> partes = Arrays.asList(fParte1, fParte2);
                                
                                maquetador.separarFichero(fUnido, puntosDeCorte, partes);
                                System.out.println(">> [ÉXITO] Fichero separado correctamente en: '" + fParte1.getName() + "' y '" + fParte2.getName() + "'.");
                            } catch (NumberFormatException e) {
                                System.out.println(">> [ERROR] Entrada no válida. Debes introducir un número entero.");
                            }
                        }
                        break;
                        
                    case 4:
                        System.out.println(">> Finalizando la ejecución del programa. ¡Hasta pronto!");
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