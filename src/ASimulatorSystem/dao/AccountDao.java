package ASimulatorSystem.dao;

import ASimulatorSystem.config.DatabaseConfig;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Database access for ATM accounts. */
public class AccountDao {

    public Long findIdByCardNumber(String cardNumber) throws SQLException {
        String sql = "SELECT id FROM accounts WHERE card_number = ? AND status = 'ACTIVE'";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cardNumber);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getLong("id") : null;
            }
        }
    }

    public AccountRecord findByCardNumber(String cardNumber) throws SQLException {
        String sql = "SELECT id, card_number, pin_hash, balance, status, failed_pin_attempts "
                + "FROM accounts WHERE card_number = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cardNumber);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }
                return new AccountRecord(
                        result.getLong("id"),
                        result.getString("card_number"),
                        result.getString("pin_hash"),
                        result.getBigDecimal("balance"),
                        result.getString("status"),
                        result.getInt("failed_pin_attempts"));
            }
        }
    }

    public record AccountRecord(
            long id,
            String cardNumber,
            String pinHash,
            BigDecimal balance,
            String status,
            int failedPinAttempts) {
    }
}
