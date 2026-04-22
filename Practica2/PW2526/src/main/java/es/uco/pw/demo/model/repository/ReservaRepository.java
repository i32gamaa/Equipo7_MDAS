package es.uco.pw.demo.model.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import es.uco.pw.demo.model.domain.Reserva;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;

@Repository
public class ReservaRepository extends AbstractRepository {

    public ReservaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.setSQLQueriesFileName("./src/main/resources/db/sql.properties");
    }

    // Regla 20: Método público que solo maneja el Try/Catch.
    public Reserva findById(int id) {
        try { return executeFindById(id); } 
        catch (Exception e) { return null; }
    }

    // Regla 20: Lógica aislada en el método privado.
    private Reserva executeFindById(int id) {
        String query = sqlQueries.getProperty("reserva.findById", "SELECT * FROM Reserva WHERE id = ?");
        Reserva reserva = jdbcTemplate.query(query, this::extractReserva, id);
        
        // Regla 19: Excepción en lugar de null.
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva not found"); 
        }
        return reserva;
    }

    // Regla 20
    public int addReserva(Reserva reserva) {
        try { return executeAddReserva(reserva); } 
        catch (Exception e) { return -1; }
    }

    // Reglas 15 y 20
    private int executeAddReserva(Reserva reserva) {
        String query = sqlQueries.getProperty("reserva.insert", "INSERT INTO Reserva (userId, registrationNumber, date, numSeats, purpose, totalAmount) VALUES (?, ?, ?, ?, ?, ?)");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, reserva.getUserId());
            ps.setString(2, reserva.getRegistrationNumber());
            ps.setObject(3, reserva.getReservationDate());
            ps.setInt(4, reserva.getNumberOfSeats());
            ps.setString(5, reserva.getPurpose());
            ps.setDouble(6, reserva.getTotalAmount());
            return ps;
        }, keyHolder);

        // Regla 19
        if (rowsAffected == 0 || keyHolder.getKey() == null) {
            throw new RuntimeException("Insertion failed");
        }
        return keyHolder.getKey().intValue();
    }

    public boolean isAvailable(String registrationNumber, LocalDate date) {
        try {
            String query = sqlQueries.getProperty("reserva.isAvailable", "SELECT COUNT(*) FROM Reserva WHERE registrationNumber = ? AND date = ?");
            Integer overlappingReservationsCount = jdbcTemplate.queryForObject(query, Integer.class, registrationNumber, date); 
            return overlappingReservationsCount == null || overlappingReservationsCount == 0;
        } catch (Exception e) { return false; }
    }

    public void updateReserva(Reserva reserva) {
        try {
            String query = sqlQueries.getProperty("reserva.update", "UPDATE Reserva SET purpose=?, date=?, numSeats=?, userId=?, registrationNumber=?, totalAmount=? WHERE id=?");
            int rowsAffected = jdbcTemplate.update(query, reserva.getPurpose(), reserva.getReservationDate(), 
                    reserva.getNumberOfSeats(), reserva.getUserId(), reserva.getRegistrationNumber(),reserva.getTotalAmount(), reserva.getId());
            
            // Regla 19
            if (rowsAffected == 0) throw new IllegalArgumentException("Reserva not found for update");
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error updating Reserva", exception); // Regla 19
        }
    }

    private Reserva extractReserva(ResultSet row) throws SQLException {
        if (row.next()) {
            return new Reserva(
                row.getInt("id"), row.getString("purpose"), row.getDate("date").toLocalDate(),
                row.getInt("numSeats"), row.getString("userId"), row.getString("registrationNumber")
            );
        }
        return null;
    }
    
    public boolean patronAssigned(String registrationNumber) {
        try {
            String sql = sqlQueries.getProperty("reserva.patronAssigned", "SELECT COUNT(*) FROM Embarcacion WHERE registrationNumber = ? AND patronId IS NOT NULL");
            return jdbcTemplate.queryForObject(sql, Integer.class, registrationNumber) > 0;
        } catch (Exception e) { return false; }
    }

    public boolean hasCapacity(String registrationNumber, int plazas) {
        try {
            String sql = sqlQueries.getProperty("reserva.hasCapacity", "SELECT numSeats FROM Embarcacion WHERE registrationNumber = ?");
            Integer capacidad = jdbcTemplate.queryForObject(sql, Integer.class, registrationNumber);
            return capacidad != null && capacidad >= plazas;
        } catch (Exception e) { return false; }
    }

    public boolean isAvailableInAlquiler(String registrationNumber, LocalDate date) {
        try {
            String sql = sqlQueries.getProperty("reserva.isAvailableInAlquiler", "SELECT COUNT(*) FROM Alquiler WHERE registrationNumber = ? AND startDate <= ? AND (endDate IS NULL OR endDate >= ?)");
            return jdbcTemplate.queryForObject(sql, Integer.class, registrationNumber, date, date) == 0;
        } catch (Exception e) { return false; }
    }

    public boolean isAdult(String userId) {
        try {
            String sql = sqlQueries.getProperty("reserva.isAdult", "SELECT isAdult FROM Socio WHERE id = ?");
            Boolean adult = jdbcTemplate.queryForObject(sql, Boolean.class, userId);
            return adult != null && adult;
        } catch (Exception e) { return false; }
    }

    public List<Reserva> findAllReservas() {
        try {
            String sql = sqlQueries.getProperty("reserva.findAll", "SELECT * FROM Reserva");
            return jdbcTemplate.query(sql, (rs, rowNum) -> new Reserva(
                rs.getInt("id"), rs.getString("purpose"), rs.getDate("date").toLocalDate(),
                rs.getInt("numSeats"), rs.getString("userId"), rs.getString("registrationNumber")
            ));
        } catch (Exception e) { return null; }
    }
}