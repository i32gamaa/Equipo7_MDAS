package Practica1.Bridge;
import java.util.List;

public interface IEmpresaProveedora {
    List<Producto> buscarGeneral();
    List<Mesa> buscarMesas(double dimension);
    List<Sofa> buscarSofas(int plazas);
}