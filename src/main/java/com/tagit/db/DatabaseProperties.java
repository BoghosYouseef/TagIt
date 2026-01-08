package com.tagit.db;

import org.slf4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "db")
public class DatabaseProperties {
    private String databaseName;
    private String databasePath;
    private String databaseInitScriptPath;
    Logger logger = org.slf4j.LoggerFactory.getLogger(DatabaseProperties.class);


    public DatabaseProperties() {
        logger.info("DatabaseProperties initialized with default constructor.");
        logger.warn("Database name: " + databaseName);
        logger.warn("Database path: " + databasePath);
    }
    
    // Getters and Setters
    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public String getDatabaseInitScriptPath() {
        return databaseInitScriptPath;
    }

    public void setDatabaseInitScriptPath(String databaseInitScriptPath) {
        this.databaseInitScriptPath = databaseInitScriptPath;
    }
    
    @Bean
    public DatabaseConfig databaseConfig() {
        return new DatabaseConfig(databaseName, databasePath, databaseInitScriptPath);
    }
}
