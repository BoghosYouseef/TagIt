package com.tagit;



import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.tagit.db.DBInitializationScript;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.tagit.repository")
@EntityScan("com.tagit.model")
@EnableConfigurationProperties(AppConfig.class)
@DependsOnDatabaseInitialization
public class TagItApp {    
    private static final Logger logger = LoggerFactory.getLogger(TagItApp.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TagItApp.class, args);
        // AppConfig config = context.getBean(AppConfig.class);

        TagItGUI.setApplicationContext(context);

        // DBInitializationScript dbInit = new DBInitializationScript();
        // String dbName = config.getDatabaseName();
        // String dbPath = config.getDatabasePath();
        // String dbInitScriptPath = config.getDatabaseInitScriptPath();
        // String dbJDBCUrlConnectionString = config.getJDBCUrl();
        // dbInit.initializeDatabase(dbName, dbPath, dbInitScriptPath);
        

        // try{
        //     // Connection jdbcConnection = DriverManager.getConnection("jdbc:sqlite:" + dbPath + "/" + dbName + ".db", "root", "123456");
        //     Connection jdbcConnection = DriverManager.getConnection(dbJDBCUrlConnectionString, "root", "123456");
        //     DatabaseMetaData md = jdbcConnection.getMetaData();
        //     ResultSet rs = md.getTables(null, null, "%", null);
        //     ResultSet rs2 = md.getColumns(null, null, "tags", null);
        //     logger.info("Columns in 'tags' table:");
        //     while (rs.next()) {
        //         System.out.println(rs.getString(3));
                
        //     }
        //     while (rs2.next()) {
        //         logger.info(" - " + rs2.getString("COLUMN_NAME"));
        //     }

        // }
        // catch(SQLException e){
        //     logger.error("Database connection error: ", e);
        // }

        javafx.application.Application.launch(TagItGUI.class, args);
    }
}