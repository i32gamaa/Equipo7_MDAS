package es.uco.pw.demo.model.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import es.uco.pw.demo.model.domain.Patron;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PatronRepository extends AbstractRepository {

    public PatronRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.setSQLQueriesFileName(sqlQueriesFileName);
    }

    public List<Patron> findAllPatrones() {
        try {
            String query = sqlQueries.getProperty("patron.findAll");
            if (query == null) return null;

            List<Patron> fetchedPatrones = jdbcTemplate.query(query, new RowMapper<Patron>() {
                @Override
                public Patron mapRow(ResultSet rs, int rowNumber) throws SQLException {
                    return new Patron(
                            rs.getString("id"), // La bbdd lo tiene como id
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getDate("birthdate").toLocalDate(),
                            rs.getDate("titleIssueDate").toLocalDate());
                }
            });
            return fetchedPatrones;

        } catch (DataAccessException e) {
            return null;
        }
    }

    public Patron findById(String patronId) { // REFACTORIZACIÓN (Regla 1): id -> patronId
        try {
            String query = sqlQueries.getProperty("patron.findById");
            if (query == null) return null;

            List<Patron> fetchedPatrones = jdbcTemplate.query(query, new RowMapper<Patron>() {
                @Override
                public Patron mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Patron(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getDate("birthdate").toLocalDate(),
                        rs.getDate("titleIssueDate").toLocalDate()
                    );
                }
            }, patronId);
            
            return fetchedPatrones.isEmpty() ? null : fetchedPatrones.get(0);
            
        } catch (DataAccessException e) {
            return null;
        }
    }

    public boolean addPatron(Patron patron) {
        try {
            String query = sqlQueries.getProperty("patron.insert");
            if (query == null) return false;

            int rowsAffected = jdbcTemplate.update(query,
                    patron.getPatronId(), // Adaptado
                    patron.getName(),
                    patron.getSurname(),
                    patron.getBirthDate(),
                    patron.getTitleIssueDate());

            return rowsAffected > 0;
            
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean existsById(String patronId) {
        try {
            String query = sqlQueries.getProperty("patron.existsById");
            if (query == null) return false;

            Integer existenceCount = jdbcTemplate.queryForObject(query, Integer.class, patronId);
            return existenceCount != null && existenceCount > 0;
            
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean assignPatronToBoat(String patronId, String registrationNumber) {
        try {
            Patron patron = findById(patronId);
            if (patron == null) return false;
            
            String queryToVerifyBoatExists = "SELECT COUNT(*) FROM Embarcacion WHERE registrationNumber = ?"; // REFACTORIZACIÓN
            Integer boatExistenceCount = jdbcTemplate.queryForObject(queryToVerifyBoatExists, Integer.class, registrationNumber);
            if (boatExistenceCount == null || boatExistenceCount == 0) return false;
            
            String queryToVerifyPatronAvailability = "SELECT COUNT(*) FROM Embarcacion WHERE patronId = ? AND registrationNumber != ?";
            Integer assignmentCount = jdbcTemplate.queryForObject(queryToVerifyPatronAvailability, Integer.class, patronId, registrationNumber);
            if (assignmentCount != null && assignmentCount > 0) return false;
            
            String queryToUpdateBoat = "UPDATE Embarcacion SET patronId = ? WHERE registrationNumber = ?";
            int rowsAffected = jdbcTemplate.update(queryToUpdateBoat, patronId, registrationNumber);
            
            return rowsAffected > 0;
            
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean unassignPatronFromBoat(String registrationNumber) {
        try {
            String queryToVerifyBoatExists = "SELECT COUNT(*) FROM Embarcacion WHERE registrationNumber = ?";
            Integer boatExistenceCount = jdbcTemplate.queryForObject(queryToVerifyBoatExists, Integer.class, registrationNumber);
            if (boatExistenceCount == null || boatExistenceCount == 0) return false;
            
            String queryToFindCurrentPatron = "SELECT patronId FROM Embarcacion WHERE registrationNumber = ?";
            String currentPatronId = jdbcTemplate.queryForObject(queryToFindCurrentPatron, String.class, registrationNumber);
            if (currentPatronId == null || currentPatronId.equals("NULL")) return false;
            
            String queryToRemovePatron = "UPDATE Embarcacion SET patronId = NULL WHERE registrationNumber = ?";
            int rowsAffected = jdbcTemplate.update(queryToRemovePatron, registrationNumber);
            
            return rowsAffected > 0;
            
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean updatePatron(Patron patron) {
        try {
            String query = sqlQueries.getProperty("patron.update");
            int rowsAffected = jdbcTemplate.update(query,
                    patron.getName(),
                    patron.getSurname(),
                    patron.getBirthDate(),
                    patron.getTitleIssueDate(),
                    patron.getPatronId()); // Adaptado
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean deletePatron(String patronId) {
        try {
            String query = sqlQueries.getProperty("patron.delete");
            int rowsAffected = jdbcTemplate.update(query, patronId);
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean hasAssignedBoats(String patronId) {
        try {
            String query = sqlQueries.getProperty("patron.countEmbarcaciones");
            Integer assignedBoatsCount = jdbcTemplate.queryForObject(query, Integer.class, patronId); // REFACTORIZACIÓN
            return assignedBoatsCount != null && assignedBoatsCount > 0;
        } catch (DataAccessException e) {
            return false; 
        }
    }
}