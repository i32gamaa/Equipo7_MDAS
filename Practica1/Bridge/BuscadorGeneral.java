package Practica1.Bridge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuscadorGeneral extends SistemaProveedor {

    public BuscadorGeneral() {
        super();
    }

    // Método privado adaptado para agrupar TODOS los productos mezclados
    private List<Producto> agruparStock(List<Producto> listaBruta) {
        Map<String, Producto> mapa = new HashMap<>();
        for (Producto p : listaBruta) {
            if (mapa.containsKey(p.getNombre())) {
                mapa.get(p.getNombre()).sumarStock(p.getStock());
            } else {
                // Clonamos el producto para no alterar la BD de la empresa
                if (p instanceof Mesa) {
                    Mesa m = (Mesa) p;
                    mapa.put(m.getNombre(), new Mesa(m.getNombre(), m.getPrecio(), m.getStock(), m.getDimension()));
                } else if (p instanceof Sofa) {
                    Sofa s = (Sofa) p;
                    mapa.put(s.getNombre(), new Sofa(s.getNombre(), s.getPrecio(), s.getStock(), s.getPlazas()));
                }
            }
        }
        return new ArrayList<>(mapa.values());
    }

    @Override
    public List<Producto> buscarOrdenadoPorPrecio() {
        List<Producto> agrupados = agruparStock(buscarGeneral());
        agrupados.sort(Comparator.comparingDouble(Producto::getPrecio));
        return agrupados;
    }

    @Override
    public List<Producto> buscarOrdenadoPorStock() {
        List<Producto> agrupados = agruparStock(buscarGeneral());
        agrupados.sort((p1, p2) -> Integer.compare(p2.getStock(), p1.getStock()));
        return agrupados;
    }
}