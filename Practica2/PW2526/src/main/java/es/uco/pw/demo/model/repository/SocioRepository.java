package es.uco.pw.demo.model.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import es.uco.pw.demo.model.domain.Socio;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SocioRepository extends AbstractRepository {

    public SocioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.setSQLQueriesFileName(sqlQueriesFileName);

        if (sqlQueries != null) {
            System.out.println("[SocioRepository] Successfully loaded SQL queries. Total: " + sqlQueries.size());
        } else {
            System.err.println("[SocioRepository] ERROR: SQL queries not loaded!");
        }
    }

    public List<Socio> findAllSocios() {
        try {
            String query = sqlQueries.getProperty("socio.findAll");
            System.out.println("[SocioRepository] Executing findAll: " + query);

            if (query == null) {
                System.err.println("[SocioRepository] ERROR: Query 'socio.findAll' not found!");
                return null;
            }

            List<Socio> result = jdbcTemplate.query(query, new RowMapper<Socio>() {
                @Override
                public Socio mapRow(ResultSet rs, int rowNumber) throws SQLException {
                    Socio socio = new Socio(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("address"),
                            rs.getDate("birthdate").toLocalDate(),
                            rs.getBoolean("isBoatDriver"));

                    // Establecer propiedades adicionales
                    socio.setIsHolderInscription(rs.getBoolean("isHolderInscription"));
                    socio.setIsAdult(rs.getBoolean("isAdult"));
                    socio.setInscriptionId(rs.getInt("inscriptionId"));

                    return socio;
                }
            });
            return result;

        } catch (DataAccessException e) {
            System.err.println("Unable to find socios");
            e.printStackTrace();
            return null;
        }
    }

    public Socio findById(String id) {
        try {
            String query = sqlQueries.getProperty("socio.findById");
            System.out.println("[SocioRepository] Executing findById: " + query + " with id: " + id);

            List<Socio> results = jdbcTemplate.query(query, new RowMapper<Socio>() {
                @Override
                public Socio mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Socio socio = new Socio(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("address"),
                            rs.getDate("birthdate").toLocalDate(),
                            rs.getBoolean("isBoatDriver"));

                    socio.setIsHolderInscription(rs.getBoolean("isHolderInscription"));
                    socio.setIsAdult(rs.getBoolean("isAdult"));
                    socio.setInscriptionId(rs.getInt("inscriptionId"));

                    return socio;
                }
            }, id);

            return results.isEmpty() ? null : results.get(0);

        } catch (DataAccessException e) {
            System.err.println("Unable to find socio by id: " + id);
            e.printStackTrace();
            return null;
        }
    }


    public boolean addSocio(Socio socio) {
        try {
            String query = sqlQueries.getProperty("socio.insert2");
            if (query == null) {
                System.err.println("SQL query for socio.insert not found.");
                return false;
            }

            System.out.println("[SocioRepository] Executing insert: " + query);
            System.out.println("[SocioRepository] Inserting socio: " + socio.getId() + ", " + socio.getName());

            int result = jdbcTemplate.update(query,
                    socio.getId(),
                    socio.getName(),
                    socio.getSurname(),
                    socio.getAddress(),
                    socio.getBirthdate(),
                    socio.getInscriptionDate(),
                    socio.getIsHolderInscription(),
                    socio.getIsBoatDriver(),
                    socio.getIsAdult(),
                    socio.getInscriptionId());

            return result > 0;

        } catch (DataAccessException e) {
            System.err.println("Unable to save socio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean addSocioAdult(Socio socio) {
        try {
            String query = sqlQueries.getProperty("socio.insert1");
            if (query == null) {
                System.err.println("SQL query for socio.insert not found.");
                return false;
            }

            System.out.println("[SocioRepository] Executing insert: " + query);
            System.out.println("[SocioRepository] Inserting socio: " + socio.getId() + ", " + socio.getName());

            int result = jdbcTemplate.update(query,
                    socio.getId(),
                    socio.getName(),
                    socio.getSurname(),
                    socio.getAddress(),
                    socio.getBirthdate(),
                    socio.getInscriptionDate(),
                    socio.getIsHolderInscription(),
                    socio.getIsBoatDriver(),
                    socio.getIsAdult());

            return result > 0;

        } catch (DataAccessException e) {
            System.err.println("Unable to save socio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateInscriptionId(Socio socio) {
    try {
        String query = sqlQueries.getProperty("socio.update");
        int result = jdbcTemplate.update(query, socio.getInscriptionId(), socio.getId());
        return result > 0;
    } catch (DataAccessException e) {
        System.err.println("Error al actualizar inscriptionId del socio:");
        e.printStackTrace();
        return false;
    }
}


    public boolean updateIsBoatDriver(String idSocio, boolean isBoatDriver) {
        try {
            String query = sqlQueries.getProperty("socio.updateIsBoatDriver");
            int result = jdbcTemplate.update(query, isBoatDriver, idSocio);
            return result > 0;
        } catch (DataAccessException e) {
            System.err.println("Unable to update isBoatDriver for socio with id: " + idSocio);
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSocio(Socio socio) {
        try {
            String query = sqlQueries.getProperty("socio.updateSocio");
            int result = jdbcTemplate.update(query, socio.getName(), socio.getSurname(), socio.getAddress(), socio.getBirthdate(), socio.getId());
            return result > 0;
        } catch (DataAccessException e) {
            System.err.println("Unable to update socio with id: " + socio.getId());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTodo(Socio socio) {
        try {
            String query = sqlQueries.getProperty("socio.updateTodo");
            int result = jdbcTemplate.update(query, socio.getName(), socio.getSurname(), socio.getAddress(), socio.getBirthdate(), socio.getInscriptionDate(), socio.getIsHolderInscription(), socio.getIsBoatDriver(), socio.getIsAdult(), socio.getInscriptionId(), socio.getId());
            return result > 0;
        } catch (DataAccessException e) {
            System.err.println("Unable to update socio with id: " + socio.getId());
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsById(String id) {
        try {
            String query = sqlQueries.getProperty("socio.existsById");
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, id);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            System.err.println("Unable to check existence of socio by id" + id);
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteIsNotHolder(String id){
        try{
            String query = sqlQueries.getProperty("socio.deleteIsNotHolder");
            if(query != null){
                int result = jdbcTemplate.update(query, id);
                if (result>0)
                    return true;
                else
                    return false;
            }
            else
                return false;
        }catch(DataAccessException exception){
            return false;
        }
    }


}