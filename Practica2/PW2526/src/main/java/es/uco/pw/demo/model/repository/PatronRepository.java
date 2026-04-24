package es.uco.pw.demo.model.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import es.uco.pw.demo.model.domain.Patron;
import java.util.List;

@Repository
public class PatronRepository extends AbstractRepository {

    public PatronRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.setSQLQueriesFileName("./src/main/resources/db/sql.properties");
    }

    // Regla 20: Saco el try-catch de la lógica principal. 
    public Patron findById(String patronId) {
        try { return executeFindById(patronId); } 
        catch (Exception e) { return null; } // Lo capturo aquí para compatibilidad con Controladores
    }

    // Regla 20: Función que hace una sola cosa (consulta SQL).
    private Patron executeFindById(String patronId) {
        String query = sqlQueries.getProperty("patron.findById", "SELECT * FROM Patron WHERE id = ?");
        List<Patron> fetchedPatrones = jdbcTemplate.query(query, patronRowMapper(), patronId);
        
        // Regla 19: Uso excepciones en vez de null para controlar el flujo de error real.
        if (fetchedPatrones.isEmpty() || fetchedPatrones.get(0) == null) {
            throw new IllegalArgumentException("Patrón no encontrado");
        }
        return fetchedPatrones.get(0);
    }

    // Regla 20: Aislar bloque try-catch.
    public boolean addPatron(Patron patron) {
        try { executeAddPatron(patron); return true; } 
        catch (Exception e) { return false; }
    }

    // REGLA S3: Extraer mapeo de parámetros (Nivel de abstracción)
    private void executeAddPatron(Patron patron) {
        String query = sqlQueries.getProperty("patron.insert");
        Object[] parametros = extraerParametrosPatron(patron); // Llamada limpia
        
        int rowsAffected = jdbcTemplate.update(query, parametros);

        if (rowsAffected == 0) throw new RuntimeException("No se pudo insertar el patrón");
    }

    // REGLA 20: Extraer Try/Catch
    public void updatePatron(Patron patron) {
        try {
            executeUpdatePatron(patron);
        } catch (org.springframework.dao.DataAccessException e) {
            throw new RuntimeException("Fallo al actualizar el Patrón", e); // REGLA 19
        }
    }

    // REGLA S3: Separar la ejecución SQL del mapeo de datos
    private void executeUpdatePatron(Patron patron) {
        String query = sqlQueries.getProperty("patron.update");
        // Reutilizamos la función porque los parámetros (menos el ID) son parecidos, 
        // o creamos los parámetros específicos para el UPDATE:
        Object[] parametrosUpdate = new Object[]{
            patron.getName(), patron.getSurname(), patron.getBirthDate(), 
            patron.getTitleIssueDate(), patron.getPatronId()
        };
        
        int rowsAffected = jdbcTemplate.update(query, parametrosUpdate);
                
        if (rowsAffected == 0) throw new IllegalArgumentException("El patrón a actualizar no existe");
    }

    // REGLA S3: Hacer una sola cosa (Mapear el objeto a un array de base de datos)
    private Object[] extraerParametrosPatron(Patron patron) {
        return new Object[]{
            patron.getPatronId(), patron.getName(), patron.getSurname(), 
            patron.getBirthDate(), patron.getTitleIssueDate()
        };
    }

    private RowMapper<Patron> patronRowMapper() {
        return (rs, rowNum) -> new Patron(
                rs.getString("id"), rs.getString("name"), rs.getString("surname"),
                rs.getDate("birthdate").toLocalDate(), rs.getDate("titleIssueDate").toLocalDate()
        );
    }

    public List<Patron> findAllPatrones() {
        try {
            String query = sqlQueries.getProperty("patron.findAll", "SELECT * FROM Patron");
            return jdbcTemplate.query(query, patronRowMapper());
        } catch (Exception e) { return null; }
    }

    public boolean assignPatronToBoat(String patronId, String registrationNumber) {
        try {
            String sql = sqlQueries.getProperty("patron.assignBoat", "UPDATE Embarcacion SET patronId = ? WHERE registrationNumber = ?");
            return jdbcTemplate.update(sql, patronId, registrationNumber) > 0;
        } catch (Exception e) { return false; }
    }

    public boolean unassignPatronFromBoat(String registrationNumber) {
        try {
            String sql = sqlQueries.getProperty("patron.unassignBoat", "UPDATE Embarcacion SET patronId = NULL WHERE registrationNumber = ?");
            return jdbcTemplate.update(sql, registrationNumber) > 0;
        } catch (Exception e) { return false; }
    }
}