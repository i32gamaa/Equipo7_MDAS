package Practica1.Abstract_Factory;

// Implementamos la interfaz que define las reglas de las factorías
public class FactoriaParaLlevar implements FactoriaAbstracta {

    @Override
    public MenuSemanal crearMenuSemanal(Plato.TipoAcompanamiento acompanamiento) {
        MenuSemanal menu = new MenuSemanal();
        menu.setParaLlevar(true);

        menu.asignarPlato(new Plato("Crema de calabaza", 6.00f, Plato.TipoPlato.ENTRANTE));

        Plato principal = new Plato("Costillas BBQ", 14.00f, Plato.TipoPlato.PRINCIPAL);
        principal.setAcompanamiento(acompanamiento);
        menu.asignarPlato(principal);

        return menu;
    }

    @Override
    public MenuTemporada crearMenuTemporada() {
        MenuTemporada menu = new MenuTemporada();
        menu.setParaLlevar(true);

        menu.asignarPlato(new Plato("Pollo asado", 10.00f, Plato.TipoPlato.TEMPORADA));

        return menu;
    }
}
