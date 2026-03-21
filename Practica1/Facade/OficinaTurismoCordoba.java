package Practica1.Facade;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class OficinaTurismoCordoba {
    public List<Actividad> buscarPorTipo(String tipoEvento, String fecha) {
        List<Actividad> resultados = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("Practica1/Facade/actividades_cordoba.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos[1].equalsIgnoreCase(tipoEvento) && datos[2].equals(fecha)) {
                    resultados.add(new Actividad(datos[0], datos[1], datos[2]));
                }
            }
        } catch (Exception e) { System.out.println("Error leyendo turismo Córdoba."); }
        return resultados;
    }
}
