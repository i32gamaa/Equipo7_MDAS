package es.uco.pw.demo.model.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import es.uco.pw.demo.model.domain.Alquiler;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
public class AlquilerRepository extends AbstractRepository {

    public AlquilerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.setSQLQueriesFileName("./src/main/resources/db/sql.properties");
    }

    // Aplico Regla 15: En lugar de recibir "LocalDate startDate, LocalDate endDate, int numSeats"
    // le paso el objeto Alquiler directamente, lo cual es mucho más limpio.
    public double calculateAmount(Alquiler alquiler) {
        // Regla 19: Excepción pura en lugar de código de error si faltan datos
        if (alquiler.getStartDate() == null || alquiler.getEndDate() == null) {
            throw new IllegalArgumentException("Las fechas del alquiler no pueden ser nulas"); 
        }
        long rentedDays = ChronoUnit.DAYS.between(alquiler.getStartDate(), alquiler.getEndDate());
        if (rentedDays <= 0) rentedDays = 1; 
        
        double basePrice = rentedDays * 50.0; 
        double seatMultiplier = 1 + (alquiler.getNumberOfSeats() - 1) * 0.1; 
        return basePrice * seatMultiplier;
    }

    // Regla 20: Saco el try-catch fuera de la consulta SQL
    public void deleteById(int id) {
        try { executeDeleteById(id); } 
        catch (DataAccessException e) { throw new RuntimeException("Error eliminando", e); } // Regla 19
    }

    private void executeDeleteById(int id) {
        String query = sqlQueries.getProperty("alquiler.delete", "DELETE FROM Alquiler WHERE id = ?");
        int rowsAffected = jdbcTemplate.update(query, id);
        
        // Regla 19
        if (rowsAffected == 0) throw new IllegalArgumentException("El alquiler a eliminar no existe");
    }

    // Regla 20
    public Alquiler findById(int id) {
        try { return executeFindById(id); } 
        catch (Exception e) { return null; }
    }

    private Alquiler executeFindById(int id) {
        String query = sqlQueries.getProperty("alquiler.findById", "SELECT * FROM Alquiler WHERE id = ?");
        List<Alquiler> fetchedAlquileres = jdbcTemplate.query(query, (rs, rowNum) -> new Alquiler(
                rs.getDate("startDate").toLocalDate(),
                rs.getDate("endDate") != null ? rs.getDate("endDate").toLocalDate() : null,
                rs.getString("registrationNumber"),
                rs.getInt("numSeats"),
                rs.getDouble("totalAmount"),
                rs.getInt("id"),
                rs.getString("userid")
        ), id);
        
        // Regla 19
        if (fetchedAlquileres.isEmpty() || fetchedAlquileres.get(0) == null) {
            throw new IllegalArgumentException("Alquiler no encontrado"); 
        }
        return fetchedAlquileres.get(0);
    }

    public int addAlquiler(Alquiler alquiler) {
        try {
            String query = sqlQueries.getProperty("alquiler.insert", "INSERT INTO Alquiler (startDate, endDate, registrationNumber, numSeats, totalAmount, userid) VALUES (?, ?, ?, ?, ?, ?)");
            int rowsAffected = jdbcTemplate.update(query, alquiler.getStartDate(), alquiler.getEndDate(), 
                    alquiler.getRegistrationNumber(), alquiler.getNumberOfSeats(), alquiler.getAmount(), alquiler.getUserId());
            return rowsAffected > 0 ? 1 : -1;
        } catch (Exception e) { return -1; }
    }

    public List<Alquiler> findCurrentAndFutureAlquileres() {
        try {
            String sql = sqlQueries.getProperty("alquiler.findVigentes", "SELECT * FROM Alquiler WHERE endDate >= ? OR endDate IS NULL");
            return jdbcTemplate.query(sql, (rs, rowNum) -> new Alquiler(
                rs.getDate("startDate").toLocalDate(),
                rs.getDate("endDate") != null ? rs.getDate("endDate").toLocalDate() : null,
                rs.getString("registrationNumber"),
                rs.getInt("numSeats"),
                rs.getDouble("totalAmount"),
                rs.getInt("id"),
                rs.getString("userid")
            ), java.sql.Date.valueOf(LocalDate.now()));
        } catch (Exception e) { return null; }
    }
}