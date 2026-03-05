import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

// extends MaquetadorBasico: Heredamos la funcionalidad antigua (Clase Existente).
// implements MaquetadorAvanzado: Cumplimos con el nuevo contrato (Interfaz).

public class AdaptadorMaquetador extends MaquetadorBasico implements MaquetadorAvanzado {

    @Override
    public void unirFicheros(File f1, File f2, File destino) throws IOException {
        // 1. Preparamos el terreno: si el destino ya existe, lo borramos para empezar limpios
        if (destino.exists()) {
            destino.delete();
        }
        destino.createNewFile();

        // 2. Leemos todas las líneas de los dos ficheros
        List<String> lineasF1 = Files.readAllLines(f1.toPath());
        List<String> lineasF2 = Files.readAllLines(f2.toPath());

        // 3. Usamos el método anadirTexto() que HEMOS HEREDADO de MaquetadorBasico
        // Primero metemos todo el fichero 1...
        for (String linea : lineasF1) {
            this.anadirTexto(destino, linea);
        }
        // ...y luego todo el fichero 2
        for (String linea : lineasF2) {
            this.anadirTexto(destino, linea);
        }
    }

    @Override
    public void combinarFicheros(File f1, File f2, List<int[]> parrafosF1, List<int[]> parrafosF2, File destino) throws IOException {
        if (destino.exists()) {
            destino.delete();
        }
        destino.createNewFile();

        // Cuantas veces tenemos que iterar? Pues tantas como pares de párrafos tengamos en total.
        int iteraciones = Math.max(parrafosF1.size(), parrafosF2.size());
        
        for (int i = 0; i < iteraciones; i++) {
            // Extraer y añadir el trozo del Fichero 1 usando los métodos heredados
            if (i < parrafosF1.size()) {
                int[] rango = parrafosF1.get(i);
                // this.extraerParrafo viene de MaquetadorBasico
                String parrafo = this.extraerParrafo(f1, rango[0], rango[1]); 
                if (parrafo != null && !parrafo.isEmpty()) {
                    this.anadirTexto(destino, parrafo.trim());
                }
            }
            // Hacemos exactamente lo mismo para el Fichero 2
            if (i < parrafosF2.size()) {
                int[] rango = parrafosF2.get(i);
                String parrafo = this.extraerParrafo(f2, rango[0], rango[1]);
                if (parrafo != null && !parrafo.isEmpty()) {
                    this.anadirTexto(destino, parrafo.trim());
                }
            }
        }
    }

    @Override
    public void separarFichero(File archivoOriginal, List<Integer> puntosCorte, List<File> archivosDestino) throws IOException {
        File archivoActual = archivoOriginal;
        
        for (int i = 0; i < puntosCorte.size(); i++) {
            File parteExtraida = archivosDestino.get(i);
            File restoDelArchivo = new File(archivoOriginal.getParent(), "temp_resto.txt");  

            // Calculamos por qué línea cortar. 
            // Si ya hemos cortado antes, hay que restar las líneas que ya nos hemos llevado.
            int lineaCorte = puntosCorte.get(i);
            if (i > 0) {
                lineaCorte = lineaCorte - puntosCorte.get(i-1) + 1; 
            }

            // Usamos el método dividirFichero heredado de MaquetadorBasico
            this.dividirFichero(archivoActual, lineaCorte, parteExtraida, restoDelArchivo);
            
            // El resto se convierte en el archivo actual para seguir cortando en la siguiente vuelta
            archivoActual = restoDelArchivo;
        }
        
        // Guardamos el último pedazo sobrante en el último archivo de la lista
        if (archivosDestino.size() > puntosCorte.size()) {
            File ultimaParte = archivosDestino.get(archivosDestino.size() - 1);
            Files.copy(archivoActual.toPath(), ultimaParte.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Borramos el archivo temporal para no dejar basura
        if (archivoActual.getName().equals("temp_resto.txt")) {
            archivoActual.delete();
        }
    }
}