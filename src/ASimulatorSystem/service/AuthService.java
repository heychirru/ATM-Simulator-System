package ASimulatorSystem.service;

import ASimulatorSystem.dao.AccountDao;
import ASimulatorSystem.security.PinHasher;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

/** Authentication and account creation business rules. */
public class AuthService {
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private final AccountDao accountDao = new AccountDao();

    public AuthResult authenticate(String cardNumber, String pin) throws SQLException {
        AccountDao.AccountRecord account = accountDao.findByCardNumber(cardNumber);
        if (account == null) return AuthResult.failure("Invalid card number or PIN.");
        if (!"ACTIVE".equals(account.status())) return AuthResult.failure("Account is " + account.status().toLowerCase() + ".");
        if (!PinHasher.matches(pin, account.pinHash())) {
            int attempts = account.failedPinAttempts() + 1;
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                accountDao.updateFailedAttempts(account.id(), attempts, "BLOCKED");
                return AuthResult.failure("Too many failed attempts. Account blocked.");
            }
            accountDao.updateFailedAttempts(account.id(), attempts, "ACTIVE");
            return AuthResult.failure("Invalid card number or PIN. Attempts left: " + (MAX_FAILED_ATTEMPTS - attempts));
        }
        accountDao.resetFailedAttempts(account.id());
        return AuthResult.success(new AuthenticatedAccount(account.id(), account.cardNumber()));
    }

    public long createAccount(String cardNumber, String pin, BigDecimal initialDeposit) throws SQLException {
        validateCard(cardNumber);
        validatePin(pin);
        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Initial deposit cannot be negative.");
        if (accountDao.findByCardNumber(cardNumber) != null) throw new IllegalArgumentException("Card number already exists.");
        long id = accountDao.createAccount(cardNumber, PinHasher.hash(pin), initialDeposit);
        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) accountDao.recordInitialDeposit(id, UUID.randomUUID().toString(), initialDeposit);
        return id;
    }

    public void changePin(long accountId, String currentPin, String newPin) throws SQLException {
        validatePin(newPin);
        AccountDao.AccountRecord account = accountDao.findById(accountId);
        if (account == null || !"ACTIVE".equals(account.status()) || !PinHasher.matches(currentPin, account.pinHash())) throw new IllegalArgumentException("Current PIN is incorrect.");
        accountDao.updatePinHash(accountId, PinHasher.hash(newPin));
    }

    private void validateCard(String card) {
        if (card == null || !card.matches("\\d{12,19}")) throw new IllegalArgumentException("Card number must contain 12 to 19 digits.");
    }
    private void validatePin(String pin) {
        if (pin == null || !pin.matches("\\d{4}")) throw new IllegalArgumentException("PIN must contain exactly 4 digits.");
    }

    public record AuthenticatedAccount(long id, String cardNumber) { }
    public record AuthResult(boolean success, String message, AuthenticatedAccount account) {
        static AuthResult success(AuthenticatedAccount account) { return new AuthResult(true, "Login successful", account); }
        static AuthResult failure(String message) { return new AuthResult(false, message, null); }
    }
}
