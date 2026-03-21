package Practica1.Facade;
import java.util.List;

public class AgenciaViajesFacade {
    private ServicioTransporte transporteApi = new ServicioTransporte();
    private ServicioAlojamiento alojamientoApi = new ServicioAlojamiento();
    private OficinaTurismoMadrid turismoMadrid = new OficinaTurismoMadrid();
    private OficinaTurismoCordoba turismoCordoba = new OficinaTurismoCordoba();

    public void buscarViajeCompleto(String origen, String destino, String fechaInicio, String fechaFin) {
        System.out.println("\n --- BUSCANDO PAQUETE DE VIAJE GRUPAL --- ");
        System.out.println("Ruta: " + origen + " -> " + destino + " | Fechas: " + fechaInicio + " a " + fechaFin);
        
        System.out.println("\n[1/3] Conectando con aerolíneas y trenes...");
        List<Transporte> transportes = transporteApi.buscarTransporte(origen, destino, fechaInicio);
        if(transportes.isEmpty()) System.out.println("   No hay transportes.");
        for (Transporte t : transportes) System.out.println("  " + t);

        System.out.println("\n[2/3] Buscando alojamientos con capacidad grupal...");
        List<Alojamiento> alojamientos = alojamientoApi.buscarAlojamientoGrupal(destino, fechaInicio, fechaFin);
        if(alojamientos.isEmpty()) System.out.println("   No hay alojamientos.");
        for (Alojamiento a : alojamientos) System.out.println("  " + a);

        System.out.println("\n[3/3] Contactando con la Oficina de Turismo de " + destino + "...");
        if (destino.equalsIgnoreCase("Madrid")) {
            List<Actividad> acts = turismoMadrid.buscarPorFecha(fechaInicio); 
            for (Actividad act : acts) System.out.println("  " + act);
        } else if (destino.equalsIgnoreCase("Cordoba") || destino.equalsIgnoreCase("Córdoba")) {
            List<Actividad> acts = turismoCordoba.buscarPorTipo("Cultural", fechaInicio); // Córdoba pide el tipo
            for (Actividad act : acts) System.out.println("  " + act);
        } else {
            System.out.println(" [!]  No tenemos acuerdos con la oficina de turismo de esta ciudad.");
        }
        System.out.println("----------------------------------------------------\n");
    }
}
