package ASimulatorSystem.dao;

import java.math.BigDecimal;
import java.sql.SQLException;

/** Local embedded data access for ATM accounts. */
public class AccountDao {
    private final LocalStorage storage = LocalStorage.getInstance();

    public AccountRecord findByCardNumber(String cardNumber) throws SQLException {
        return storage.findAccountByCardNumber(cardNumber);
    }

    public AccountRecord findById(long accountId) throws SQLException {
        return storage.findAccountById(accountId);
    }

    public long createAccount(String cardNumber, String pinHash, BigDecimal initialBalance) throws SQLException {
        return storage.createAccount(cardNumber, pinHash, initialBalance);
    }

    public void updateFailedAttempts(long accountId, int attempts, String status) throws SQLException {
        storage.updateFailedAttempts(accountId, attempts, status);
    }

    public void resetFailedAttempts(long accountId) throws SQLException {
        storage.resetFailedAttempts(accountId);
    }

    public void updatePinHash(long accountId, String pinHash) throws SQLException {
        storage.updatePinHash(accountId, pinHash);
    }

    public static final class AccountRecord {
        private final long id;
        private final String cardNumber;
        private final String pinHash;
        private final BigDecimal balance;
        private final String status;
        private final int failedPinAttempts;

        public AccountRecord(long id, String cardNumber, String pinHash, BigDecimal balance, String status, int failedPinAttempts) {
            this.id = id;
            this.cardNumber = cardNumber;
            this.pinHash = pinHash;
            this.balance = balance;
            this.status = status;
            this.failedPinAttempts = failedPinAttempts;
        }

        public long id() { return id; }
        public String cardNumber() { return cardNumber; }
        public String pinHash() { return pinHash; }
        public BigDecimal balance() { return balance; }
        public String status() { return status; }
        public int failedPinAttempts() { return failedPinAttempts; }
    }
}
