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
        // Cargar las queries SQL al inicializar el repositorio
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.setSQLQueriesFileName(sqlQueriesFileName);
        
        // Verificar que las queries se cargaron
        if (sqlQueries != null) {
            System.out.println("[PatronRepository] Successfully loaded SQL queries. Total: " + sqlQueries.size());
            // Debug: mostrar las queries cargadas
            sqlQueries.forEach((key, value) -> 
                System.out.println("[PatronRepository] Loaded: " + key + " = " + value)
            );
        } else {
            System.err.println("[PatronRepository] ERROR: SQL queries not loaded!");
        }
    }

    public List<Patron> findAllPatrones() {
        try {
            String query = sqlQueries.getProperty("patron.findAll");
            System.out.println("[PatronRepository] Executing findAll: " + query);
            
            if (query == null) {
                System.err.println("[PatronRepository] ERROR: Query 'patron.findAll' not found!");
                return null;
            }

            List<Patron> result = jdbcTemplate.query(query, new RowMapper<Patron>() {
                @Override
                public Patron mapRow(ResultSet rs, int rowNumber) throws SQLException {
                    return new Patron(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getDate("birthdate").toLocalDate(),
                            rs.getDate("titleIssueDate").toLocalDate());
                }
            });
            return result;

        } catch (DataAccessException e) {
            System.err.println("Unable to find patrones");
            e.printStackTrace();
            return null;
        }
    }

    public Patron findById(String id) {
        try {
            String query = sqlQueries.getProperty("patron.findById");
            System.out.println("[PatronRepository] Executing findById: " + query + " with id: " + id);
            
            if (query == null) {
                System.err.println("[PatronRepository] ERROR: Query 'patron.findById' not found!");
                return null;
            }

            List<Patron> result = jdbcTemplate.query(query, new RowMapper<Patron>() {
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
            }, id);
            
            return result.isEmpty() ? null : result.get(0);
            
        } catch (DataAccessException e) {
            System.err.println("Unable to find patron by id: " + id);
            e.printStackTrace();
            return null;
        }
    }

    public boolean addPatron(Patron patron) {
        try {
            String query = sqlQueries.getProperty("patron.insert");
            System.out.println("[PatronRepository] Executing insert: " + query);
            
            if (query == null) {
                System.err.println("[PatronRepository] ERROR: Query 'patron.insert' not found!");
                return false;
            }

            int result = jdbcTemplate.update(query,
                    patron.getId(),
                    patron.getName(),
                    patron.getSurname(),
                    patron.getBirthDate(),
                    patron.getTitleIssueDate());

            return result > 0;
            
        } catch (DataAccessException e) {
            System.err.println("Unable to save patron: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsById(String id) {
        try {
            String query = sqlQueries.getProperty("patron.existsById");
            System.out.println("[PatronRepository] Executing existsById: " + query + " with id: " + id);
            
            if (query == null) {
                System.err.println("[PatronRepository] ERROR: Query 'patron.existsById' not found!");
                return false;
            }

            Integer count = jdbcTemplate.queryForObject(query, Integer.class, id);
            return count != null && count > 0;
            
        } catch (DataAccessException e) {
            System.err.println("Unable to check existence of patron by id: " + id);
            e.printStackTrace();
            return false;
        }
    }

    public boolean assignPatronToBoat(String patronId, String registrationNumber) {
    try {
        System.out.println("[PatronRepository] Asignando patrón " + patronId + " a embarcación " + registrationNumber);
        
        // 1. Verificar que el patrón existe
        Patron patron = findById(patronId);
        if (patron == null) {
            System.err.println("El patrón con ID " + patronId + " no existe");
            return false;
        }
        
        // 2. Verificar que la embarcación existe
        String checkBoatSql = "SELECT COUNT(*) FROM Embarcacion WHERE registrationNumber = ?";
        Integer boatCount = jdbcTemplate.queryForObject(checkBoatSql, Integer.class, registrationNumber);
        
        if (boatCount == null || boatCount == 0) {
            System.err.println("La embarcación con matrícula " + registrationNumber + " no existe");
            return false;
        }
        
        // 3. Verificar que el patrón no esté asignado a otra embarcación
        String checkPatronAssignmentSql = "SELECT COUNT(*) FROM Embarcacion WHERE patronId = ? AND registrationNumber != ?";
        Integer assignmentCount = jdbcTemplate.queryForObject(checkPatronAssignmentSql, Integer.class, patronId, registrationNumber);
        
        if (assignmentCount != null && assignmentCount > 0) {
            System.err.println("El patrón " + patronId + " ya está asignado a otra embarcación");
            return false;
        }
        
        // 4. Asignar el patrón a la embarcación
        String updateSql = "UPDATE Embarcacion SET patronId = ? WHERE registrationNumber = ?";
        int result = jdbcTemplate.update(updateSql, patronId, registrationNumber);
        
        System.out.println("[PatronRepository] Asignación completada. Resultado: " + (result > 0));
        return result > 0;
        
    } catch (DataAccessException e) {
        System.err.println("Error al asignar patrón a embarcación: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}


    public boolean unassignPatronFromBoat(String registrationNumber) {
        try {
            System.out.println("[PatronRepository] Desasignando patrón de la embarcación " + registrationNumber);
            
            // 1. Verificar que la embarcación existe
            String checkBoatSql = "SELECT COUNT(*) FROM Embarcacion WHERE registrationNumber = ?";
            Integer boatCount = jdbcTemplate.queryForObject(checkBoatSql, Integer.class, registrationNumber);
            
            if (boatCount == null || boatCount == 0) {
                System.err.println("La embarcación con matrícula " + registrationNumber + " no existe");
                return false;
            }
            
            // 2. Verificar que la embarcación tiene un patrón asignado
            String checkPatronSql = "SELECT patronId FROM Embarcacion WHERE registrationNumber = ?";
            String currentPatronId = jdbcTemplate.queryForObject(checkPatronSql, String.class, registrationNumber);
            
            if (currentPatronId == null || currentPatronId.equals("NULL")) {
                System.err.println("La embarcación " + registrationNumber + " no tiene ningún patrón asignado");
                return false;
            }
            
            // 3. Desasignar el patrón (establecer patronId a NULL)
            String updateSql = "UPDATE Embarcacion SET patronId = NULL WHERE registrationNumber = ?";
            int result = jdbcTemplate.update(updateSql, registrationNumber);
            
            System.out.println("[PatronRepository] Desasignación completada. Resultado: " + (result > 0));
            return result > 0;
            
        } catch (DataAccessException e) {
            System.err.println("Error al desasignar patrón de la embarcación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar datos de un patrón
    public boolean updatePatron(Patron patron) {
        try {
            String query = sqlQueries.getProperty("patron.update");
            int result = jdbcTemplate.update(query,
                    patron.getName(),
                    patron.getSurname(),
                    patron.getBirthDate(),
                    patron.getTitleIssueDate(),
                    patron.getId()); // El ID va al final en el WHERE
            return result > 0;
        } catch (DataAccessException e) {
            System.err.println("Error updating patron: " + e.getMessage());
            return false;
        }
    }

    // Borrar un patrón
    public boolean deletePatron(String id) {
        try {
            String query = sqlQueries.getProperty("patron.delete");
            int result = jdbcTemplate.update(query, id);
            return result > 0;
        } catch (DataAccessException e) {
            System.err.println("Error deleting patron: " + e.getMessage());
            return false;
        }
    }

    // Comprobar si un patrón tiene barcos asignados (Para restricción B.6)
    public boolean hasAssignedBoats(String patronId) {
        try {
            String query = sqlQueries.getProperty("patron.countEmbarcaciones");
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, patronId);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            System.err.println("Error checking assigned boats for patron: " + e.getMessage());
            return false; // Ante la duda, asumimos false, pero lo ideal es manejar el error
        }
    }
}