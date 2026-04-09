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
                List<Inscripcion> result = jdbcTemplate.query(query, new RowMapper<Inscripcion>() {
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
                return result;
            } else {
                return null;
            }

        } catch (DataAccessException e) {
            System.err.println("Unable to find inscripciones");
            e.printStackTrace();
            return null;
        }
    }

    public List<Inscripcion> findIndividualInscripciones() {
        try {
            String query = sqlQueries.getProperty("inscripcion.findIndividual");
            if (query != null) {
                List<Inscripcion> result = jdbcTemplate.query(query, new RowMapper<Inscripcion>() {
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
                return result;
            } else {
                return null;
            }

        } catch (DataAccessException e) {
            System.err.println("Unable to find inscripciones");
            e.printStackTrace();
            return null;
        }
    }

    public List<Inscripcion> findFamiliarInscripciones() {
        try {
            String query = sqlQueries.getProperty("inscripcion.findFamiliar");
            if (query != null) {
                List<Inscripcion> result = jdbcTemplate.query(query, new RowMapper<Inscripcion>() {
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
                return result;
            } else {
                return null;
            }

        } catch (DataAccessException e) {
            System.err.println("Unable to find inscripciones");
            e.printStackTrace();
            return null;
        }
    }

    public Inscripcion findById(int id) {
        try {
            String query = sqlQueries.getProperty("inscripcion.findById");
            Inscripcion result = jdbcTemplate.query(query, this::mapRowToInscripcion, id);
            if (result != null) {
                return result;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            System.err.println("Unable to find inscripcion by id" + id);
            e.printStackTrace();
            return null;
        }
    }

    public Inscripcion findByUserId(String userId) {
        try {
            String query = sqlQueries.getProperty("inscripcion.findByUserId");
            Inscripcion result = jdbcTemplate.query(query, this::mapRowToInscripcion, userId);
            if (result != null) {
                return result;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            System.err.println("Unable to find inscripcion by userId" + userId);
            e.printStackTrace();
            return null;
        }
    }

    private Inscripcion mapRowToInscripcion(ResultSet row) {
        try {
            if (row.first()) {
                int id = row.getInt("id");
                LocalDate date = row.getDate("date").toLocalDate();
                int totalAmount = row.getInt("totalAmount");
                String userId = row.getString("userId");
                int registeredAdults= row.getInt("registeredAdults");
                int registeredKids= row.getInt("registeredKids");

                Inscripcion inscripcion = new Inscripcion(id, date, totalAmount, userId, registeredAdults, registeredKids);
                return inscripcion;
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Unable to retrieve results from the database");
            e.printStackTrace();
            return null;
        }

    }

    public boolean addInscripcion(Inscripcion inscripcion) {
        try {
            String query = sqlQueries.getProperty("inscripcion.insert");
            if (query != null) {
                int result = jdbcTemplate.update(query,
                        inscripcion.getDate(),
                        inscripcion.getTotalAmount(),
                        inscripcion.getUserId(),
                        inscripcion.getRegisteredAdults(),
                        inscripcion.getRegisteredKids());

                if (result > 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (DataAccessException e) {
            System.err.println("[InscripcionRepository] Error inserting inscripcion:");
            e.printStackTrace();
            return false;
        }

    }


    public boolean existsByUserId(String userId) {
        try {
            String query = sqlQueries.getProperty("inscripcion.existsByUserId");
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, userId);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            System.err.println("Unable to check existence of inscripcion by userId: " + userId);
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsById(int id) {
        try {
            String query = sqlQueries.getProperty("inscripcion.existsById");
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, id);
            if (count != null && count > 0) {
                return true;
            } else {
                return false;
            }
        } catch (DataAccessException e) {
            System.err.println("Unable to check existence of inscripcions by id" + id);
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Inscripcion inscripcion) {
        try {
            String query = sqlQueries.getProperty("inscripcion.update");
            if (query != null) {
                int result = jdbcTemplate.update(query,
                        inscripcion.getTotalAmount(),
                        inscripcion.getRegisteredAdults(),
                        inscripcion.getRegisteredKids(),
                        inscripcion.getId());
                return result > 0;
            } else {
                return false;
            }
        } catch (DataAccessException e) {
            System.err.println("Unable to update inscripcion: ");
            e.printStackTrace();
            return false;
        }
    }

    
}