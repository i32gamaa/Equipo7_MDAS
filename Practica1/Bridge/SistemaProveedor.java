package Practica1.Bridge;
import java.util.ArrayList;
import java.util.List;

public abstract class SistemaProveedor {
    
    // Aquí guardamos las empresas que conectamos al sistema
    protected List<IEmpresaProveedora> proveedores;
    // Constructor que inicializa la lista de proveedores
    public SistemaProveedor() {
        this.proveedores = new ArrayList<>();
    }
    // Método para conectar una nueva empresa proveedora al sistema
    public void addProveedor(IEmpresaProveedora p) {
        this.proveedores.add(p);
    }

    // Métodos que delegan la búsqueda a todas las empresas conectadas y unen los resultados
    public List<Producto> buscarGeneral() {
        List<Producto> todos = new ArrayList<>();
        for (IEmpresaProveedora p : proveedores) {
            todos.addAll(p.buscarGeneral());
        }
        return todos;
    }
    // Métodos específicos para mesas
    public List<Mesa> buscarMesas(double dimension) {
        List<Mesa> todas = new ArrayList<>();
        for (IEmpresaProveedora p : proveedores) {
            todas.addAll(p.buscarMesas(dimension));
        }
        return todas;
    }
    // Métodos específicos para sofás
    public List<Sofa> buscarSofas(int plazas) {
        List<Sofa> todos = new ArrayList<>();
        for (IEmpresaProveedora p : proveedores) {
            todos.addAll(p.buscarSofas(plazas));
        }
        return todos;
    }

    // Métodos abstractos que los buscadores específicos tendrán que implementar
    public abstract List<Producto> buscarOrdenadoPorPrecio();
    public abstract List<Producto> buscarOrdenadoPorStock();
}