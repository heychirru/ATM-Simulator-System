package ASimulatorSystem.backend.service;

import ASimulatorSystem.backend.entity.Account;
import ASimulatorSystem.backend.entity.BankTransaction;
import ASimulatorSystem.backend.repository.AccountRepository;
import ASimulatorSystem.backend.repository.TransactionRepository;
import ASimulatorSystem.security.PinHasher;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtmService {
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final BigDecimal MAX_WITHDRAWAL = new BigDecimal("10000.00");
    private static final BigDecimal DAILY_WITHDRAWAL_LIMIT = new BigDecimal("20000.00");
    private final AccountRepository accounts;
    private final TransactionRepository transactions;

    public AtmService(AccountRepository accounts, TransactionRepository transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
    }

    @Transactional
    public Account authenticate(String cardNumber, String pin) {
        Account account = accounts.findByCardNumberForUpdate(cardNumber).orElseThrow(() -> new IllegalArgumentException("Invalid card number or PIN."));
        if (account.getStatus() != Account.Status.ACTIVE) throw new IllegalStateException("Account is " + account.getStatus().name().toLowerCase() + ".");
        if (!PinHasher.matches(pin, account.getPinHash())) {
            int attempts = account.getFailedPinAttempts() + 1;
            account.setFailedPinAttempts(attempts);
            if (attempts >= MAX_FAILED_ATTEMPTS) account.setStatus(Account.Status.BLOCKED);
            accounts.save(account);
            throw new IllegalArgumentException(attempts >= MAX_FAILED_ATTEMPTS ? "Too many failed attempts. Account blocked." : "Invalid card number or PIN. Attempts left: " + (MAX_FAILED_ATTEMPTS - attempts));
        }
        account.setFailedPinAttempts(0);
        return accounts.save(account);
    }

    @Transactional
    public Account createAccount(String cardNumber, String pin, BigDecimal initialDeposit) {
        if (accounts.findByCardNumber(cardNumber).isPresent()) throw new IllegalArgumentException("Card number already exists.");
        Account account = new Account();
        account.setCardNumber(cardNumber);
        account.setPinHash(PinHasher.hash(pin));
        account.setBalance(initialDeposit);
        account = accounts.save(account);
        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) record(account, BankTransaction.Type.DEPOSIT, initialDeposit);
        return account;
    }

    @Transactional
    public BigDecimal deposit(long accountId, BigDecimal amount) {
        validateAmount(amount);
        Account account = activeAccountForUpdate(accountId);
        BigDecimal next = account.getBalance().add(amount);
        account.setBalance(next);
        accounts.save(account);
        record(account, BankTransaction.Type.DEPOSIT, amount);
        return next;
    }

    @Transactional
    public BigDecimal withdraw(long accountId, BigDecimal amount) {
        validateAmount(amount);
        if (amount.compareTo(MAX_WITHDRAWAL) > 0) throw new IllegalArgumentException("Maximum withdrawal is Rs. 10,000 per transaction.");
        Account account = activeAccountForUpdate(accountId);
        BigDecimal used = transactions.totalByTypeSince(accountId, BankTransaction.Type.WITHDRAWAL, Instant.now().minus(24, ChronoUnit.HOURS));
        if (used.add(amount).compareTo(DAILY_WITHDRAWAL_LIMIT) > 0) throw new IllegalArgumentException("Daily withdrawal limit is Rs. 20,000.");
        if (account.getBalance().compareTo(amount) < 0) throw new IllegalArgumentException("Insufficient balance.");
        BigDecimal next = account.getBalance().subtract(amount);
        account.setBalance(next);
        accounts.save(account);
        record(account, BankTransaction.Type.WITHDRAWAL, amount);
        return next;
    }

    @Transactional(readOnly = true)
    public BigDecimal balance(long accountId) {
        return accounts.findById(accountId).map(Account::getBalance).orElseThrow(() -> new IllegalArgumentException("Account not found."));
    }

    @Transactional(readOnly = true)
    public List<BankTransaction> recent(long accountId) {
        if (!accounts.existsById(accountId)) throw new IllegalArgumentException("Account not found.");
        return transactions.findTop10ByAccount_IdOrderByCreatedAtDescIdDesc(accountId);
    }

    @Transactional
    public void changePin(long accountId, String currentPin, String newPin) {
        Account account = activeAccountForUpdate(accountId);
        if (!PinHasher.matches(currentPin, account.getPinHash())) throw new IllegalArgumentException("Current PIN is incorrect.");
        account.setPinHash(PinHasher.hash(newPin));
        account.setFailedPinAttempts(0);
        accounts.save(account);
    }

    private Account activeAccountForUpdate(long accountId) {
        Account account = accounts.findByIdForUpdate(accountId).orElseThrow(() -> new IllegalArgumentException("Account not found."));
        if (account.getStatus() != Account.Status.ACTIVE) throw new IllegalStateException("Account is not active.");
        return account;
    }

    private void record(Account account, BankTransaction.Type type, BigDecimal amount) {
        BankTransaction transaction = new BankTransaction();
        transaction.setAccount(account);
        transaction.setReference(UUID.randomUUID().toString());
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(account.getBalance());
        transactions.save(transaction);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
        if (amount.scale() > 2) throw new IllegalArgumentException("Amount can have at most 2 decimal places.");
    }
}
