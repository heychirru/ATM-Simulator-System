package ASimulatorSystem.service;

import ASimulatorSystem.dao.AccountDao;
import ASimulatorSystem.security.PinHasher;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Authentication and account creation business rules for the desktop client. */
public class AuthService {
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final Logger LOG = Logger.getLogger(AuthService.class.getName());
    private final AccountDao accountDao = new AccountDao();

    public AuthResult authenticate(String cardNumber, String pin) throws SQLException {
        if (cardNumber == null || pin == null) return AuthResult.failure("Card number and PIN are required.");

        AccountDao.AccountRecord account = accountDao.findByCardNumber(cardNumber);
        if (account == null) return AuthResult.failure("Invalid card number or PIN.");
        if (!"ACTIVE".equals(account.status())) return AuthResult.failure("Account is " + account.status().toLowerCase() + ".");

        if (!PinHasher.matches(pin, account.pinHash())) {
            int attempts = account.failedPinAttempts() + 1;
            accountDao.updateFailedAttempts(account.id(), attempts,
                    attempts >= MAX_FAILED_ATTEMPTS ? "BLOCKED" : "ACTIVE");
            LOG.log(Level.WARNING, "Failed authentication attempt for account {0}", account.id());
            if (attempts >= MAX_FAILED_ATTEMPTS) return AuthResult.failure("Too many failed attempts. Account blocked.");
            return AuthResult.failure("Invalid card number or PIN. Attempts left: "
                    + (MAX_FAILED_ATTEMPTS - attempts));
        }

        accountDao.resetFailedAttempts(account.id());
        return AuthResult.success(new AuthenticatedAccount(account.id(), account.cardNumber()));
    }

    public long createAccount(String cardNumber, String pin, BigDecimal initialDeposit) throws SQLException {
        validateCard(cardNumber);
        validatePin(pin);
        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0 || initialDeposit.scale() > 2) {
            throw new IllegalArgumentException("Initial deposit must be a non-negative amount with at most 2 decimals.");
        }
        if (accountDao.findByCardNumber(cardNumber) != null) {
            throw new IllegalArgumentException("Card number already exists.");
        }
        return accountDao.createAccount(cardNumber, PinHasher.hash(pin), initialDeposit);
    }

    public void changePin(long accountId, String currentPin, String newPin) throws SQLException {
        validatePin(newPin);
        if (currentPin == null || !currentPin.matches("\\d{4}")) {
            throw new IllegalArgumentException("Current PIN must contain exactly 4 digits.");
        }
        if (currentPin.equals(newPin)) {
            throw new IllegalArgumentException("New PIN must be different from the current PIN.");
        }
        AccountDao.AccountRecord account = accountDao.findById(accountId);
        if (account == null || !"ACTIVE".equals(account.status())
                || !PinHasher.matches(currentPin, account.pinHash())) {
            throw new IllegalArgumentException("Current PIN is incorrect.");
        }
        accountDao.updatePinHash(accountId, PinHasher.hash(newPin));
    }

    private void validateCard(String card) {
        if (card == null || !card.matches("\\d{12,19}")) {
            throw new IllegalArgumentException("Card number must contain 12 to 19 digits.");
        }
    }

    private void validatePin(String pin) {
        if (pin == null || !pin.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN must contain exactly 4 digits.");
        }
    }

    public static final class AuthenticatedAccount {
        private final long id;
        private final String cardNumber;

        public AuthenticatedAccount(long id, String cardNumber) {
            this.id = id;
            this.cardNumber = cardNumber;
        }

        public long id() { return id; }
        public String cardNumber() { return cardNumber; }
    }

    public static final class AuthResult {
        private final boolean success;
        private final String message;
        private final AuthenticatedAccount account;

        private AuthResult(boolean success, String message, AuthenticatedAccount account) {
            this.success = success;
            this.message = message;
            this.account = account;
        }

        public static AuthResult success(AuthenticatedAccount account) {
            return new AuthResult(true, "Login successful", account);
        }

        public static AuthResult failure(String message) {
            return new AuthResult(false, message, null);
        }

        public boolean success() { return success; }
        public String message() { return message; }
        public AuthenticatedAccount account() { return account; }
    }
}
