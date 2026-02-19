package Practica1.Abstract_Factory;

// Implementamos la interfaz que define las reglas de las factorías
public class FactoriaParaLlevar implements FactoriaAbstracta {

    @Override
    public Menu crearMenuTemporada(String tipoAcompanamiento) {
        // Le pasamos un 'true' al menú para avisarle de que es un pedido para llevar
        // Así, cuando el menú calcule su precio final, sabrá que tiene que sumar el 2%
        MenuTemporada menu = new MenuTemporada(true);

        // Regla del local: El menú semanal para llevar SOLO tiene entrante y principal
        menu.asignarPlato(new Plato("Sopa de fideos", 5.00, "Entrante", "Ninguno"));
        menu.asignarPlato(new Plato("Pollo asado", 10.00, "Principal", tipoAcompanamiento));

        // ¡Ojo! No le metemos postre.
        return menu;
    }

    @Override
    public Menu crearMenuSemanal(String tipoAcompanamiento) {
        // Igual que antes, le decimos que es para llevar (true)
        MenuSemanal menu = new MenuSemanal(true);

        // En el de temporada vamos a suponer que sí les damos el postre
        menu.asignarPlato(new Plato("Crema de calabaza", 6.00, "Entrante", "Ninguno"));
        menu.asignarPlato(new Plato("Costillas BBQ", 14.00, "Principal", tipoAcompanamiento));
        menu.asignarPlato(new Plato("Tarta de queso", 4.50, "Postre", "Ninguno"));

        return menu;
    }
}
