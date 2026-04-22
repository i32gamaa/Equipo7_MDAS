package es.uco.pw.demo.model.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import es.uco.pw.demo.model.domain.Socio;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SocioRepository extends AbstractRepository {

    public SocioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.setSQLQueriesFileName("./src/main/resources/db/sql.properties");
    }

    // Aplico Regla 20: Método público que solo maneja el error.
    public Socio findById(String socioId) {
        try { return executeFindById(socioId); } 
        catch (Exception e) { return null; }
    }

    // Regla 20: Método privado con la lógica SQL limpia.
    private Socio executeFindById(String socioId) {
        String query = sqlQueries.getProperty("socio.findById", "SELECT * FROM Socio WHERE id = ?");
        List<Socio> fetchedSocios = jdbcTemplate.query(query, socioRowMapper(), socioId);
        
        // Aplico Regla 19: Excepción descriptiva en vez de código de error.
        if (fetchedSocios.isEmpty() || fetchedSocios.get(0) == null) {
            throw new IllegalArgumentException("Socio no encontrado"); 
        }
        return fetchedSocios.get(0);
    }

    // Regla 20: Extracción de Try/Catch
    public boolean addSocio(Socio socio) {
        try { executeAddSocio(socio); return true; } 
        catch (Exception e) { return false; }
    }

    // Reglas 15 (se pasa el objeto) y 20 (lógica aislada)
    private void executeAddSocio(Socio socio) {
        String query = sqlQueries.getProperty("socio.insert2", "INSERT INTO Socio (id, name, surname, address, birthdate, inscriptionDate, isHolderInscription, isBoatDriver, isAdult, inscriptionId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        int rowsAffected = jdbcTemplate.update(query, socio.getSocioId(), socio.getName(), socio.getSurname(), 
                socio.getAddress(), socio.getBirthdate(), socio.getInscriptionDate(), socio.isHolderInscription(), 
                socio.isBoatDriver(), socio.isAdult(), socio.getInscriptionId());
                
        // Regla 19: Excepciones
        if (rowsAffected == 0) {
            throw new RuntimeException("No se insertó ninguna fila para el socio"); 
        }
    }

    // Regla 20
    public void updateSocio(Socio socio) {
        try { executeUpdateSocio(socio); } 
        catch (DataAccessException e) { throw new RuntimeException("Error al actualizar el socio", e); } // Regla 19
    }

    // Regla 20
    private void executeUpdateSocio(Socio socio) {
        String query = sqlQueries.getProperty("socio.updateSocio", "UPDATE Socio SET name=?, surname=?, address=?, birthdate=? WHERE id=?");
        int rowsAffected = jdbcTemplate.update(query, socio.getName(), socio.getSurname(), socio.getAddress(), socio.getBirthdate(), socio.getSocioId());
        
        if (rowsAffected == 0) throw new IllegalArgumentException("No se encontró el socio"); // Regla 19
    }

    private RowMapper<Socio> socioRowMapper() {
        return (rs, rowNum) -> {
            Socio socio = new Socio(
                    rs.getString("id"), rs.getString("name"), rs.getString("surname"),
                    rs.getString("address"), rs.getDate("birthdate").toLocalDate(),
                    rs.getBoolean("isBoatDriver"));
            socio.setHolderInscription(rs.getBoolean("isHolderInscription"));
            socio.setAdult(rs.getBoolean("isAdult"));
            socio.setInscriptionId(rs.getInt("inscriptionId"));
            return socio;
        };
    }
    
    public boolean addSocioAdult(Socio socio) {
        try { executeAddSocio(socio); return true; } 
        catch (Exception e) { return false; }
    }

    public boolean updateInscriptionId(Socio socio) {
        try {
            String sql = sqlQueries.getProperty("socio.updateInscriptionId", "UPDATE Socio SET inscriptionId = ? WHERE id = ?");
            return jdbcTemplate.update(sql, socio.getInscriptionId(), socio.getSocioId()) > 0;
        } catch (Exception e) { return false; }
    }

    public List<Socio> findAllSocios() {
        try {
            String sql = sqlQueries.getProperty("socio.findAll", "SELECT * FROM Socio");
            return jdbcTemplate.query(sql, socioRowMapper());
        } catch (Exception e) { return null; }
    }

    public void updateIsBoatDriver(String socioId, boolean isBoatDriver) {
        try {
            String sql = sqlQueries.getProperty("socio.updateBoatDriver", "UPDATE Socio SET isBoatDriver = ? WHERE id = ?");
            jdbcTemplate.update(sql, isBoatDriver, socioId);
        } catch (Exception e) { }
    }
}