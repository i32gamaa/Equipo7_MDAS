import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class MaquetadorBasico {
    
    // Añadir texto al final de un archivo
    public void anadirTexto(File archivo, String texto) throws IOException {
        try (FileWriter fw = new FileWriter(archivo, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(texto);
            bw.newLine();
        }
    }

    // Extraer un párrafo indicando donde empieza y acaba
    public String extraerParrafo(File archivo, int lineaInicio, int lineaFin) throws IOException {
        List<String> lineas = Files.readAllLines(archivo.toPath());
        StringBuilder sb = new StringBuilder();
        // Los índices empiezan en 1 para el usuario
        for (int i = lineaInicio - 1; i < lineaFin && i < lineas.size(); i++) {
            if (i >= 0) sb.append(lineas.get(i)).append("\n");
        }
        return sb.toString();
    }

    // Dividir el fichero en dos
    public void dividirFichero(File archivoOrig, int lineaCorte, File archivo1, File archivo2) throws IOException {
        List<String> lineas = Files.readAllLines(archivoOrig.toPath());
        try (BufferedWriter bw1 = new BufferedWriter(new FileWriter(archivo1));
             BufferedWriter bw2 = new BufferedWriter(new FileWriter(archivo2))) {
            for (int i = 0; i < lineas.size(); i++) {
                if (i < lineaCorte - 1) {
                    bw1.write(lineas.get(i));
                    bw1.newLine();
                } else {
                    bw2.write(lineas.get(i));
                    bw2.newLine();
                }
            }
        }
    }
}