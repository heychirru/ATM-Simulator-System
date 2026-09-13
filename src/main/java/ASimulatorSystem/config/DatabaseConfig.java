package ASimulatorSystem.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centralized database configuration.
 * Values can be supplied with JVM system properties or environment variables.
 */
public final class DatabaseConfig {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/bankmanagementsystem";
    private static final String URL = value("DB_URL", "db.url", DEFAULT_URL);
    private static final String USERNAME = value("DB_USERNAME", "db.username", "root");
    private static final String PASSWORD = value("DB_PASSWORD", "db.password", "");

    private DatabaseConfig() {
    }

    private static String value(String environmentName, String propertyName, String defaultValue) {
        String property = System.getProperty(propertyName);
        if (property != null && !property.isBlank()) {
            return property;
        }

        String environment = System.getenv(environmentName);
        if (environment != null && !environment.isBlank()) {
            return environment;
        }

        return defaultValue;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
