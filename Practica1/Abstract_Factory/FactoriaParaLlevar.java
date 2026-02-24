package Practica1.Abstract_Factory;

// Implementamos la interfaz que define las reglas de las factorías
public class FactoriaParaLlevar implements FactoriaAbstracta {

    @Override
    public MenuSemanal crearMenuSemanal(Plato.TipoAcompanamiento acompanamiento) {
        // Le decimos que es para llevar (true) para que se aplique el recargo.
        MenuSemanal menu = new MenuSemanal(true);

        // En el menú semanal para llevar, s    í incluimos postre.
        menu.agregarPlato(new Plato("Crema de calabaza", 6.00, Plato.TipoPlato.ENTRANTE));

        Plato principal = new Plato("Costillas BBQ", 14.00, Plato.TipoPlato.PRINCIPAL);
        principal.setAcompanamiento(acompanamiento);
        menu.agregarPlato(principal);

        menu.agregarPlato(new Plato("Tarta de queso", 4.50, Plato.TipoPlato.POSTRE));

        return menu;
    }

    @Override
    public MenuTemporada crearMenuTemporada() {
        // Le pasamos un 'true' al menú para avisarle de que es un pedido para llevar
        // Así, cuando el menú calcule su precio final, sabrá que tiene que sumar el 2%
        MenuTemporada menu = new MenuTemporada(true);

        // Regla del local: El menú de temporada para llevar SOLO tiene entrante y principal.
        menu.agregarPlato(new Plato("Sopa de fideos", 5.00, Plato.TipoPlato.ENTRANTE));

        Plato principal = new Plato("Pollo asado", 10.00, Plato.TipoPlato.PRINCIPAL);
        // El menú de temporada para llevar no tiene acompañamiento a elegir, se sirve con patatas.
        principal.setAcompanamiento(Plato.TipoAcompanamiento.PAPAS);
        menu.agregarPlato(principal);

        // ¡Ojo! No le metemos postre.
        return menu;
    }
}
