package Practica1.Bridge;
import java.util.ArrayList;
import java.util.List;

public class EmpresaB implements IEmpresaProveedora {
    private List<Mesa> catalogoMesas;

    public EmpresaB() {
        catalogoMesas = new ArrayList<>();
        catalogoMesas.add(new Mesa("Mesa Comedor (Empresa B)", 150.0, 10, 2.0));
        catalogoMesas.add(new Mesa("Mesa Oficina (Empresa B)", 80.0, 4, 1.5));
    }

    @Override
    public List<Producto> buscarGeneral() {
        return new ArrayList<>(catalogoMesas);
    }

    @Override
    public List<Mesa> buscarMesas(double dimension) {
        List<Mesa> resultado = new ArrayList<>();
        for (Mesa m : catalogoMesas) {
            if (m.getDimension() == dimension) resultado.add(m);
        }
        return resultado;
    }

    @Override
    public List<Sofa> buscarSofas(int plazas) {
        return new ArrayList<>(); // Devuelve lista vacía porque no tiene sofás
    }
}
