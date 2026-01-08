package com.tagit.integration.DataBaseIntegrationTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.tagit.AppConfig;
import com.tagit.db.DBInitializationScript;


@SpringBootTest
@ActiveProfiles("test")
public class DataBaseIntegrationTest {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DataBaseIntegrationTest.class);
    
    @Autowired
    private AppConfig config;
    
    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testConfig() {
        assertNotNull(config);
    }

    @Test
    void dataBaseGetsInitializedSuccessfully() {
        DBInitializationScript dbInit = new DBInitializationScript();
        String dbName = config.getDatabaseName();
        String dbPath = config.getDatabasePath();
        String dbInitScriptPath = config.getDatabaseInitScriptPath();
        logger.info("database name: " + dbName);
        logger.info("database path: " + dbPath);
        if (dbInitScriptPath == null) {
            logger.error("Database init script path is null!");
            throw new IllegalArgumentException("Database init script path cannot be null");
        } else {
            logger.info("Database init script path: " + dbInitScriptPath);
        }
        dbInit.initializeDatabase(dbName, dbPath, dbInitScriptPath);

        try{
            Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:sqlite:" + dbPath + "/" + dbName + ".db",
                "root",
                "123456");

            DatabaseMetaData md = jdbcConnection.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            ResultSet rs2 = md.getColumns(null, null, "files", null);
            logger.info("Columns in 'files' table:");
            while (rs.next()) {
                System.out.println(rs.getString(3));
                
            }
            while (rs2.next()) {
                logger.info(" - " + rs2.getString("COLUMN_NAME"));
            }

        }
        catch(SQLException e){
            logger.error("Database connection error: ", e);
        }
    }

    private void cleanDataBase() {
        // Implement database cleanup logic here
    }

}