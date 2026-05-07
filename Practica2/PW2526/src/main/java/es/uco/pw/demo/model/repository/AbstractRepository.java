package es.uco.pw.demo.model.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.springframework.jdbc.core.JdbcTemplate;

public class AbstractRepository {
    
    protected JdbcTemplate jdbcTemplate;
    protected Properties sqlQueries;
    protected String sqlQueriesFileName;

    // REFACTORIZACIÓN (Regla 1): Se cambia el parámetro para que coincida exactamente con la intención del campo de clase.
    public void setSQLQueriesFileName(String sqlQueriesFileName){
        this.sqlQueriesFileName = sqlQueriesFileName;
        createProperties();
    }

    // SEMANA 4: Variable en línea. Nos ahorramos declarar 'BufferedReader reader' en una línea separada.
    private void createProperties(){
        sqlQueries = new Properties();
        try {
            File sqlFile = new File(sqlQueriesFileName); // REFACTORIZACIÓN: f por sqlFile (Regla 6)
            sqlQueries.load(new BufferedReader(new FileReader(sqlFile)));
        } catch (IOException e) {
            System.err.println("Error creating properties object for SQL queries");
            e.printStackTrace();
        }
    }
}