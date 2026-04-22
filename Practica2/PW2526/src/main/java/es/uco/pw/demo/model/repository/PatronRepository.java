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

    // Regla 15: Recibe el objeto Patron entero. Regla 20: Lógica aislada.
    private void executeAddPatron(Patron patron) {
        String query = sqlQueries.getProperty("patron.insert", "INSERT INTO Patron (id, name, surname, birthdate, titleIssueDate) VALUES (?, ?, ?, ?, ?)");
        int rowsAffected = jdbcTemplate.update(query, patron.getPatronId(), patron.getName(),
                patron.getSurname(), patron.getBirthDate(), patron.getTitleIssueDate());
                
        // Regla 19: Excepción si no se inserta nada.
        if (rowsAffected == 0) throw new RuntimeException("No se pudo insertar el patrón");
    }

    // Regla 20: Extracción try-catch.
    public void updatePatron(Patron patron) {
        try { executeUpdatePatron(patron); } 
        catch (DataAccessException e) { throw new RuntimeException("Fallo al actualizar el Patrón", e); } // Regla 19
    }

    private void executeUpdatePatron(Patron patron) {
        String query = sqlQueries.getProperty("patron.update", "UPDATE Patron SET name=?, surname=?, birthdate=?, titleIssueDate=? WHERE id=?");
        int rowsAffected = jdbcTemplate.update(query, patron.getName(), patron.getSurname(),
                patron.getBirthDate(), patron.getTitleIssueDate(), patron.getPatronId());
        
        // Regla 19: Excepción
        if (rowsAffected == 0) throw new IllegalArgumentException("El patrón a actualizar no existe");
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