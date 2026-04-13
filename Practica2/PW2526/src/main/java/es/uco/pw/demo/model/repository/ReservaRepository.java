package es.uco.pw.demo.model.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.setSQLQueriesFileName(sqlQueriesFileName);
    }

    public List<Reserva> findAllReservas() {
        try {
            String query = sqlQueries.getProperty("reserva.findAll");
            if (query == null) return null;

            List<Reserva> fetchedReservas = jdbcTemplate.query(query, new RowMapper<Reserva>() {
                @Override
                public Reserva mapRow(ResultSet rs, int rowNumber) throws SQLException {
                    return new Reserva(
                            rs.getInt("id"),
                            rs.getString("purpose"),
                            rs.getDate("date").toLocalDate(),
                            rs.getInt("numSeats"),
                            rs.getString("userId"),
                            rs.getString("registrationNumber"));
                }
            });
            return fetchedReservas;

        } catch (DataAccessException e) {
            return null;
        }
    }

    public Reserva findById(int id) {
        try {
            String query = sqlQueries.getProperty("reserva.findById");
            return jdbcTemplate.query(query, this::extractReserva, id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<Reserva> findByDate(LocalDate date) {
        try {
            String query = sqlQueries.getProperty("reserva.findByAfterDate");
            return jdbcTemplate.query(
                    query,
                    new RowMapper<Reserva>() {
                        @Override
                        public Reserva mapRow(ResultSet rs, int rowNumber) throws SQLException {
                            return new Reserva(
                                    rs.getInt("id"),
                                    rs.getString("purpose"),
                                    rs.getDate("date").toLocalDate(),
                                    rs.getInt("numSeats"),
                                    rs.getString("userId"),
                                    rs.getString("registrationNumber"));
                        }
                    },
                    java.sql.Date.valueOf(date) 
            );
        } catch (DataAccessException e) {
            return null;
        }
    }

    public Reserva findByThisDate(LocalDate date) {
        try {
            String query = sqlQueries.getProperty("reserva.findByThisDate");
            return jdbcTemplate.query(query, this::extractReserva, date);
        } catch (DataAccessException e) {
            return null;
        }
    }

    // REFACTORIZACIÓN: Nombre claro para evitar ambigüedades con el RowMapper
    private Reserva extractReserva(ResultSet row) throws SQLException {
        if (row.next()) {
            int id = row.getInt("id");
            String purpose = row.getString("purpose");
            LocalDate reservationDate = row.getDate("date").toLocalDate();
            int numberOfSeats = row.getInt("numSeats");
            String userId = row.getString("userId");
            String registrationNumber = row.getString("registrationNumber");

            return new Reserva(id, purpose, reservationDate, numberOfSeats, userId, registrationNumber);
        }
        return null;
    }

    public int addReserva(Reserva reserva) {
        try {
            String query = sqlQueries.getProperty("reserva.insert");
            if (query == null) return -1;

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

            if (rowsAffected > 0) {
                Number generatedId = keyHolder.getKey();
                if (generatedId != null) {
                    int reservaId = generatedId.intValue();
                    reserva.setId(reservaId);
                    return reservaId;
                }
            }
            return -1;

        } catch (DataAccessException e) {
            return -1;
        }
    }

    public boolean patronAssigned(String registrationNumber) {
        try {
            String query = sqlQueries.getProperty("reserva.patronAssigned");
            String assignedPatronId = jdbcTemplate.queryForObject(query, String.class, registrationNumber); 
            return assignedPatronId != null;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean hasCapacity(String registrationNumber, int numSeatsRequested) {
        try {
            String query = sqlQueries.getProperty("reserva.hasCapacity");
            int availableCapacity = jdbcTemplate.queryForObject(query, Integer.class, registrationNumber); 
            return availableCapacity > numSeatsRequested;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean isAvailable(String registrationNumber, LocalDate date) {
        try {
            String query = sqlQueries.getProperty("reserva.isAvailable");
            Integer overlappingReservationsCount = jdbcTemplate.queryForObject(query, Integer.class, registrationNumber, date); 
            return overlappingReservationsCount == null || overlappingReservationsCount == 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean isAdult(String userId) {
        try {
            String query = sqlQueries.getProperty("reserva.isAdult");
            Boolean userIsAdult = jdbcTemplate.queryForObject(query, Boolean.class, userId);
            return userIsAdult != null ? userIsAdult : false;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean isAvailableInAlquiler(String registrationNumber, LocalDate date) {
        try {
            String query = sqlQueries.getProperty("reserva.isAvailableInAlquiler");
            Integer overlappingAlquileresCount = jdbcTemplate.queryForObject(query, Integer.class, registrationNumber, date); 
            return overlappingAlquileresCount == null || overlappingAlquileresCount == 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean updateReserva(Reserva reserva) {
        try {
            String query = sqlQueries.getProperty("reserva.update");
            if (query != null) {
                int rowsAffected = jdbcTemplate.update(query, reserva.getPurpose(), reserva.getReservationDate(), 
                        reserva.getNumberOfSeats(), reserva.getUserId(), reserva.getRegistrationNumber(),reserva.getTotalAmount(), reserva.getId());
                return rowsAffected > 0;
            }
            return false;
        } catch (DataAccessException exception) {
            return false;
        }
    }
    
    public boolean deleteReserva(int id) {
        try {
            String query = sqlQueries.getProperty("reserva.delete");
            if (query != null) {
                int rowsAffected = jdbcTemplate.update(query, id);
                return rowsAffected > 0;
            }
            return false;
        } catch (DataAccessException exception) {
            return false;
        }
    }
}