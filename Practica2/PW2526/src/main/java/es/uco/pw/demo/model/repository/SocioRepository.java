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
    }

    public List<Socio> findAllSocios() {
        try {
            String query = sqlQueries.getProperty("socio.findAll");
            if (query == null) return null;

            List<Socio> fetchedSocios = jdbcTemplate.query(query, new RowMapper<Socio>() {
                @Override
                public Socio mapRow(ResultSet rs, int rowNumber) throws SQLException {
                    Socio socio = new Socio(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("address"),
                            rs.getDate("birthdate").toLocalDate(),
                            rs.getBoolean("isBoatDriver"));

                    socio.setHolderInscription(rs.getBoolean("isHolderInscription")); // Adaptado (isX)
                    socio.setAdult(rs.getBoolean("isAdult")); // Adaptado (isX)
                    socio.setInscriptionId(rs.getInt("inscriptionId"));

                    return socio;
                }
            });
            return fetchedSocios;

        } catch (DataAccessException e) {
            return null;
        }
    }

    public Socio findById(String socioId) { // REFACTORIZACIÓN: id -> socioId
        try {
            String query = sqlQueries.getProperty("socio.findById");
            List<Socio> fetchedSocios = jdbcTemplate.query(query, new RowMapper<Socio>() {
                @Override
                public Socio mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Socio socio = new Socio(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("address"),
                            rs.getDate("birthdate").toLocalDate(),
                            rs.getBoolean("isBoatDriver"));

                    socio.setHolderInscription(rs.getBoolean("isHolderInscription"));
                    socio.setAdult(rs.getBoolean("isAdult"));
                    socio.setInscriptionId(rs.getInt("inscriptionId"));

                    return socio;
                }
            }, socioId);

            return fetchedSocios.isEmpty() ? null : fetchedSocios.get(0);

        } catch (DataAccessException e) {
            return null;
        }
    }

    public boolean addSocio(Socio socio) {
        try {
            String queryForFullSocio = sqlQueries.getProperty("socio.insert2"); // REFACTORIZACIÓN (Regla 1 y 4)
            if (queryForFullSocio == null) return false;

            int rowsAffected = jdbcTemplate.update(queryForFullSocio,
                    socio.getSocioId(), // Adaptado
                    socio.getName(),
                    socio.getSurname(),
                    socio.getAddress(),
                    socio.getBirthdate(),
                    socio.getInscriptionDate(),
                    socio.isHolderInscription(), // Adaptado
                    socio.isBoatDriver(), // Adaptado
                    socio.isAdult(), // Adaptado
                    socio.getInscriptionId());

            return rowsAffected > 0;

        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean addSocioAdult(Socio socio) {
        try {
            String queryForAdultSocio = sqlQueries.getProperty("socio.insert1"); // REFACTORIZACIÓN (Regla 1 y 4)
            if (queryForAdultSocio == null) return false;

            int rowsAffected = jdbcTemplate.update(queryForAdultSocio,
                    socio.getSocioId(),
                    socio.getName(),
                    socio.getSurname(),
                    socio.getAddress(),
                    socio.getBirthdate(),
                    socio.getInscriptionDate(),
                    socio.isHolderInscription(),
                    socio.isBoatDriver(),
                    socio.isAdult());

            return rowsAffected > 0;

        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean updateInscriptionId(Socio socio) {
        try {
            String query = sqlQueries.getProperty("socio.update");
            int rowsAffected = jdbcTemplate.update(query, socio.getInscriptionId(), socio.getSocioId());
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean updateIsBoatDriver(String socioId, boolean isBoatDriver) { // REFACTORIZACIÓN: idSocio -> socioId
        try {
            String query = sqlQueries.getProperty("socio.updateIsBoatDriver");
            int rowsAffected = jdbcTemplate.update(query, isBoatDriver, socioId);
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean updateSocio(Socio socio) {
        try {
            String query = sqlQueries.getProperty("socio.updateSocio");
            int rowsAffected = jdbcTemplate.update(query, socio.getName(), socio.getSurname(), socio.getAddress(), socio.getBirthdate(), socio.getSocioId());
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    // REFACTORIZACIÓN (Regla de verbos para métodos): Se renombra updateTodo a updateAllSocioFields
    public boolean updateAllSocioFields(Socio socio) { 
        try {
            String query = sqlQueries.getProperty("socio.updateTodo"); // El property lo mantenemos igual para no romper BBDD
            int rowsAffected = jdbcTemplate.update(query, socio.getName(), socio.getSurname(), socio.getAddress(), socio.getBirthdate(), socio.getInscriptionDate(), socio.isHolderInscription(), socio.isBoatDriver(), socio.isAdult(), socio.getInscriptionId(), socio.getSocioId());
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean existsById(String socioId) {
        try {
            String query = sqlQueries.getProperty("socio.existsById");
            Integer existenceCount = jdbcTemplate.queryForObject(query, Integer.class, socioId);
            return existenceCount != null && existenceCount > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    // REFACTORIZACIÓN (Regla de verbos): Se renombra deleteIsNotHolder a deleteIfNotHolder
    public boolean deleteIfNotHolder(String socioId){
        try{
            String query = sqlQueries.getProperty("socio.deleteIsNotHolder");
            if(query != null){
                int rowsAffected = jdbcTemplate.update(query, socioId);
                return rowsAffected > 0;
            }
            return false;
        }catch(DataAccessException exception){
            return false;
        }
    }
}