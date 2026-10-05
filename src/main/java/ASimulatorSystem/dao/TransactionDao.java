package ASimulatorSystem.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/** Atomic balance updates and transaction history using local embedded storage. */
public class TransactionDao {
    private final LocalStorage storage = LocalStorage.getInstance();

    public BigDecimal getBalance(long accountId) throws SQLException {
        try {
            return storage.getBalance(accountId);
        } catch (IllegalArgumentException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public BigDecimal deposit(long accountId, BigDecimal amount) throws SQLException {
        try {
            return storage.deposit(accountId, amount);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    public BigDecimal withdraw(long accountId, BigDecimal amount) throws SQLException {
        try {
            return storage.withdraw(accountId, amount);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    public List<TransactionRecord> recent(long accountId, int limit) throws SQLException {
        return storage.getRecentTransactions(accountId, limit);
    }

    public static final class TransactionRecord {
        private final String reference;
        private final String type;
        private final BigDecimal amount;
        private final BigDecimal balanceAfter;
        private final Timestamp createdAt;

        public TransactionRecord(String reference, String type, BigDecimal amount, BigDecimal balanceAfter, Timestamp createdAt) {
            this.reference = reference;
            this.type = type;
            this.amount = amount;
            this.balanceAfter = balanceAfter;
            this.createdAt = createdAt;
        }

        public String reference() { return reference; }
        public String type() { return type; }
        public BigDecimal amount() { return amount; }
        public BigDecimal balanceAfter() { return balanceAfter; }
        public Timestamp createdAt() { return createdAt; }
    }
}
