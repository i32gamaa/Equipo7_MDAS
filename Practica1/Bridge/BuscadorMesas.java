package Practica1.Bridge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuscadorMesas extends SistemaProveedor {
    
    private double dimensionBuscada;

    public BuscadorMesas(double dimensionBuscada) {
        super();
        this.dimensionBuscada = dimensionBuscada;
    }

    // Método privado para fusionar mesas iguales y sumar su stock
    private List<Producto> agruparStock(List<Mesa> listaBruta) {
        Map<String, Producto> mapa = new HashMap<>();
        for (Mesa m : listaBruta) {
            if (mapa.containsKey(m.getNombre())) {
                // Si ya existe, le sumamos el stock
                mapa.get(m.getNombre()).sumarStock(m.getStock());
            } else {
                // Creamos una copia exacta para no modificar el catálogo original de la empresa
                mapa.put(m.getNombre(), new Mesa(m.getNombre(), m.getPrecio(), m.getStock(), m.getDimension()));
            }
        }
        return new ArrayList<>(mapa.values());
    }

    @Override
    public List<Producto> buscarOrdenadoPorPrecio() {
        List<Mesa> todasLasMesas = buscarMesas(this.dimensionBuscada);
        List<Producto> agrupadas = agruparStock(todasLasMesas);
        
        // Ordenamos de menor a mayor precio
        agrupadas.sort(Comparator.comparingDouble(Producto::getPrecio));
        return agrupadas;
    }

    @Override
    public List<Producto> buscarOrdenadoPorStock() {
        List<Mesa> todasLasMesas = buscarMesas(this.dimensionBuscada);
        List<Producto> agrupadas = agruparStock(todasLasMesas);
        
        // Ordenamos de mayor a menor stock
        agrupadas.sort((p1, p2) -> Integer.compare(p2.getStock(), p1.getStock()));
        return agrupadas;
    }
}
