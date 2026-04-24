package es.uco.pw.demo.model.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import es.uco.pw.demo.model.domain.Inscripcion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;

@Repository
public class InscripcionRepository extends AbstractRepository {

    public InscripcionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.setSQLQueriesFileName("./src/main/resources/db/sql.properties");
    }

    // Regla 20: Método envoltorio para las excepciones
    public Inscripcion findById(int id) {
        try { return executeFindById(id); } 
        catch (Exception e) { return null; }
    }

    // Regla 20: Método de lógica pura
    private Inscripcion executeFindById(int id) {
        String query = sqlQueries.getProperty("inscripcion.findById", "SELECT * FROM Inscripcion WHERE id = ?");
        Inscripcion inscripcion = jdbcTemplate.query(query, this::extractInscripcion, id);
        
        // Aplico Regla 19: Excepción si falla la búsqueda real.
        if (inscripcion == null) {
            throw new IllegalArgumentException("Inscripción no encontrada"); 
        }
        return inscripcion;
    }

    public boolean addInscripcion(Inscripcion inscripcion) {
        try {
            String query = sqlQueries.getProperty("inscripcion.insert", "INSERT INTO Inscripcion (date, totalAmount, userId, registeredAdults, registeredKids) VALUES (?, ?, ?, ?, ?)");
            
            // REGLA S3: Separar la extracción de datos de la ejecución SQL
            Object[] parametros = extraerParametrosInscripcion(inscripcion);
            int rowsAffected = jdbcTemplate.update(query, parametros);
            
            return rowsAffected > 0;
        } catch (Exception e) { return false; }
    }

    // REGLA S3: Hacer una sola cosa
    private Object[] extraerParametrosInscripcion(Inscripcion inscripcion) {
        return new Object[]{
            inscripcion.getRegistrationDate(), 
            inscripcion.getTotalAmount(),
            inscripcion.getUserId(), 
            inscripcion.getRegisteredAdults(),
            inscripcion.getRegisteredKids()
        };
    }
    
    private Inscripcion extractInscripcion(java.sql.ResultSet row) throws java.sql.SQLException {
        if (row.next()) { 
            int id = row.getInt("id");
            java.time.LocalDate registrationDate = row.getDate("date").toLocalDate();
            int totalAmount = row.getInt("totalAmount");
            String userId = row.getString("userId");
            int registeredAdults = row.getInt("registeredAdults");
            int registeredKids = row.getInt("registeredKids");
            return new Inscripcion(id, registrationDate, totalAmount, userId, registeredAdults, registeredKids);
        }
        return null;
    }

    private final RowMapper<Inscripcion> inscripcionRowMapper = (rs, rowNum) -> {
        return new Inscripcion(
            rs.getInt("id"), rs.getDate("date").toLocalDate(), rs.getInt("totalAmount"),
            rs.getString("userId"), rs.getInt("registeredAdults"), rs.getInt("registeredKids")
        );
    };

    public Inscripcion findByUserId(String userId) {
        try {
            String query = sqlQueries.getProperty("inscripcion.findByUserId", "SELECT * FROM Inscripcion WHERE userId = ?");
            List<Inscripcion> lista = jdbcTemplate.query(query, inscripcionRowMapper, userId);
            return lista.isEmpty() ? null : lista.get(0);
        } catch (Exception e) { return null; }
    }

    public List<Inscripcion> findAllInscripciones() {
        try {
            String query = sqlQueries.getProperty("inscripcion.findAll", "SELECT * FROM Inscripcion");
            return jdbcTemplate.query(query, inscripcionRowMapper); 
        } catch (Exception e) { return null; }
    }

    public void update(Inscripcion inscripcion) {
        try {
            String query = sqlQueries.getProperty("inscripcion.update", "UPDATE Inscripcion SET totalAmount=?, registeredAdults=?, registeredKids=? WHERE id=?");
            jdbcTemplate.update(query, inscripcion.getTotalAmount(), inscripcion.getRegisteredAdults(), inscripcion.getRegisteredKids(), inscripcion.getId());
        } catch (Exception e) { }
    }
}