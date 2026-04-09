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
        // Cargar las queries SQL al inicializar el repositorio
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.setSQLQueriesFileName(sqlQueriesFileName);
    }

    public List<Alquiler> findAllAlquileres() {
        try {
            System.out.println("=== DEBUG AlquilerRepository.findAllAlquileres() ===");
            
            // 1. Verificar que sqlQueries se cargó
            if (sqlQueries == null) {
                System.err.println("ERROR: sqlQueries es NULL");
                return null;
            }
            System.out.println("sqlQueries cargado: " + sqlQueries.size() + " propiedades");
            
            // 2. Obtener la query
            String query = sqlQueries.getProperty("alquiler.findAll");
            System.out.println("Query obtenida: " + query);
            
            if (query == null) {
                System.err.println("ERROR: Query 'alquiler.findAll' no encontrada");
                System.out.println("Queries disponibles:");
                sqlQueries.forEach((key, value) -> {
                    if (key.toString().contains("alquiler")) {
                        System.out.println("  " + key + " = " + value);
                    }
                });
                return null;
            }

            // 3. Probar consulta COUNT primero
            try {
                String countQuery = "SELECT COUNT(*) FROM Alquiler";
                Integer count = jdbcTemplate.queryForObject(countQuery, Integer.class);
                System.out.println("COUNT de Alquiler: " + count + " registros");
            } catch (Exception e) {
                System.err.println("ERROR en COUNT: " + e.getMessage());
                return null;
            }

            // 4. Ejecutar la consulta principal
            System.out.println("Ejecutando consulta principal...");
            List<Alquiler> result = jdbcTemplate.query(query, new RowMapper<Alquiler>() {
                @Override
                public Alquiler mapRow(ResultSet rs, int rowNumber) throws SQLException {
                    System.out.println("Mapeando fila " + rowNumber);
                    
                    // Debug: mostrar valores de cada columna
                    System.out.println("  id: " + rs.getInt("id"));
                    System.out.println("  userid: " + rs.getString("userid"));
                    System.out.println("  registrationNumber: " + rs.getString("registrationNumber"));
                    System.out.println("  startDate: " + rs.getDate("startDate"));
                    System.out.println("  endDate: " + rs.getDate("endDate"));
                    System.out.println("  numSeats: " + rs.getInt("numSeats"));
                    System.out.println("  totalAmount: " + rs.getDouble("totalAmount"));
                    
                    // Ahora usamos la columna 'id'
                    Alquiler alquiler = new Alquiler(
                            rs.getDate("startDate").toLocalDate(),
                            rs.getDate("endDate") != null ? rs.getDate("endDate").toLocalDate() : null,
                            rs.getString("registrationNumber"),
                            rs.getInt("numSeats"),
                            rs.getDouble("totalAmount"),
                            rs.getInt("id"), 
                            rs.getString("userid"));
                    
                    System.out.println("Alquiler creado: " + alquiler);
                    return alquiler;
                }
            });
            
            System.out.println("Consulta completada. Resultados: " + (result != null ? result.size() : "null"));
            System.out.println("=== FIN DEBUG AlquilerRepository ===");
            return result;

        } catch (DataAccessException e) {
            System.err.println("ERROR en findAllAlquileres: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Alquiler findById(int id) {
        try {
            System.out.println("=== DEBUG AlquilerRepository.findById(" + id + ") ===");
            
            String query = sqlQueries.getProperty("alquiler.findById");
            System.out.println("Query obtenida: " + query);
            
            if (query == null) {
                System.err.println("ERROR: Query 'alquiler.findById' no encontrada");
                return null;
            }

            List<Alquiler> result = jdbcTemplate.query(query, new RowMapper<Alquiler>() {
                @Override
                public Alquiler mapRow(ResultSet rs, int rowNum) throws SQLException {
                    System.out.println("Mapeando alquiler por ID:");
                    System.out.println("  id: " + rs.getInt("id"));
                    
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
            
            Alquiler alquiler = (result != null && !result.isEmpty()) ? result.get(0) : null;
            System.out.println("Alquiler encontrado: " + alquiler);
            System.out.println("=== FIN DEBUG AlquilerRepository.findById ===");
            
            return alquiler;
            
        } catch (DataAccessException e) {
            System.err.println("Unable to find alquiler by id: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public int addAlquiler(Alquiler alquiler) {
        try {
            System.out.println("=== DEBUG AlquilerRepository.addAlquiler() ===");
            System.out.println("Alquiler a insertar: " + alquiler);
            
            String query = sqlQueries.getProperty("alquiler.insert");
            System.out.println("Query obtenida: " + query);
            
            if (query == null) {
                System.err.println("ERROR: Query 'alquiler.insert' no encontrada");
                return -1;
            }

            KeyHolder keyHolder = new GeneratedKeyHolder();

            int result = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, alquiler.getUserId());
                ps.setDate(2, java.sql.Date.valueOf(alquiler.getStartDate()));
                ps.setDate(3, alquiler.getEndDate() != null ? java.sql.Date.valueOf(alquiler.getEndDate()) : null);
                ps.setString(4, alquiler.getRegistrationNumber());
                ps.setInt(5, alquiler.getNumSeats());
                ps.setDouble(6, alquiler.getAmount());
                return ps;
            }, keyHolder);

            if (result > 0) {
                Number generatedId = keyHolder.getKey();
                if (generatedId != null) {
                    int id = generatedId.intValue();
                    alquiler.setRentalId(id); // Asignar el ID generado al objeto
                    System.out.println("[AlquilerRepository] Inserted alquiler with ID: " + id);
                    return id;
                }
            }

            return -1;
            
        } catch (DataAccessException e) {
            System.err.println("Unable to save alquiler: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public boolean existsById(int id) {
        try {
            System.out.println("=== DEBUG AlquilerRepository.existsById(" + id + ") ===");
            
            String query = sqlQueries.getProperty("alquiler.existsById");
            System.out.println("Query obtenida: " + query);
            
            if (query == null) {
                System.err.println("ERROR: Query 'alquiler.existsById' no encontrada");
                return false;
            }

            Integer count = jdbcTemplate.queryForObject(query, Integer.class, id);
            boolean exists = count != null && count > 0;
            
            System.out.println("Count: " + count + ", Exists: " + exists);
            System.out.println("=== FIN DEBUG AlquilerRepository.existsById ===");
            
            return exists;
            
        } catch (DataAccessException e) {
            System.err.println("Unable to check existence of alquiler by id: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Alquiler alquiler) {
        try {
            System.out.println("=== DEBUG AlquilerRepository.update() ===");
            System.out.println("Alquiler a actualizar: " + alquiler);
            
            String query = sqlQueries.getProperty("alquiler.update");
            System.out.println("Query obtenida: " + query);
            
            if (query == null) {
                System.err.println("ERROR: Query 'alquiler.update' no encontrada");
                return false;
            }

            int result = jdbcTemplate.update(query,
                    alquiler.getUserId(),
                    java.sql.Date.valueOf(alquiler.getStartDate()),
                    alquiler.getEndDate() != null ? java.sql.Date.valueOf(alquiler.getEndDate()) : null,
                    alquiler.getRegistrationNumber(),
                    alquiler.getNumSeats(),
                    alquiler.getAmount(),
                    alquiler.getRentalId());

            System.out.println("Filas afectadas: " + result);
            System.out.println("=== FIN DEBUG AlquilerRepository.update ===");

            return result > 0;
            
        } catch (DataAccessException e) {
            System.err.println("Unable to update alquiler: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteById(int id) {
        try {
            System.out.println("=== DEBUG AlquilerRepository.deleteById(" + id + ") ===");
            
            String query = sqlQueries.getProperty("alquiler.delete");
            System.out.println("Query obtenida: " + query);
            
            if (query == null) {
                System.err.println("ERROR: Query 'alquiler.delete' no encontrada");
                return false;
            }

            int result = jdbcTemplate.update(query, id);
            
            System.out.println("Filas afectadas: " + result);
            System.out.println("=== FIN DEBUG AlquilerRepository.deleteById ===");

            return result > 0;
            
        } catch (DataAccessException e) {
            System.err.println("Unable to delete alquiler: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método para calcular el importe automáticamente
    public double calculateAmount(LocalDate startDate, LocalDate endDate, int numSeats) {
        try {
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            if (days <= 0) days = 1; // Mínimo 1 día
            
            double basePrice = days * 50.0; // 50€ por día base
            double seatMultiplier = 1 + (numSeats - 1) * 0.1; // 10% extra por plaza adicional
            double totalAmount = basePrice * seatMultiplier;
            
            System.out.println("[AlquilerRepository] Cálculo: " + days + " días, " + 
                             numSeats + " plazas, importe: " + totalAmount);
            
            return totalAmount;
        } catch (Exception e) {
            System.err.println("[AlquilerRepository] Error calculando importe: " + e.getMessage());
            return 100.0; // Importe por defecto
        }
    }

public List<Alquiler> findCurrentAndFutureAlquileres() {
    try {
        System.out.println("=== DEBUG AlquilerRepository.findCurrentAndFutureAlquileres() ===");
        
        // Consulta que obtiene alquileres que empiezan hoy o después, 
        // O que están en curso (startDate <= hoy <= endDate)
        String query = "SELECT id, userId, startDate, endDate, registrationNumber, numSeats, totalAmount " +
                      "FROM Alquiler WHERE endDate >= CURDATE() ORDER BY startDate ASC";
        
        System.out.println("Query: " + query);
        
        List<Alquiler> result = jdbcTemplate.query(query, new RowMapper<Alquiler>() {
            @Override
            public Alquiler mapRow(ResultSet rs, int rowNumber) throws SQLException {
                System.out.println("Mapeando fila " + rowNumber);
                
                System.out.println("  id: " + rs.getInt("id"));
                System.out.println("  userid: " + rs.getString("userid"));
                System.out.println("  registrationNumber: " + rs.getString("registrationNumber"));
                System.out.println("  startDate: " + rs.getDate("startDate"));
                System.out.println("  endDate: " + rs.getDate("endDate"));
                System.out.println("  numSeats: " + rs.getInt("numSeats"));
                System.out.println("  totalAmount: " + rs.getDouble("totalAmount"));
                
                Alquiler alquiler = new Alquiler(
                        rs.getDate("startDate").toLocalDate(),
                        rs.getDate("endDate") != null ? rs.getDate("endDate").toLocalDate() : null,
                        rs.getString("registrationNumber"),
                        rs.getInt("numSeats"),
                        rs.getDouble("totalAmount"),
                        rs.getInt("id"),
                        rs.getString("userid"));
                
                System.out.println("Alquiler creado: " + alquiler);
                return alquiler;
            }
        });
        
        System.out.println("Consulta completada. Alquileres actuales y futuros: " + (result != null ? result.size() : "null"));
        System.out.println("=== FIN DEBUG AlquilerRepository.findCurrentAndFutureAlquileres ===");
        return result;

    } catch (DataAccessException e) {
        System.err.println("ERROR en findCurrentAndFutureAlquileres: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}

public List<Alquiler> findByDate(LocalDate fecha) {
    try {
        System.out.println("=== DEBUG AlquilerRepository.findByDate(" + fecha + ") ===");
        
        // Consulta para encontrar alquileres que están activos en la fecha especificada
        String query = "SELECT id, userId, startDate, endDate, registrationNumber, numSeats, totalAmount " +
                      "FROM Alquiler WHERE ? BETWEEN startDate AND endDate ORDER BY startDate ASC";
        
        System.out.println("Query: " + query);

        List<Alquiler> result = jdbcTemplate.query(query, new RowMapper<Alquiler>() {
            @Override
            public Alquiler mapRow(ResultSet rs, int rowNum) throws SQLException {
                System.out.println("Mapeando alquiler para fecha - fila " + rowNum);
                
                System.out.println("  id: " + rs.getInt("id"));
                System.out.println("  userid: " + rs.getString("userid"));
                System.out.println("  registrationNumber: " + rs.getString("registrationNumber"));
                System.out.println("  startDate: " + rs.getDate("startDate"));
                System.out.println("  endDate: " + rs.getDate("endDate"));
                System.out.println("  numSeats: " + rs.getInt("numSeats"));
                System.out.println("  totalAmount: " + rs.getDouble("totalAmount"));
                
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
        
        System.out.println("Alquileres encontrados para la fecha: " + (result != null ? result.size() : "null"));
        System.out.println("=== FIN DEBUG AlquilerRepository.findByDate ===");
        
        return result;
        
    } catch (DataAccessException e) {
        System.err.println("Unable to find alquileres by date: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}

}