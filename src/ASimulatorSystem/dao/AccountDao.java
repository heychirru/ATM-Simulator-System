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
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cardNumber);
            try (ResultSet result = statement.executeQuery()) { return result.next() ? result.getLong("id") : null; }
        }
    }
    public AccountRecord findByCardNumber(String cardNumber) throws SQLException {
        String sql = "SELECT id, card_number, pin_hash, balance, status, failed_pin_attempts FROM accounts WHERE card_number = ?";
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cardNumber);
            try (ResultSet result = statement.executeQuery()) { return result.next() ? map(result) : null; }
        }
    }
    public AccountRecord findById(long accountId) throws SQLException {
        String sql = "SELECT id, card_number, pin_hash, balance, status, failed_pin_attempts FROM accounts WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, accountId);
            try (ResultSet result = statement.executeQuery()) { return result.next() ? map(result) : null; }
        }
    }
    public long createAccount(String cardNumber, String pinHash, BigDecimal initialBalance) throws SQLException {
        String sql = "INSERT INTO accounts(card_number, pin_hash, balance) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, cardNumber); statement.setString(2, pinHash); statement.setBigDecimal(3, initialBalance); statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) { if (!keys.next()) throw new SQLException("Unable to create account"); return keys.getLong(1); }
        }
    }
    public void recordInitialDeposit(long accountId, String reference, BigDecimal amount) throws SQLException {
        String sql = "INSERT INTO transactions(account_id, reference, type, amount, balance_after) VALUES (?, ?, 'DEPOSIT', ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, accountId); statement.setString(2, reference); statement.setBigDecimal(3, amount); statement.setBigDecimal(4, amount); statement.executeUpdate();
        }
    }
    public void updateFailedAttempts(long accountId, int attempts, String status) throws SQLException {
        String sql = "UPDATE accounts SET failed_pin_attempts = ?, status = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, attempts); statement.setString(2, status); statement.setLong(3, accountId); statement.executeUpdate();
        }
    }
    public void resetFailedAttempts(long accountId) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE accounts SET failed_pin_attempts = 0 WHERE id = ?")) {
            statement.setLong(1, accountId); statement.executeUpdate();
        }
    }
    public void updatePinHash(long accountId, String pinHash) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE accounts SET pin_hash = ?, failed_pin_attempts = 0 WHERE id = ?")) {
            statement.setString(1, pinHash); statement.setLong(2, accountId); statement.executeUpdate();
        }
    }
    private AccountRecord map(ResultSet result) throws SQLException {
        return new AccountRecord(result.getLong("id"), result.getString("card_number"), result.getString("pin_hash"), result.getBigDecimal("balance"), result.getString("status"), result.getInt("failed_pin_attempts"));
    }
    public record AccountRecord(long id, String cardNumber, String pinHash, BigDecimal balance, String status, int failedPinAttempts) { }
}
