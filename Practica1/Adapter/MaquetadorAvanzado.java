import java.io.File;
import java.io.IOException;
import java.util.List;

public interface MaquetadorAvanzado {
    // 1) Unir dos ficheros de texto
    void unirFicheros(File f1, File f2, File destino) throws IOException;

    // 2) Combinar contenido intercalando párrafos
    void combinarFicheros(File f1, File f2, List<int[]> parrafosF1, List<int[]> parrafosF2, File destino) throws IOException;

    // 3) Separar fichero en varios indicando puntos de corte
    void separarFichero(File archivoOriginal, List<Integer> puntosCorte, List<File> archivosDestino) throws IOException;
}
