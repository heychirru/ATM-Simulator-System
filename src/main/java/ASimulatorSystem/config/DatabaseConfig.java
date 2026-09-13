package ASimulatorSystem.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/** Centralized MySQL configuration backed by a small HikariCP connection pool. */
public final class DatabaseConfig {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/bankmanagementsystem";
    private static final String URL = value("DB_URL", "db.url", DEFAULT_URL);
    private static final String USERNAME = value("DB_USERNAME", "db.username", "root");
    private static final String PASSWORD = value("DB_PASSWORD", "db.password", "");
    private static final HikariDataSource DATA_SOURCE = createDataSource();

    private DatabaseConfig() { }

    private static HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setPoolName("ChirruATM-Pool");
        config.setMaximumPoolSize(8);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(5000);
        config.setValidationTimeout(3000);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(600000);
        config.setLeakDetectionThreshold(10000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }

    private static String value(String environmentName, String propertyName, String defaultValue) {
        String property = System.getProperty(propertyName);
        if (property != null && !property.isBlank()) return property;
        String environment = System.getenv(environmentName);
        if (environment != null && !environment.isBlank()) return environment;
        return defaultValue;
    }

    /** Borrows a pooled connection. Closing it returns it to the pool. */
    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }

    /** Lightweight health check used by the desktop UI. */
    public static boolean isDatabaseAvailable() {
        try (Connection connection = getConnection()) {
            return connection.isValid(2);
        } catch (SQLException ex) {
            return false;
        }
    }

    public static String poolStatus() {
        var pool = DATA_SOURCE.getHikariPoolMXBean();
        if (pool == null) return "starting";
        return "active=" + pool.getActiveConnections()
                + ", idle=" + pool.getIdleConnections()
                + ", total=" + pool.getTotalConnections();
    }

    public static void closePool() {
        if (!DATA_SOURCE.isClosed()) DATA_SOURCE.close();
    }
}
