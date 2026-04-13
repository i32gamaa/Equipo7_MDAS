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
            // Adaptación al modelo: numberOfSeats
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
            return null;
        }
    }

    public Embarcacion findByRegistration(String registrationNumber) {
        try {
            String sql = sqlQueries.getProperty("embarcacion.findByRegistration");
            return jdbcTemplate.queryForObject(sql, embarcacionWithPatronMapper, registrationNumber);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean addEmbarcacion(Embarcacion embarcacion) {
        try {
            String sql = sqlQueries.getProperty("embarcacion.insert");
            int rowsAffected = jdbcTemplate.update(sql, // REFACTORIZACIÓN
                    embarcacion.getRegistrationNumber(),
                    embarcacion.getType().toString(),
                    embarcacion.getName(),
                    embarcacion.getNumberOfSeats(),
                    embarcacion.getPatronId(),
                    embarcacion.getLength(),
                    embarcacion.getWidth(),
                    embarcacion.getHeight());
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean update(Embarcacion embarcacion) {
        try {
            String sql = sqlQueries.getProperty("embarcacion.update");
            int rowsAffected = jdbcTemplate.update(sql,
                    embarcacion.getType().toString(),
                    embarcacion.getName(),
                    embarcacion.getNumberOfSeats(),
                    embarcacion.getPatronId(),
                    embarcacion.getLength(), 
                    embarcacion.getWidth(), 
                    embarcacion.getHeight(), 
                    embarcacion.getRegistrationNumber() 
            );
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteByRegistration(String registrationNumber) {
        try {
            String sql = sqlQueries.getProperty("embarcacion.delete");
            int rowsAffected = jdbcTemplate.update(sql, registrationNumber);
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Embarcacion> findByType(EmbarcacionType type) {
        try {
            String sql = sqlQueries.getProperty("embarcacion.findByType");
            return jdbcTemplate.query(sql, embarcacionWithPatronMapper, type.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public List<Embarcacion> findAvailableByDate(LocalDate date) {
        try {
            String sql = sqlQueries.getProperty("embarcacion.findAvailableByDate");
            return jdbcTemplate.query(sql, embarcacionWithPatronMapper, date, date);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean assingPatronToBoat(String patronId, String registrationNumber) {
        try {
            // REFACTORIZACIÓN (Regla 1 y 10): Nombres de variables descriptivos
            String queryToVerifyPatronExists = "SELECT COUNT(*) FROM Patron WHERE id = ?";
            Integer patronExistenceCount = jdbcTemplate.queryForObject(queryToVerifyPatronExists, Integer.class, patronId);
            if (patronExistenceCount == null || patronExistenceCount == 0) return false;

            String queryToVerifyPatronAvailability = "SELECT COUNT(*) FROM Embarcacion WHERE patronId = ? AND registrationNumber != ?";
            Integer patronAssignedBoatsCount = jdbcTemplate.queryForObject(queryToVerifyPatronAvailability, Integer.class, patronId, registrationNumber);

            if (patronAssignedBoatsCount != null && patronAssignedBoatsCount > 0) return false;

            String queryToVerifyBoatExists = "SELECT COUNT(*) FROM Embarcacion WHERE registrationNumber = ?";
            Integer boatExistenceCount = jdbcTemplate.queryForObject(queryToVerifyBoatExists, Integer.class, registrationNumber);

            if (boatExistenceCount == null || boatExistenceCount == 0) return false;

            String queryToAssignPatron = "UPDATE Embarcacion SET patronId = ? WHERE registrationNumber = ?";
            int rowsAffected = jdbcTemplate.update(queryToAssignPatron, patronId, registrationNumber);

            return rowsAffected > 0;

        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean isEmbarcacionAvailable(String registrationNumber, LocalDate startDate, LocalDate endDate) {
        try {
            String query = "SELECT COUNT(*) FROM Alquiler " +
                    "WHERE registrationNumber = ? " +
                    "AND ((startDate BETWEEN ? AND ?) " +
                    "OR (endDate BETWEEN ? AND ?) " +
                    "OR (? BETWEEN startDate AND endDate) " +
                    "OR (? BETWEEN startDate AND endDate))";

            Integer overlappingRentalsCount = jdbcTemplate.queryForObject(query, Integer.class, // REFACTORIZACIÓN: count -> overlappingRentalsCount
                    registrationNumber,
                    startDate, endDate, 
                    startDate, endDate, 
                    startDate, startDate 
            );

            return (overlappingRentalsCount == null || overlappingRentalsCount == 0);

        } catch (Exception e) {
            return false;
        }
    }

    public List<Embarcacion> findAvailableByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            String query = "SELECT e.registrationNumber, e.type, e.name, e.numSeats, e.patronId, e.length, e.width, e.height "
                    +
                    "FROM Embarcacion e " +
                    "WHERE e.registrationNumber NOT IN (" +
                    "    SELECT a.registrationNumber FROM Alquiler a " +
                    "    WHERE (a.startDate BETWEEN ? AND ?) OR (a.endDate BETWEEN ? AND ?) OR " +
                    "          (? BETWEEN a.startDate AND a.endDate) OR (? BETWEEN a.startDate AND a.endDate)" +
                    ")";

            List<Embarcacion> availableBoats = jdbcTemplate.query(query, new RowMapper<Embarcacion>() {
                @Override
                public Embarcacion mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Embarcacion embarcacion = new Embarcacion();
                    embarcacion.setRegistrationNumber(rs.getString("registrationNumber"));
                    embarcacion.setName(rs.getString("name"));

                    String tipoString = rs.getString("type");
                    try {
                        EmbarcacionType tipo = EmbarcacionType.valueOf(tipoString);
                        embarcacion.setType(tipo);
                    } catch (IllegalArgumentException e) {
                        embarcacion.setType(EmbarcacionType.VELERO); 
                    }

                    embarcacion.setNumberOfSeats(rs.getInt("numSeats"));
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

            return availableBoats;

        } catch (DataAccessException e) {
            return null;
        }
    }

    public Embarcacion findByName(String name) {
        try {
            String sql = "SELECT registrationNumber, type, name, numSeats, patronId, length, width, height FROM Embarcacion WHERE name = ?";
            return jdbcTemplate.queryForObject(sql, embarcacionWithPatronMapper, name);
        } catch (Exception e) {
            return null;
        }
    }   

    public boolean hasAlquileres(String registrationNumber) {
        try {
            String query = sqlQueries.getProperty("embarcacion.countAlquileres");
            
            Integer rentalCount = jdbcTemplate.queryForObject(query, Integer.class, registrationNumber); // REFACTORIZACIÓN
            return rentalCount != null && rentalCount > 0;
        } catch (Exception e) {
            return true; 
        }
    }
}