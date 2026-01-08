package com.tagit.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;


public class DBInitializationScript {
    Logger logger = org.slf4j.LoggerFactory.getLogger(DBInitializationScript.class);
    


    public void initializeDatabase(String databaseName, String databasePath, String initScriptFilePath) {

        logger.info("Initializing database...");

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath + "/" + databaseName + ".db")) {
            Statement jdbcStatement = connection.createStatement();
            String sqlScript = getSQLInitScript(initScriptFilePath);
            logger.info("Executing SQL script: \n" + sqlScript);
            jdbcStatement.execute(sqlScript);
        } catch (SQLException e) {
            logger.error("Database initialization error: ", e);
        }
        logger.info("Database initialized successfully.");
    }

    private String getSQLInitScript(String initScriptFilePath) {
        try {
            logger.info("Reading SQL init script from: " + initScriptFilePath);
            return Files.readString(Path.of(initScriptFilePath));
        } catch (Exception e) {
            logger.error("Error reading SQL init script: ", e);
            return "";
        }
    }
}
