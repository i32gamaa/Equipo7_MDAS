package es.uco.pw.demo.model.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.EmbarcacionType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

@Repository
public class EmbarcacionRepository extends AbstractRepository {

    private final JdbcTemplate jdbcTemplate;
    private Properties sqlQueries;

    public EmbarcacionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        loadSQLQueries();
    }

    private void loadSQLQueries() {
        try {
            sqlQueries = new Properties();
            sqlQueries.load(getClass().getClassLoader().getResourceAsStream("db/sql.properties"));
        } catch (Exception e) {
            System.err.println("Error loading SQL queries: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private final RowMapper<Embarcacion> embarcacionMapper = new RowMapper<Embarcacion>() {
        @Override
        public Embarcacion mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Embarcacion(
                    rs.getString("registrationNumber"),
                    EmbarcacionType.valueOf(rs.getString("type")),
                    rs.getString("name"),
                    rs.getInt("numSeats"),
                    rs.getBigDecimal("length").doubleValue(),
                    rs.getBigDecimal("width").doubleValue(),
                    rs.getBigDecimal("height").doubleValue());
        }
    };

    private final RowMapper<Embarcacion> embarcacionWithPatronMapper = new RowMapper<Embarcacion>() {
        @Override
        public Embarcacion mapRow(ResultSet rs, int rowNum) throws SQLException {
            Embarcacion embarcacion = new Embarcacion(
                    rs.getString("registrationNumber"),
                    EmbarcacionType.valueOf(rs.getString("type")),
                    rs.getString("name"),
                    rs.getInt("numSeats"),
                    rs.getBigDecimal("length").doubleValue(),
                    rs.getBigDecimal("width").doubleValue(),
                    rs.getBigDecimal("height").doubleValue());

            String patronId = rs.getString("patronId");
            if (patronId != null && !patronId.equals("NULL")) {
                embarcacion.setPatronId(patronId);
            } else {
                embarcacion.setPatronId(null);
            }
            return embarcacion;
        }
    };

    public List<Embarcacion> findAllEmbarcaciones() {
        try {
            String sql = sqlQueries.getProperty("embarcacion.findAll");
            return jdbcTemplate.query(sql, embarcacionWithPatronMapper);
        } catch (Exception e) {
            System.err.println("Error finding all embarcaciones: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Embarcacion findByRegistration(String registrationNumber) {
        try {
            String sql = sqlQueries.getProperty("embarcacion.findByRegistration");
            return jdbcTemplate.queryForObject(sql, embarcacionWithPatronMapper, registrationNumber);
        } catch (Exception e) {
            System.err.println("Error finding embarcacion by registration: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean addEmbarcacion(Embarcacion embarcacion) {
        try {
            String sql = sqlQueries.getProperty("embarcacion.insert");
            int result = jdbcTemplate.update(sql,
                    embarcacion.getRegistrationNumber(),
                    embarcacion.getType().toString(),
                    embarcacion.getName(),
                    embarcacion.getNumSeats(),
                    embarcacion.getPatronId(),
                    embarcacion.getLength(),
                    embarcacion.getWidth(),
                    embarcacion.getHeight());
            return result > 0;
        } catch (Exception e) {
            System.err.println("Error saving embarcacion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public boolean update(Embarcacion embarcacion) {
        try {
            String sql = sqlQueries.getProperty("embarcacion.update");
            int result = jdbcTemplate.update(sql,
                    embarcacion.getType().toString(),
                    embarcacion.getName(),
                    embarcacion.getNumSeats(),
                    embarcacion.getPatronId(),
                    embarcacion.getLength(), 
                    embarcacion.getWidth(), 
                    embarcacion.getHeight(), 
                    embarcacion.getRegistrationNumber() 
            );
            return result > 0;
        } catch (Exception e) {
            System.err.println("Error updating embarcacion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteByRegistration(String registrationNumber) {
        try {
            String sql = sqlQueries.getProperty("embarcacion.delete");
            int result = jdbcTemplate.update(sql, registrationNumber);
            return result > 0;
        } catch (Exception e) {
            System.err.println("Error deleting embarcacion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Embarcacion> findByType(EmbarcacionType type) {
        try {
            String sql = sqlQueries.getProperty("embarcacion.findByType");
            return jdbcTemplate.query(sql, embarcacionWithPatronMapper, type.toString());

        } catch (Exception e) {
            System.err.println("Error finding embarcaciones by type: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Embarcacion> findAvailableByDate(LocalDate date) {
        try {
            String sql = sqlQueries.getProperty("embarcacion.findAvailableByDate");
            return jdbcTemplate.query(sql, embarcacionWithPatronMapper, date, date);
        } catch (Exception e) {
            System.err.println("Error finding available embarcaciones by date: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean assingPatronToBoat(String patronId, String registrationNumber) {
        try {
            // 1. Verificar que el patrón existe (consulta directa)
            String checkPatronExistsSql = "SELECT COUNT(*) FROM Patron WHERE id = ?";
            Integer patronCount = jdbcTemplate.queryForObject(checkPatronExistsSql, Integer.class, patronId);
            if (patronCount == null || patronCount == 0) {
                System.err.println("Patron with id " + patronId + " does not exist");
                return false;
            }

            // 2. Verificar que el patrón no esté asignado a otra embarcación
            String checkPatronQuery = "SELECT COUNT(*) FROM Embarcacion WHERE patronId = ? AND registrationNumber != ?";
            Integer count = jdbcTemplate.queryForObject(checkPatronQuery, Integer.class, patronId, registrationNumber);

            if (count != null && count > 0) {
                System.err.println("Patron " + patronId + " is already assigned to another boat");
                return false;
            }

            // 3. Verificar que la embarcación existe
            String checkBoatQuery = "SELECT COUNT(*) FROM Embarcacion WHERE registrationNumber = ?";
            Integer boatCount = jdbcTemplate.queryForObject(checkBoatQuery, Integer.class, registrationNumber);

            if (boatCount == null || boatCount == 0) {
                System.err.println("Embarcacion with registration " + registrationNumber + " does not exist");
                return false;
            }

            // 4. Asociar el patrón a la embarcación
            String updateQuery = "UPDATE Embarcacion SET patronId = ? WHERE registrationNumber = ?";
            int result = jdbcTemplate.update(updateQuery, patronId, registrationNumber);

            return result > 0;

        } catch (DataAccessException e) {
            System.err.println("Unable to associate patron with boat: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isEmbarcacionAvailable(String registrationNumber, LocalDate startDate, LocalDate endDate) {
        try {
            System.out.println("[EmbarcacionRepository] Verificando disponibilidad de " + registrationNumber +
                    " entre " + startDate + " y " + endDate);

            // Query para verificar si hay alquileres que se solapen con el período solciditado
           
            String query = "SELECT COUNT(*) FROM Alquiler " +
                    "WHERE registrationNumber = ? " +
                    "AND ((startDate BETWEEN ? AND ?) " +
                    "OR (endDate BETWEEN ? AND ?) " +
                    "OR (? BETWEEN startDate AND endDate) " +
                    "OR (? BETWEEN startDate AND endDate))";

            Integer count = jdbcTemplate.queryForObject(query, Integer.class,
                    registrationNumber,
                    startDate, endDate, // startDate BETWEEN ? AND ?
                    startDate, endDate, // endDate BETWEEN ? AND ?
                    startDate, startDate // ? BETWEEN startDate AND endDate (dos veces)
            );

            boolean available = (count == null || count == 0);

            System.out.println("[EmbarcacionRepository] Embarcación " + registrationNumber +
                    " disponible entre " + startDate + " y " + endDate + ": " + available +
                    " (alquileres existentes: " + count + ")");

            return available;

        } catch (Exception e) {
            System.err.println("Error checking availability for " + registrationNumber + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public List<Embarcacion> findAvailableByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            System.out.println("=== DEBUG EmbarcacionRepository.findAvailableByDateRange() ===");
            System.out.println("Buscando embarcaciones disponibles desde " + startDate + " hasta " + endDate);

            // Consulta para encontrar embarcaciones NO alquiladas en el rango de fechas
            String query = "SELECT e.registrationNumber, e.type, e.name, e.numSeats, e.patronId, e.length, e.width, e.height "
                    +
                    "FROM Embarcacion e " +
                    "WHERE e.registrationNumber NOT IN (" +
                    "    SELECT a.registrationNumber FROM Alquiler a " +
                    "    WHERE (a.startDate BETWEEN ? AND ?) OR (a.endDate BETWEEN ? AND ?) OR " +
                    "          (? BETWEEN a.startDate AND a.endDate) OR (? BETWEEN a.startDate AND a.endDate)" +
                    ")";

            System.out.println("Query: " + query);

            List<Embarcacion> result = jdbcTemplate.query(query, new RowMapper<Embarcacion>() {
                @Override
                public Embarcacion mapRow(ResultSet rs, int rowNum) throws SQLException {
                    System.out.println("Mapeando embarcación disponible - fila " + rowNum);

                    Embarcacion embarcacion = new Embarcacion();
                    embarcacion.setRegistrationNumber(rs.getString("registrationNumber"));
                    embarcacion.setName(rs.getString("name"));

                    // Convertir String a EmbarcacionType
                    String tipoString = rs.getString("type");
                    try {
                        EmbarcacionType tipo = EmbarcacionType.valueOf(tipoString);
                        embarcacion.setType(tipo);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Tipo de embarcación no válido: " + tipoString);
                        embarcacion.setType(EmbarcacionType.VELERO); // Valor por defecto
                    }

                    embarcacion.setNumSeats(rs.getInt("numSeats"));
                    embarcacion.setPatronId(rs.getString("patronId"));
                    embarcacion.setLength(rs.getDouble("length"));
                    embarcacion.setWidth(rs.getDouble("width"));
                    embarcacion.setHeight(rs.getDouble("height"));

                    return embarcacion;
                }
            },
                    java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate),
                    java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate),
                    java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));

            System.out.println("Embarcaciones disponibles encontradas: " + (result != null ? result.size() : "null"));
            System.out.println("=== FIN DEBUG EmbarcacionRepository.findAvailableByDateRange ===");

            return result;

        } catch (DataAccessException e) {
            System.err.println("Unable to find available embarcaciones: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

        public Embarcacion findByName(String name) {
        try {
            String sql = "SELECT registrationNumber, type, name, numSeats, patronId, length, width, height FROM Embarcacion WHERE name = ?";
            return jdbcTemplate.queryForObject(sql, embarcacionWithPatronMapper, name);
        } catch (Exception e) {
            System.err.println("Error finding embarcacion by name: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }   

    // Comprobar si la embarcación tiene historial de alquileres (Para restricción B.5)
    public boolean hasAlquileres(String registrationNumber) {
        try {
            String query = sqlQueries.getProperty("embarcacion.countAlquileres");
            
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, registrationNumber);
            return count != null && count > 0;
        } catch (Exception e) {
            System.err.println("Error checking alquileres for embarcacion: " + e.getMessage());
            return true; 
        }
    }


}