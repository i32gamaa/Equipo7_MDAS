package Practica1.Abstract_Factory;

public class MenuSemanal extends Menu {

    public MenuSemanal() {
        super(); // Llama al constructor del padre para preparar la lista vacía
    }

    public MenuSemanal(boolean paraLlevar) {
        super(paraLlevar);
    }
    @Override
    public String toString() {
        return "--- Menú Semanal ---";
    }
}
