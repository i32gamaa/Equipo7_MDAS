package Practica1.Abstract_Factory;

public interface FactoriaAbstracta {

    // Método para fabricar un Menú Semanal que recibe como parámetro el tipo de acompañamiento (ensalada o patatas)
    MenuSemanal crearMenuSemanal(TipoAcompañamiento acompañamiento);
    
    // Método para fabricar un Menú de Temporada
    MenuTemporada crearMenuTemporada();

}
