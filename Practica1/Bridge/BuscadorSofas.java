package Practica1.Bridge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuscadorSofas extends SistemaProveedor {
    
    private int plazasBuscadas;

    public BuscadorSofas(int plazasBuscadas) {
        super();
        this.plazasBuscadas = plazasBuscadas;
    }
    // Método privado para fusionar sofás iguales y sumar su stock
    private List<Producto> agruparStock(List<Sofa> listaBruta) {
        Map<String, Producto> mapa = new HashMap<>();
        for (Sofa s : listaBruta) {
            if (mapa.containsKey(s.getNombre())) {
                // Si ya existe, le sumamos el stock
                mapa.get(s.getNombre()).sumarStock(s.getStock());
            } else {
                // Creamos una copia exacta para no modificar el catálogo original de la empresa
                mapa.put(s.getNombre(), new Sofa(s.getNombre(), s.getPrecio(), s.getStock(), s.getPlazas()));
            }
        }
        return new ArrayList<>(mapa.values());
    }

    @Override
    public List<Producto> buscarOrdenadoPorPrecio() {
        List<Sofa> todosLosSofas = buscarSofas(this.plazasBuscadas);
        List<Producto> agrupados = agruparStock(todosLosSofas);
        // Ordenamos de menor a mayor precio
        agrupados.sort(Comparator.comparingDouble(Producto::getPrecio));
        return agrupados;
    }

    @Override
    public List<Producto> buscarOrdenadoPorStock() {
        List<Sofa> todosLosSofas = buscarSofas(this.plazasBuscadas);
        List<Producto> agrupados = agruparStock(todosLosSofas);
        // Ordenamos de mayor a menor stock
        agrupados.sort((p1, p2) -> Integer.compare(p2.getStock(), p1.getStock()));
        return agrupados;
    }
}