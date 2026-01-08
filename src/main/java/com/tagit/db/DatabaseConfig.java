package com.tagit.db;

public record DatabaseConfig(String databaseName, String databasePath, String databaseInitScriptPath) {
    public DatabaseConfig {
        if (databaseName == null || databasePath == null || databaseInitScriptPath == null) {
            throw new IllegalArgumentException("Database name, path, or script path cannot be null");
        }
    }
}
