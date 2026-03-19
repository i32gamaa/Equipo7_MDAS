package Practica1.Facade;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ServicioAlojamiento {
    public List<Alojamiento> buscarAlojamientoGrupal(String ciudad, String fechaIn, String fechaOut) {
        List<Alojamiento> resultados = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("alojamientos.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                // Posiciones: datos[0]=Nombre, datos[1]=Ciudad, datos[2]=Capacidad, datos[3]=Precio
                if (datos[1].equalsIgnoreCase(ciudad)) {
                    resultados.add(new Alojamiento(datos[0], datos[1], Integer.parseInt(datos[2]), Double.parseDouble(datos[3])));
                }
            }
        } catch (Exception e) { 
            System.out.println("Error leyendo alojamientos: " + e.getMessage()); 
        }
        return resultados;
    }
}