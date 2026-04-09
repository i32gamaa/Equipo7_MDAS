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
        // Cargar las queries SQL al inicializar el repositorio
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.setSQLQueriesFileName(sqlQueriesFileName);

        // Verificar que las queries se cargaron
        if (sqlQueries != null) {
            System.out.println("[ReservaRepository] Successfully loaded SQL queries. Total: " + sqlQueries.size());
            // Debug: mostrar las queries cargadas
            sqlQueries
                    .forEach((key, value) -> System.out.println("[ReservaRepository] Loaded: " + key + " = " + value));
        } else {
            System.err.println("[ReservaRepository] ERROR: SQL queries not loaded!");
        }
    }

    public List<Reserva> findAllReservas() {
        try {
            String query = sqlQueries.getProperty("reserva.findAll");
            System.out.println("[ReservaRepository] Executing findAll: " + query);

            if (query == null) {
                System.err.println("[ReservaRepository] ERROR: Query 'reserva.findAll' not found!");
                return null;
            }

            List<Reserva> result = jdbcTemplate.query(query, new RowMapper<Reserva>() {
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
            return result;

        } catch (DataAccessException e) {
            System.err.println("Unable to find reservas");
            e.printStackTrace();
            return null;
        }
    }

    public Reserva findById(int id) {
        try {
            String query = sqlQueries.getProperty("reserva.findById");
            Reserva result = jdbcTemplate.query(query, this::mapRowToReserva, id);
            if (result != null) {
                return result;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            System.err.println("Unable to find reserva by id" + id);
            e.printStackTrace();
            return null;
        }
    }

    public List<Reserva> findByDate(LocalDate date) {
        try {
            String query = sqlQueries.getProperty("reserva.findByAfterDate");
            List<Reserva> result = jdbcTemplate.query(
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
                    java.sql.Date.valueOf(date) // <- parámetro al final
            );
            return result;
        } catch (DataAccessException e) {
            System.err.println("Unable to find reserva by date " + date);
            e.printStackTrace();
            return null;
        }
    }

    public Reserva findByThisDate(LocalDate date) {
        try {
            String query = sqlQueries.getProperty("reserva.findByThisDate");
            Reserva result = jdbcTemplate.query(query, this::mapRowToReserva, date);
            if (result != null) {
                return result;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            System.err.println("Unable to find reserva by date " + date);
            e.printStackTrace();
            return null;
        }
    }

    private Reserva mapRowToReserva(ResultSet row) {
        try {
            if (row.first()) {
                int id = row.getInt("id");
                String purpose = row.getString("purpose");
                LocalDate date = row.getDate("date").toLocalDate();
                int numSeats = row.getInt("numSeats");
                String userId = row.getString("userId");
                String registrationNumber = row.getString("registrationNumber");

                Reserva Reserva = new Reserva(id, purpose, date, numSeats, userId, registrationNumber);
                return Reserva;
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Unable to retrieve results from the database");
            e.printStackTrace();
            return null;
        }

    }

    public int addReserva(Reserva reserva) {
        try {
            String query = sqlQueries.getProperty("reserva.insert");
            System.out.println("[ReservaRepository] Executing insert: " + query);

            if (query == null) {
                System.err.println("[ReservaRepository] ERROR: Query 'reserva.insert' not found!");
                return -1;
            }

            KeyHolder keyHolder = new GeneratedKeyHolder();

            int result = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, reserva.getUserId());
                ps.setString(2, reserva.getRegistrationNumber());
                ps.setObject(3, reserva.getDate()); // LocalDate
                ps.setInt(4, reserva.getNumSeats());
                ps.setString(5, reserva.getPurpose());
                ps.setDouble(6, reserva.getTotalAmount());
                return ps;
            }, keyHolder);

            if (result > 0) {
                Number generatedId = keyHolder.getKey();
                if (generatedId != null) {
                    int id = generatedId.intValue();
                    reserva.setId(id);
                    System.out.println("[ReservaRepository] Inserted reserva with ID: " + id);
                    return id;
                }
            }

            return -1;

        } catch (DataAccessException e) {
            System.err.println("Unable to save reserva: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public boolean patronAssigned(String registrationNumber) {
        try {
            String query = sqlQueries.getProperty("reserva.patronAssigned");
            String patronId = jdbcTemplate.queryForObject(query, String.class, registrationNumber);
            if (patronId != null) {
                // La embarcación tiene un patrón asignado
                return true;
            } else {
                // La embarcación no tiene un patrón asignado
                return false;
            }
        } catch (DataAccessException e) {
            System.err.println("Unable to check patron assignment for registration number: " + registrationNumber);
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasCapacity(String registrationNumber, int numSeatsRequested) {
        try {
            String query = sqlQueries.getProperty("reserva.hasCapacity");
            int capacity = jdbcTemplate.queryForObject(query, Integer.class, registrationNumber);
            if (capacity > numSeatsRequested) {
                return true;
            } else {
                return false;
            }
        } catch (DataAccessException e) {
            System.err.println("Unable to check capacity for registration number: " + registrationNumber);
            e.printStackTrace();
            return false;
        }
    }

    public boolean isAvailable(String registrationNumber, LocalDate date) {
        try {
            String query = sqlQueries.getProperty("reserva.isAvailable");
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, registrationNumber, date);
            if (count != null && count > 0) {
                return false;
            } else {
                return true;
            }
        } catch (DataAccessException e) {
            System.err.println("Unable to check availability for registration number: " + registrationNumber
                    + " on date: " + date);
            e.printStackTrace();
            return false;
        }
    }

    public boolean isAdult(String userId) {
        try {
            String query = sqlQueries.getProperty("reserva.isAdult");
            Boolean isAdult = jdbcTemplate.queryForObject(query, Boolean.class, userId);
            if (isAdult != null) {
                return isAdult;
            } else {
                return false;
            }
        } catch (DataAccessException e) {
            System.err.println("Unable to check age for user ID: " + userId);
            e.printStackTrace();
            return false;
        }
    }

    public boolean isAvailableInAlquiler(String registrationNumber, LocalDate date) {
        try {
            String query = sqlQueries.getProperty("reserva.isAvailableInAlquiler");
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, registrationNumber, date);
            if (count != null && count > 0) {
                return false;
            } else {
                return true;
            }
        } catch (DataAccessException e) {
            System.err.println("Unable to check availability in Alquiler for registration number: " + registrationNumber
                    + " on date: " + date);
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateReserva(Reserva reserva) {
        try {
            String query = sqlQueries.getProperty("reserva.update");
            if (query != null) {
                int result = jdbcTemplate.update(query, reserva.getPurpose(), reserva.getDate(),
                        reserva.getNumSeats(), reserva.getUserId(), reserva.getRegistrationNumber(),reserva.getTotalAmount(), reserva.getId());
                if (result > 0)
                    return true;
                else
                    return false;
            } else
                return false;
        } catch (DataAccessException exception) {
            System.err.println("Unable to update reserva in the database");
            exception.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteReserva(int id) {
        try {
            String query = sqlQueries.getProperty("reserva.delete");
            if (query != null) {
                int result = jdbcTemplate.update(query, id);
                if (result > 0)
                    return true;
                else
                    return false;
            } else
                return false;
        } catch (DataAccessException exception) {
            return false;
        }
    }
}
