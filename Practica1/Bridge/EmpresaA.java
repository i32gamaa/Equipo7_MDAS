package Practica1.Bridge;
import java.util.ArrayList;
import java.util.List;

public class EmpresaA implements IEmpresaProveedora {
    private List<Sofa> catalogoSofas;

    public EmpresaA() {
        catalogoSofas = new ArrayList<>();
        catalogoSofas.add(new Sofa("Sofá Chaise Longue (Empresa A)", 450.0, 5, 3));
        catalogoSofas.add(new Sofa("Sofá Cama", 300.0, 2, 2));
    }

    @Override
    public List<Producto> buscarGeneral() {
        return new ArrayList<>(catalogoSofas); 
    }

    @Override
    public List<Mesa> buscarMesas(double dimension) {
        return new ArrayList<>(); // Devuelve lista vacía porque no tiene mesas
    }

    @Override
    public List<Sofa> buscarSofas(int plazas) {
        List<Sofa> resultado = new ArrayList<>();
        for (Sofa s : catalogoSofas) {
            if (s.getPlazas() == plazas) resultado.add(s);
        }
        return resultado;
    }
}