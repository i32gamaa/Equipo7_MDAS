package Practica1.Abstract_Factory;

public class FactoriaRestaurante implements FactoriaAbstracta {

    @Override
    public MenuSemanal crearMenuSemanal(Plato.TipoAcompanamiento acompanamiento) {
        // Creamos el menú vacío
        MenuSemanal menu = new MenuSemanal();
        
        // Añadimos al menú el plato de entrante
        menu.asignarPlato(new Plato("Salmorejo",4.50f, Plato.TipoPlato.ENTRANTE)); 
        
        // Plato principal
        Plato principal = new Plato("Entrecot a la parrilla", 15.00f, Plato.TipoPlato.PRINCIPAL);
        principal.setAcompanamiento(acompanamiento);
        menu.asignarPlato(principal);
        
        //Postre
        menu.asignarPlato(new Plato("Tarta de queso", 5.00f, Plato.TipoPlato.POSTRE)); 
        
        //Devolvemos el menú completo con todos los platos
        return menu;
    }

    @Override
    public MenuTemporada crearMenuTemporada() {
        MenuTemporada menu = new MenuTemporada();
        //Añadimos el único plato del menú de temporada
        menu.asignarPlato(new Plato("Lentejas con chorizo", 6.00f, Plato.TipoPlato.TEMPORADA));
        
        return menu;
    }
}
