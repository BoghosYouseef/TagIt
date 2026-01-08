package com.tagit;



import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;


import com.tagit.db.DBInitializationScript;

@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
public class TagItApp {    
    private static final Logger logger = LoggerFactory.getLogger(TagItApp.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TagItApp.class, args);
        AppConfig config = context.getBean(AppConfig.class);

        TagItGUI.setApplicationContext(context);

        DBInitializationScript dbInit = new DBInitializationScript();
        String dbName = config.getDatabaseName();
        String dbPath = config.getDatabasePath();
        String dbInitScriptPath = config.getDatabaseInitScriptPath();
        dbInit.initializeDatabase(dbName, dbPath, dbInitScriptPath);

        try{
            Connection jdbcConnection = DriverManager.getConnection("jdbc:sqlite:" + dbPath + "/" + dbName + ".db", "root", "123456");
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
        javafx.application.Application.launch(TagItGUI.class, args);
    }
}