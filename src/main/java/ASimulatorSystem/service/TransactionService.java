package ASimulatorSystem.service;

import ASimulatorSystem.dao.TransactionDao;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/** Validates and delegates account money operations. */
public class TransactionService {
    private static final BigDecimal MAX_WITHDRAWAL = new BigDecimal("10000.00");
    private final TransactionDao transactionDao = new TransactionDao();

    public BigDecimal balance(long accountId) throws SQLException { return transactionDao.getBalance(accountId); }
    public BigDecimal deposit(long accountId, BigDecimal amount) throws SQLException { validateAmount(amount); return transactionDao.deposit(accountId, amount); }
    public BigDecimal withdraw(long accountId, BigDecimal amount) throws SQLException {
        validateAmount(amount);
        if (amount.compareTo(MAX_WITHDRAWAL) > 0) throw new IllegalArgumentException("Maximum withdrawal is Rs. 10,000.");
        return transactionDao.withdraw(accountId, amount);
    }
    public List<TransactionDao.TransactionRecord> recent(long accountId) throws SQLException { return transactionDao.recent(accountId, 10); }
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
        if (amount.scale() > 2) throw new IllegalArgumentException("Amount can have at most 2 decimal places.");
    }
}
