package ASimulatorSystem.dao;

import ASimulatorSystem.config.DatabaseConfig;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Atomic balance updates and transaction history for the Swing client. */
public class TransactionDao {
    public BigDecimal getBalance(long accountId) throws SQLException {
        try (Connection c = DatabaseConfig.getConnection(); PreparedStatement s = c.prepareStatement("SELECT balance FROM accounts WHERE id = ?")) {
            s.setLong(1, accountId);
            try (ResultSet r = s.executeQuery()) { if (!r.next()) throw new SQLException("Account not found"); return r.getBigDecimal(1); }
        }
    }

    public BigDecimal deposit(long accountId, BigDecimal amount) throws SQLException { return changeBalance(accountId, amount, "DEPOSIT"); }

    public BigDecimal withdraw(long accountId, BigDecimal amount) throws SQLException {
        try (Connection c = DatabaseConfig.getConnection()) {
            c.setAutoCommit(false);
            try {
                BigDecimal current;
                try (PreparedStatement lock = c.prepareStatement("SELECT balance FROM accounts WHERE id = ? AND status = 'ACTIVE' FOR UPDATE")) {
                    lock.setLong(1, accountId);
                    try (ResultSet r = lock.executeQuery()) { if (!r.next()) throw new IllegalArgumentException("Active account not found."); current = r.getBigDecimal(1); }
                }
                BigDecimal used;
                try (PreparedStatement s = c.prepareStatement("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE account_id = ? AND type = 'WITHDRAWAL' AND created_at >= CURRENT_TIMESTAMP - INTERVAL 1 DAY")) {
                    s.setLong(1, accountId); try (ResultSet r = s.executeQuery()) { r.next(); used = r.getBigDecimal(1); }
                }
                if (used.add(amount).compareTo(new BigDecimal("20000.00")) > 0) throw new IllegalArgumentException("Daily withdrawal limit is Rs. 20,000.");
                BigDecimal next = current.subtract(amount);
                if (next.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Insufficient balance.");
                updateAndRecord(c, accountId, next, "WITHDRAWAL", amount);
                c.commit();
                return next;
            } catch (Exception e) {
                c.rollback();
                if (e instanceof SQLException) throw (SQLException)e;
                if (e instanceof RuntimeException) throw (RuntimeException)e;
                throw new SQLException(e);
            } finally { c.setAutoCommit(true); }
        }
    }

    private BigDecimal changeBalance(long accountId, BigDecimal delta, String type) throws SQLException {
        try (Connection c = DatabaseConfig.getConnection()) {
            c.setAutoCommit(false);
            try {
                BigDecimal current;
                try (PreparedStatement lock = c.prepareStatement("SELECT balance FROM accounts WHERE id = ? AND status = 'ACTIVE' FOR UPDATE")) {
                    lock.setLong(1, accountId); try (ResultSet r = lock.executeQuery()) { if (!r.next()) throw new IllegalArgumentException("Active account not found."); current = r.getBigDecimal(1); }
                }
                BigDecimal next = current.add(delta);
                if (next.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Insufficient balance.");
                updateAndRecord(c, accountId, next, type, delta.abs());
                c.commit(); return next;
            } catch (Exception e) {
                c.rollback();
                if (e instanceof SQLException) throw (SQLException)e;
                if (e instanceof RuntimeException) throw (RuntimeException)e;
                throw new SQLException(e);
            } finally { c.setAutoCommit(true); }
        }
    }

    private void updateAndRecord(Connection c, long accountId, BigDecimal next, String type, BigDecimal amount) throws SQLException {
        try (PreparedStatement update = c.prepareStatement("UPDATE accounts SET balance = ? WHERE id = ?")) {
            update.setBigDecimal(1, next); update.setLong(2, accountId); update.executeUpdate();
        }
        try (PreparedStatement insert = c.prepareStatement("INSERT INTO transactions(account_id, reference, type, amount, balance_after) VALUES (?, ?, ?, ?, ?)")) {
            insert.setLong(1, accountId); insert.setString(2, UUID.randomUUID().toString()); insert.setString(3, type); insert.setBigDecimal(4, amount); insert.setBigDecimal(5, next); insert.executeUpdate();
        }
    }

    public List<TransactionRecord> recent(long accountId, int limit) throws SQLException {
        int safeLimit = Math.max(1, Math.min(limit, 50));
        String sql = "SELECT reference, type, amount, balance_after, created_at FROM transactions WHERE account_id = ? ORDER BY created_at DESC, id DESC LIMIT ?";
        List<TransactionRecord> records = new ArrayList<>();
        try (Connection c = DatabaseConfig.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, accountId); s.setInt(2, safeLimit);
            try (ResultSet r = s.executeQuery()) { while (r.next()) records.add(new TransactionRecord(r.getString(1), r.getString(2), r.getBigDecimal(3), r.getBigDecimal(4), r.getTimestamp(5))); }
        }
        return records;
    }

    public static final class TransactionRecord {
        private final String reference; private final String type; private final BigDecimal amount; private final BigDecimal balanceAfter; private final java.sql.Timestamp createdAt;
        public TransactionRecord(String reference, String type, BigDecimal amount, BigDecimal balanceAfter, java.sql.Timestamp createdAt) { this.reference=reference; this.type=type; this.amount=amount; this.balanceAfter=balanceAfter; this.createdAt=createdAt; }
        public String reference(){return reference;} public String type(){return type;} public BigDecimal amount(){return amount;} public BigDecimal balanceAfter(){return balanceAfter;} public java.sql.Timestamp createdAt(){return createdAt;}
    }
}
