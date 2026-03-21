package Practica1.Facade;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GestorArchivos {
    public static void inicializarFicheros() {
        // Viajes desde Sevilla hacia Madrid y Córdoba
        crearFichero("Practica1/Facade/transportes.txt", "Iberia;Sevilla;Madrid;45.0\nAVE;Sevilla;Madrid;30.0\nRenfe Avant;Sevilla;Cordoba;20.0");
        crearFichero("Practica1/Facade/alojamientos.txt", "Hotel Center;Madrid;20;120.0\nHostal Sol;Madrid;5;40.0\nHotel Eurostars Palace;Cordoba;50;150.0");
        crearFichero("Practica1/Facade/actividades_madrid.txt", "Tour Museo del Prado;Cultural;2026-04-10\nMusical Rey Leon;Ocio;2026-04-10");
        crearFichero("Practica1/Facade/actividades_cordoba.txt", "Visita Mezquita-Catedral;Cultural;2026-05-01\nAlcazar de los Reyes Cristianos;Cultural;2026-05-01");
    }

    private static void crearFichero(String nombre, String contenido) {
        try {
            File file = new File(nombre);
            if (!file.exists()) {
                FileWriter writer = new FileWriter(file);
                writer.write(contenido);
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Error creando " + nombre + ": " + e.getMessage());
        }
    }
}
