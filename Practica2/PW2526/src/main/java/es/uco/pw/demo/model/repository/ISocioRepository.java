package es.uco.pw.demo.model.repository;

import java.util.List;

import es.uco.pw.demo.model.domain.Socio;

public interface ISocioRepository {

    // Aplico Regla 20: Método público que solo maneja el error.
    Socio findById(String socioId);

    // Regla 20: Extracción de Try/Catch
    boolean addSocio(Socio socio);

    // Regla 20
    void updateSocio(Socio socio);

    boolean addSocioAdult(Socio socio);

    boolean updateInscriptionId(Socio socio);

    List<Socio> findAllSocios();

    void updateIsBoatDriver(String socioId, boolean isBoatDriver);

}