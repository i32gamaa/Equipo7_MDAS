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

@Repository
public class EmbarcacionRepository extends AbstractRepository {

    public EmbarcacionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.setSQLQueriesFileName("./src/main/resources/db/sql.properties");
    }

    private final RowMapper<Embarcacion> embarcacionWithPatronMapper = new RowMapper<Embarcacion>() {
        @Override
        public Embarcacion mapRow(ResultSet rs, int rowNum) throws SQLException {
            Embarcacion embarcacion = new Embarcacion(
                    rs.getString("registrationNumber"),
                    es.uco.pw.demo.model.domain.EmbarcacionType.valueOf(rs.getString("type")),
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

    // Aplico la Regla 20 (Extraer bloques Try/Catch). 
    // Este método público solo gestiona la excepción para no romper los controladores de mis compañeros.
    public Embarcacion findByRegistration(String registrationNumber) {
        try {
            return executeFindByRegistration(registrationNumber);
        } catch (Exception e) {
            return null; 
        }
    }

    // Siguiendo la Regla 20, separo la lógica pura de BD en este método privado.
    private Embarcacion executeFindByRegistration(String registrationNumber) {
        String sql = sqlQueries.getProperty("embarcacion.findByRegistration", "SELECT * FROM Embarcacion WHERE registrationNumber = ?");
        List<Embarcacion> resultados = jdbcTemplate.query(sql, embarcacionWithPatronMapper, registrationNumber);
        
        // Aplico la Regla 19: Lanzo una excepción en lugar de devolver null cuando falla la lógica de negocio.
        if (resultados.isEmpty() || resultados.get(0) == null) {
            throw new IllegalArgumentException("Embarcación no encontrada"); 
        }
        return resultados.get(0);
    }

    // Regla 20: Método envoltorio para el manejo de errores.
    public boolean addEmbarcacion(Embarcacion embarcacion) {
        try {
            executeAddEmbarcacion(embarcacion);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Reglas 15 (Paso el objeto Embarcacion entero) y 20 (Lógica SQL aislada).
    private void executeAddEmbarcacion(Embarcacion embarcacion) {
        String sql = sqlQueries.getProperty("embarcacion.insert", "INSERT INTO Embarcacion (registrationNumber, type, name, numSeats, patronId, length, width, height) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        int rowsAffected = jdbcTemplate.update(sql,
                embarcacion.getRegistrationNumber(), embarcacion.getType().toString(),
                embarcacion.getName(), embarcacion.getNumberOfSeats(),
                embarcacion.getPatronId(), embarcacion.getLength(),
                embarcacion.getWidth(), embarcacion.getHeight());
                
        // Regla 19: Uso de excepciones descriptivas.
        if (rowsAffected == 0) {
            throw new RuntimeException("No se pudo insertar la embarcación"); 
        }
    }

    public List<Embarcacion> findAllEmbarcaciones() {
        try { return executeFindAllEmbarcaciones(); } 
        catch (Exception e) { return null; }
    }

    private List<Embarcacion> executeFindAllEmbarcaciones() {
        String sql = sqlQueries.getProperty("embarcacion.findAll", "SELECT * FROM Embarcacion");
        return jdbcTemplate.query(sql, embarcacionWithPatronMapper);
    }

    public List<Embarcacion> findByType(EmbarcacionType tipo) {
        try { return executeFindByType(tipo); } 
        catch (Exception e) { return null; }
    }

    private List<Embarcacion> executeFindByType(EmbarcacionType tipo) {
        String sql = sqlQueries.getProperty("embarcacion.findByType", "SELECT * FROM Embarcacion WHERE type = ?");
        return jdbcTemplate.query(sql, embarcacionWithPatronMapper, tipo.toString());
    }

    // REGLA S3 (DRY - Don't Repeat Yourself): He creado esta función privada para no estar 
        // repitiendo java.sql.Date.valueOf() en cada consulta de fechas, limpiando la lectura de SQL.
        private java.sql.Date toSqlDate(LocalDate date) {
            return java.sql.Date.valueOf(date);
        }

        public boolean isEmbarcacionAvailable(String matricula, LocalDate inicio, LocalDate fin) {
            try { return executeIsEmbarcacionAvailable(matricula, inicio, fin); } 
            catch (Exception e) { return false; }
        }

        private boolean executeIsEmbarcacionAvailable(String matricula, LocalDate inicio, LocalDate fin) {
            String sql = sqlQueries.getProperty("embarcacion.checkAvailability", "SELECT COUNT(*) FROM Alquiler WHERE registrationNumber = ? AND ((startDate <= ? AND endDate >= ?) OR (startDate <= ? AND endDate >= ?))");
            // Uso la función extractora para cumplir DRY
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, matricula, toSqlDate(fin), toSqlDate(inicio), toSqlDate(inicio), toSqlDate(fin));
            return count != null && count == 0;
        }

        public List<Embarcacion> findAvailableByDateRange(LocalDate inicio, LocalDate fin) {
            try { return executeFindAvailableByDateRange(inicio, fin); } 
            catch (Exception e) { return null; }
        }

        private List<Embarcacion> executeFindAvailableByDateRange(LocalDate inicio, LocalDate fin) {
            String sql = sqlQueries.getProperty("embarcacion.findAvailableByDates", "SELECT * FROM Embarcacion WHERE registrationNumber NOT IN (SELECT registrationNumber FROM Alquiler WHERE (startDate <= ? AND endDate >= ?) OR (startDate <= ? AND endDate >= ?))");
            // Uso la función extractora (DRY)
            return jdbcTemplate.query(sql, embarcacionWithPatronMapper, toSqlDate(fin), toSqlDate(inicio), toSqlDate(inicio), toSqlDate(fin));
        }
    }
