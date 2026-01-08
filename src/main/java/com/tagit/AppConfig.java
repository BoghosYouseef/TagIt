package com.tagit;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.tagit.db.DatabaseProperties;

@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private static final Properties properties = new Properties();

    private String title;
    private double minimumTabPaneWidth;
    private double minimumWindowWidth;
    private DatabaseProperties databaseProperties;

    private Window window = new Window();

    @Autowired
    public AppConfig(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    public static class Window {
        private int width;
        private int height;


        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key);
    }

    public double getMinimumWindowWidth() {
        // return Double.parseDouble(getProperty("app.minimumTabPaneWidth", "200"));
        return minimumWindowWidth;
    }

    public void setMinimumWindowWidth(double minimumWindowWidth) {
        // return Double.parseDouble(getProperty("app.minimumTabPaneWidth", "200"));
        this.minimumWindowWidth = minimumWindowWidth;
    }

    public double getMinimumTabPaneWidth() {
        // return Double.parseDouble(getProperty("app.minimumTabPaneWidth", "200"));
        return minimumTabPaneWidth;
    }

    public void setMinimumTabPaneWidth(double minimumTabPaneWidth) {
        // return Double.parseDouble(getProperty("app.minimumTabPaneWidth", "200"));
        this.minimumTabPaneWidth = minimumTabPaneWidth;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public String getDatabaseName() {
        return databaseProperties.getDatabaseName();
    }

    public String getDatabasePath() {
        return databaseProperties.getDatabasePath();
    }

    public String getDatabaseInitScriptPath() {
        return databaseProperties.getDatabaseInitScriptPath();
    }
}
