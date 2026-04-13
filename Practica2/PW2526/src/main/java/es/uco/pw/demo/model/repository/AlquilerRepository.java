package es.uco.pw.demo.model.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import es.uco.pw.demo.model.domain.Alquiler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
public class AlquilerRepository extends AbstractRepository {

    public AlquilerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.setSQLQueriesFileName(sqlQueriesFileName);
    }

    public List<Alquiler> findAllAlquileres() {
        try {
            if (sqlQueries == null) return null;
            
            String query = sqlQueries.getProperty("alquiler.findAll");
            if (query == null) return null;

            List<Alquiler> fetchedAlquileres = jdbcTemplate.query(query, new RowMapper<Alquiler>() { // REFACTORIZACIÓN: result -> fetchedAlquileres
                @Override
                public Alquiler mapRow(ResultSet rs, int rowNumber) throws SQLException {
                    // ADAPTACIÓN AL MODELO: Se usa setNumberOfSeats
                    return new Alquiler(
                            rs.getDate("startDate").toLocalDate(),
                            rs.getDate("endDate") != null ? rs.getDate("endDate").toLocalDate() : null,
                            rs.getString("registrationNumber"),
                            rs.getInt("numSeats"),
                            rs.getDouble("totalAmount"),
                            rs.getInt("id"), 
                            rs.getString("userid"));
                }
            });
            return fetchedAlquileres;

        } catch (DataAccessException e) {
            System.err.println("ERROR en findAllAlquileres: " + e.getMessage());
            return null;
        }
    }

    public Alquiler findById(int id) {
        try {
            String query = sqlQueries.getProperty("alquiler.findById");
            if (query == null) return null;

            List<Alquiler> fetchedAlquileres = jdbcTemplate.query(query, new RowMapper<Alquiler>() {
                @Override
                public Alquiler mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Alquiler(
                        rs.getDate("startDate").toLocalDate(),
                        rs.getDate("endDate") != null ? rs.getDate("endDate").toLocalDate() : null,
                        rs.getString("registrationNumber"),
                        rs.getInt("numSeats"),
                        rs.getDouble("totalAmount"),
                        rs.getInt("id"),
                        rs.getString("userid")
                    );
                }
            }, id);
            
            return (fetchedAlquileres != null && !fetchedAlquileres.isEmpty()) ? fetchedAlquileres.get(0) : null;
            
        } catch (DataAccessException e) {
            System.err.println("Unable to find alquiler by id: " + e.getMessage());
            return null;
        }
    }

    public int addAlquiler(Alquiler alquiler) {
        try {
            String query = sqlQueries.getProperty("alquiler.insert");
            if (query == null) return -1;

            KeyHolder keyHolder = new GeneratedKeyHolder();

            // REFACTORIZACIÓN (Regla 1): result por rowsAffected
            int rowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, alquiler.getUserId());
                ps.setDate(2, java.sql.Date.valueOf(alquiler.getStartDate()));
                ps.setDate(3, alquiler.getEndDate() != null ? java.sql.Date.valueOf(alquiler.getEndDate()) : null);
                ps.setString(4, alquiler.getRegistrationNumber());
                ps.setInt(5, alquiler.getNumberOfSeats()); // Adaptado al domain
                ps.setDouble(6, alquiler.getAmount());
                return ps;
            }, keyHolder);

            if (rowsAffected > 0) {
                Number generatedId = keyHolder.getKey();
                if (generatedId != null) {
                    int rentalId = generatedId.intValue();
                    alquiler.setRentalId(rentalId);
                    return rentalId;
                }
            }
            return -1;
            
        } catch (DataAccessException e) {
            return -1;
        }
    }

    public boolean existsById(int id) {
        try {
            String query = sqlQueries.getProperty("alquiler.existsById");
            if (query == null) return false;

            // REFACTORIZACIÓN (Regla 1): count por recordCount
            Integer recordCount = jdbcTemplate.queryForObject(query, Integer.class, id);
            return recordCount != null && recordCount > 0;
            
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean update(Alquiler alquiler) {
        try {
            String query = sqlQueries.getProperty("alquiler.update");
            if (query == null) return false;

            int rowsAffected = jdbcTemplate.update(query,
                    alquiler.getUserId(),
                    java.sql.Date.valueOf(alquiler.getStartDate()),
                    alquiler.getEndDate() != null ? java.sql.Date.valueOf(alquiler.getEndDate()) : null,
                    alquiler.getRegistrationNumber(),
                    alquiler.getNumberOfSeats(), // Adaptado
                    alquiler.getAmount(),
                    alquiler.getRentalId());

            return rowsAffected > 0;
            
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean deleteById(int id) {
        try {
            String query = sqlQueries.getProperty("alquiler.delete");
            if (query == null) return false;

            int rowsAffected = jdbcTemplate.update(query, id);
            return rowsAffected > 0;
            
        } catch (DataAccessException e) {
            return false;
        }
    }

    public double calculateAmount(LocalDate startDate, LocalDate endDate, int numSeats) {
        try {
            long rentedDays = ChronoUnit.DAYS.between(startDate, endDate); // REFACTORIZACIÓN: days -> rentedDays
            if (rentedDays <= 0) rentedDays = 1; 
            
            double basePrice = rentedDays * 50.0; 
            double seatMultiplier = 1 + (numSeats - 1) * 0.1; 
            return basePrice * seatMultiplier;
        } catch (Exception e) {
            return 100.0; 
        }
    }

    public List<Alquiler> findCurrentAndFutureAlquileres() {
        try {
            String query = "SELECT id, userId, startDate, endDate, registrationNumber, numSeats, totalAmount " +
                          "FROM Alquiler WHERE endDate >= CURDATE() ORDER BY startDate ASC";
            
            List<Alquiler> fetchedAlquileres = jdbcTemplate.query(query, new RowMapper<Alquiler>() {
                @Override
                public Alquiler mapRow(ResultSet rs, int rowNumber) throws SQLException {
                    return new Alquiler(
                            rs.getDate("startDate").toLocalDate(),
                            rs.getDate("endDate") != null ? rs.getDate("endDate").toLocalDate() : null,
                            rs.getString("registrationNumber"),
                            rs.getInt("numSeats"),
                            rs.getDouble("totalAmount"),
                            rs.getInt("id"),
                            rs.getString("userid"));
                }
            });
            return fetchedAlquileres;
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<Alquiler> findByDate(LocalDate fecha) {
        try {
            String query = "SELECT id, userId, startDate, endDate, registrationNumber, numSeats, totalAmount " +
                          "FROM Alquiler WHERE ? BETWEEN startDate AND endDate ORDER BY startDate ASC";

            List<Alquiler> fetchedAlquileres = jdbcTemplate.query(query, new RowMapper<Alquiler>() {
                @Override
                public Alquiler mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Alquiler(
                        rs.getDate("startDate").toLocalDate(),
                        rs.getDate("endDate") != null ? rs.getDate("endDate").toLocalDate() : null,
                        rs.getString("registrationNumber"),
                        rs.getInt("numSeats"),
                        rs.getDouble("totalAmount"),
                        rs.getInt("id"),
                        rs.getString("userid")
                    );
                }
            }, java.sql.Date.valueOf(fecha));
            
            return fetchedAlquileres;
            
        } catch (DataAccessException e) {
            return null;
        }
    }
}