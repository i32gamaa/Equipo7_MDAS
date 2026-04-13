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
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.setSQLQueriesFileName(sqlQueriesFileName);
    }

    public List<Inscripcion> findAllInscripciones() {
        try {
            String query = sqlQueries.getProperty("inscripcion.findAll");
            if (query != null) {
                List<Inscripcion> fetchedInscripciones = jdbcTemplate.query(query, new RowMapper<Inscripcion>() {
                    @Override
                    public Inscripcion mapRow(ResultSet rs, int rowNumber) throws SQLException {
                        return new Inscripcion(
                                rs.getInt("id"),
                                rs.getDate("date").toLocalDate(),
                                rs.getInt("totalAmount"),
                                rs.getString("userId"),
                                rs.getInt("registeredAdults"),
                                rs.getInt("registeredKids"));
                    };
                });
                return fetchedInscripciones;
            }
            return null;
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<Inscripcion> findIndividualInscripciones() {
        try {
            String query = sqlQueries.getProperty("inscripcion.findIndividual");
            if (query != null) {
                return jdbcTemplate.query(query, new RowMapper<Inscripcion>() {
                    @Override
                    public Inscripcion mapRow(ResultSet rs, int rowNumber) throws SQLException {
                        return new Inscripcion(
                                rs.getInt("id"),
                                rs.getDate("date").toLocalDate(),
                                rs.getInt("totalAmount"),
                                rs.getString("userId"),
                                rs.getInt("registeredAdults"),
                                rs.getInt("registeredKids"));
                    }
                });
            }
            return null;
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<Inscripcion> findFamiliarInscripciones() {
        try {
            String query = sqlQueries.getProperty("inscripcion.findFamiliar");
            if (query != null) {
                return jdbcTemplate.query(query, new RowMapper<Inscripcion>() {
                    @Override
                    public Inscripcion mapRow(ResultSet rs, int rowNumber) throws SQLException {
                        return new Inscripcion(
                                rs.getInt("id"),
                                rs.getDate("date").toLocalDate(),
                                rs.getInt("totalAmount"),
                                rs.getString("userId"),
                                rs.getInt("registeredAdults"),
                                rs.getInt("registeredKids"));
                    }
                });
            }
            return null;
        } catch (DataAccessException e) {
            return null;
        }
    }

    public Inscripcion findById(int id) {
        try {
            String query = sqlQueries.getProperty("inscripcion.findById");
            return jdbcTemplate.query(query, this::extractInscripcion, id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public Inscripcion findByUserId(String userId) {
        try {
            String query = sqlQueries.getProperty("inscripcion.findByUserId");
            return jdbcTemplate.query(query, this::extractInscripcion, userId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    // REFACTORIZACIÓN: Nombre claro para evitar ambigüedades con el RowMapper
    private Inscripcion extractInscripcion(ResultSet row) throws SQLException {
        if (row.next()) { 
            int id = row.getInt("id");
            LocalDate registrationDate = row.getDate("date").toLocalDate();
            int totalAmount = row.getInt("totalAmount");
            String userId = row.getString("userId");
            int registeredAdults= row.getInt("registeredAdults");
            int registeredKids= row.getInt("registeredKids");

            return new Inscripcion(id, registrationDate, totalAmount, userId, registeredAdults, registeredKids);
        }
        return null;
    }

    public boolean addInscripcion(Inscripcion inscripcion) {
        try {
            String query = sqlQueries.getProperty("inscripcion.insert");
            if (query != null) {
                int rowsAffected = jdbcTemplate.update(query,
                        inscripcion.getRegistrationDate(), 
                        inscripcion.getTotalAmount(),
                        inscripcion.getUserId(),
                        inscripcion.getRegisteredAdults(),
                        inscripcion.getRegisteredKids());

                return rowsAffected > 0;
            }
            return false;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean existsByUserId(String userId) {
        try {
            String query = sqlQueries.getProperty("inscripcion.existsByUserId");
            Integer existenceCount = jdbcTemplate.queryForObject(query, Integer.class, userId); 
            return existenceCount != null && existenceCount > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean existsById(int id) {
        try {
            String query = sqlQueries.getProperty("inscripcion.existsById");
            Integer existenceCount = jdbcTemplate.queryForObject(query, Integer.class, id);
            return existenceCount != null && existenceCount > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean update(Inscripcion inscripcion) {
        try {
            String query = sqlQueries.getProperty("inscripcion.update");
            if (query != null) {
                int rowsAffected = jdbcTemplate.update(query,
                        inscripcion.getTotalAmount(),
                        inscripcion.getRegisteredAdults(),
                        inscripcion.getRegisteredKids(),
                        inscripcion.getId());
                return rowsAffected > 0;
            }
            return false;
        } catch (DataAccessException e) {
            return false;
        }
    }
}