import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Clase que implementa el sistema de maquetación digital básico.
 * Representa la 'ClaseExistente' en el patrón Adapter.
 */
public class MaquetadorBasico {

    // 1) Añadir texto, recibido como String, al final de un archivo [cite: 23]
    public void añadirTexto(String nombreFichero, String texto) throws IOException {
        try (FileWriter fw = new FileWriter(nombreFichero, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(texto);
            bw.newLine();
        }
    }

    // 2) Extraer un párrafo de un archivo de texto, indicando la línea de inicio y fin 
    public List<String> extraerParrafo(String nombreFichero, int lineaInicio, int lineaFin) throws IOException {
        List<String> lineas = Files.readAllLines(Paths.get(nombreFichero));
        List<String> parrafo = new ArrayList<>();
        
        // Ajuste de índices (las líneas suelen contarse desde 1)
        for (int i = lineaInicio - 1; i < lineaFin && i < lineas.size(); i++) {
            parrafo.add(lineas.get(i));
        }
        return parrafo;
    }

    // 3) Dividir un fichero de texto en dos, dando un número de línea para el corte 
    public void dividirFichero(String nombreFichero, int lineaCorte) throws IOException {
        List<String> lineas = Files.readAllLines(Paths.get(nombreFichero));
        
        // Fichero parte 1
        try (PrintWriter writer1 = new PrintWriter(new FileWriter("parte1_" + nombreFichero))) {
            for (int i = 0; i < lineaCorte && i < lineas.size(); i++) {
                writer1.println(lineas.get(i));
            }
        }

        // Fichero parte 2
        try (PrintWriter writer2 = new PrintWriter(new FileWriter("parte2_" + nombreFichero))) {
            for (int i = lineaCorte; i < lineas.size(); i++) {
                writer2.println(lineas.get(i));
            }
        }
    }
}