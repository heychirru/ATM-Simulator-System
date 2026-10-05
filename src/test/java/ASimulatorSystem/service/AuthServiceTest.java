package ASimulatorSystem.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class AuthServiceTest {
    private final AuthService authService = new AuthService();
    private final TransactionService transactionService = new TransactionService();

    @Test
    void testDemoAccountAuthentication() throws SQLException {
        AuthService.AuthResult result = authService.authenticate("1234567890123456", "1234");
        assertTrue(result.success());
        assertNotNull(result.account());
        assertEquals("1234567890123456", result.account().cardNumber());
    }

    @Test
    void testDemoAccountBalance() throws SQLException {
        BigDecimal balance = transactionService.balance(1L);
        assertTrue(balance.compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    void testAccountCreationAndDepositWithdraw() throws SQLException {
        String testCard = "9" + (System.currentTimeMillis() % 1000000000000000L);
        while (testCard.length() < 16) testCard += "0";
        long accountId = authService.createAccount(testCard, "4321", new BigDecimal("5000.00"));
        assertTrue(accountId > 0);

        AuthService.AuthResult login = authService.authenticate(testCard, "4321");
        assertTrue(login.success());

        BigDecimal newBalance = transactionService.deposit(accountId, new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("6000.00"), newBalance);

        BigDecimal afterWithdraw = transactionService.withdraw(accountId, new BigDecimal("2000.00"));
        assertEquals(new BigDecimal("4000.00"), afterWithdraw);
    }
}
