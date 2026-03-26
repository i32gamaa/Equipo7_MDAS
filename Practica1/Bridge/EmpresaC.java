package Practica1.Bridge;
import java.util.ArrayList;
import java.util.List;

public class EmpresaC implements IEmpresaProveedora {
    private List<Producto> catalogo;

    public EmpresaC() {
        catalogo = new ArrayList<>();
        catalogo.add(new Sofa("Sofá Piel (Empresa C)", 600.0, 3, 4));
        catalogo.add(new Mesa("Mesa Cristal (Empresa C)", 200.0, 5, 1.8));
        catalogo.add(new Sofa("Sofá Cama", 300.0, 4, 2)); 
    }

    @Override
    public List<Producto> buscarGeneral() {
        return new ArrayList<>(catalogo);
    }

    @Override
    public List<Mesa> buscarMesas(double dimension) {
        List<Mesa> resultado = new ArrayList<>();
        for (Producto p : catalogo) {
            if (p instanceof Mesa) {
                Mesa m = (Mesa) p;
                if (m.getDimension() == dimension) resultado.add(m);
            }
        }
        return resultado;
    }

    @Override
    public List<Sofa> buscarSofas(int plazas) {
        List<Sofa> resultado = new ArrayList<>();
        for (Producto p : catalogo) {
            if (p instanceof Sofa) {
                Sofa s = (Sofa) p;
                if (s.getPlazas() == plazas) resultado.add(s);
            }
        }
        return resultado;
    }
}
