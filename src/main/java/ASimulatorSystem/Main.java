package ASimulatorSystem;

import ASimulatorSystem.config.DatabaseConfig;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Entry point for the ATM desktop application. */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Fall back to the platform look and feel.
        }

        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseConfig::closePool, "atm-db-pool-shutdown"));
        SwingUtilities.invokeLater(Login::new);
    }
}
