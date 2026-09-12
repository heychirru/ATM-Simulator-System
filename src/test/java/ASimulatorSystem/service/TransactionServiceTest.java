package ASimulatorSystem.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class TransactionServiceTest {
    private final TransactionService service = new TransactionService();

    @Test void rejectsZeroDeposit() {
        assertThrows(IllegalArgumentException.class, () -> service.deposit(1L, BigDecimal.ZERO));
    }

    @Test void rejectsNegativeWithdrawal() {
        assertThrows(IllegalArgumentException.class, () -> service.withdraw(1L, new BigDecimal("-1.00")));
    }

    @Test void rejectsWithdrawalAboveLimit() {
        assertThrows(IllegalArgumentException.class, () -> service.withdraw(1L, new BigDecimal("10000.01")));
    }
}
