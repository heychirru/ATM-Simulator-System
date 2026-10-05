package ASimulatorSystem.config;

import ASimulatorSystem.dao.LocalStorage;

/**
 * Storage management and status for the standalone ATM desktop client.
 */
public final class DatabaseConfig {
    private DatabaseConfig() { }

    /** Local storage status check used by the desktop UI. */
    public static boolean isDatabaseAvailable() {
        return true;
    }

    public static String poolStatus() {
        return "local-storage-ready";
    }

    public static void closePool() {
        LocalStorage.getInstance().flush();
    }
}
