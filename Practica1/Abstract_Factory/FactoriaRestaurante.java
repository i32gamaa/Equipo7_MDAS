package Practica1.Abstract_Factory;

import Practica1.Abstract_Factory.Plato.TipoAcompañamiento;

public class FactoriaRestaurante implements FactoriaAbstracta {

    @Override
    public MenuSemanal crearMenuSemanal(TipoAcompañamiento acompañamiento) {
        // Creamos el menú vacío
        MenuSemanal menu = new MenuSemanal();
        
        // Añadimos al menú el plato de entrante
        menu.agregarPlato(new Plato("Salmorejo",4.50, Plato.TipoPlato.ENTRANTE)); 
        
        // Plato principal
        menu.agregarPlato(new Plato("Entrecot a la parrilla", 15.00, Plato.TipoPlato.PRINCIPAL)); 
        
        //Postre
        menu.agregarPlato(new Plato("Tarta de queso", 5.00, Plato.TipoPlato.POSTRE)); 
        
        //Devolvemos el menú completo con todos los platos
        return menu;
    }

    @Override
    public MenuTemporada crearMenuTemporada() {
        MenuTemporada menu = new MenuTemporada();
        //Añadimos el único plato del menú de temporada
        menu.agregarPlato(new Plato("Lentejas con chorizo", 6.00, Plato.TipoPlato.TEMPORADA));
        
        return menu;
    }
}
