package Practica1.Facade;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ServicioTransporte {
    public List<Transporte> buscarTransporte(String origen, String destino, String fecha) {
        List<Transporte> resultados = new ArrayList<>();
        // Abrimos el archivo que habrá generado el GestorArchivos
        try (BufferedReader br = new BufferedReader(new FileReader("Practica1/Facade/transportes.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                // Posiciones: datos[0]=Aerolinea, datos[1]=Origen, datos[2]=Destino, datos[3]=Precio
                if (datos[1].equalsIgnoreCase(origen) && datos[2].equalsIgnoreCase(destino)) {
                    resultados.add(new Transporte(datos[0], datos[1], datos[2], Double.parseDouble(datos[3])));
                }
            }
        } catch (Exception e) { 
            System.out.println("Error leyendo transportes: " + e.getMessage()); 
        }
        return resultados;
    }
}