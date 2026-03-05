import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando pruebas del Patrón Adapter...\n");

        try {
            // --- FASE 0: CONFIGURAR LA RUTA ---
            // Usamos File.separator para que funcione igual de bien en Windows, Mac o Linux
            String rutaBase = "Practica1" + File.separator + "Adapter" + File.separator;
            
            // Si la carpeta no existe, le decimos a Java que la cree por nosotros
            File directorio = new File("Practica1" + File.separator + "Adapter");
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            // --- FASE 1: PREPARACIÓN DEL ESCENARIO ---
            // Ahora le pegamos la rutaBase delante de cada nombre de archivo
            File f1 = new File(rutaBase + "fichero1.txt");
            File f2 = new File(rutaBase + "fichero2.txt");
            
            MaquetadorBasico basico = new MaquetadorBasico();
            if(f1.exists()) f1.delete();
            if(f2.exists()) f2.delete();
            
            basico.anadirTexto(f1, "Linea 1 del Fichero 1: Hola buenos dias");
            basico.anadirTexto(f1, "Linea 2 del Fichero 1: Estamos picando codigo");
            basico.anadirTexto(f1, "Linea 3 del Fichero 1: A tope!!!");
            
            basico.anadirTexto(f2, "Linea 1 del Fichero 2: Que pasa compañero");
            basico.anadirTexto(f2, "Linea 2 del Fichero 2: El patron adapter mola");
            basico.anadirTexto(f2, "Linea 3 del Fichero 2: Un saludo");

            // --- FASE 2: INSTANCIAR EL ADAPTADOR ---
            MaquetadorAvanzado maquetador = new AdaptadorMaquetador();

            // --- FASE 3: EJECUTAR LAS PRUEBAS ---
            
            // Prueba A: Unir Ficheros
            File fUnido = new File(rutaBase + "resultado_unido.txt");
            maquetador.unirFicheros(f1, f2, fUnido);
            System.out.println("[EXITO] Ficheros unidos en '" + fUnido.getPath() + "'");

            // Prueba B: Combinar Ficheros
            File fCombinado = new File(rutaBase + "resultado_combinado.txt");
            List<int[]> parrafosF1 = Arrays.asList(new int[]{1, 2}); 
            List<int[]> parrafosF2 = Arrays.asList(new int[]{1, 2});
            maquetador.combinarFicheros(f1, f2, parrafosF1, parrafosF2, fCombinado);
            System.out.println("[EXITO] Ficheros combinados en '" + fCombinado.getPath() + "'");

            // Prueba C: Separar Ficheros
            File fAseparar = new File(rutaBase + "resultado_unido.txt");
            List<Integer> puntosDeCorte = Arrays.asList(3); 
            List<File> partes = Arrays.asList(new File(rutaBase + "parte1.txt"), new File(rutaBase + "parte2.txt"));
            maquetador.separarFichero(fAseparar, puntosDeCorte, partes);
            
            System.out.println("[EXITO] Fichero separado en '" + partes.get(0).getPath() + "' y '" + partes.get(1).getPath() + "'");

        } catch (IOException e) {
            System.err.println("Error leyendo o escribiendo archivos");
            e.printStackTrace();
        }
    }
}