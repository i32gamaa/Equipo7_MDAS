package Practica1.Facade;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class OficinaTurismoMadrid {
    // Fíjate que solo recibe la fecha por parámetro, cumpliendo el requisito del PDF
    public List<Actividad> buscarPorFecha(String fecha) {
        List<Actividad> resultados = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("actividades_madrid.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                // Posiciones: datos[0]=Nombre, datos[1]=Tipo, datos[2]=Fecha
                if (datos[2].equals(fecha)) {
                    resultados.add(new Actividad(datos[0], datos[1], datos[2]));
                }
            }
        } catch (Exception e) { 
            System.out.println("Error leyendo turismo Madrid."); 
        }
        return resultados;
    }
}