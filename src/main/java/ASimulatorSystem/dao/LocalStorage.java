package ASimulatorSystem.dao;

import ASimulatorSystem.security.PinHasher;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Embedded, thread-safe local data store for the ATM Simulator System.
 * Persists accounts and transaction history to a local file without requiring MySQL.
 */
public final class LocalStorage {
    private static final Path STORE_PATH = Path.of(".atm_storage.dat");
    private static final Object LOCK = new Object();
    private static final LocalStorage INSTANCE = new LocalStorage();

    private final Map<Long, AccountDao.AccountRecord> accounts = new ConcurrentHashMap<>();
    private final Map<Long, List<TransactionDao.TransactionRecord>> transactions = new ConcurrentHashMap<>();
    private final AtomicLong accountIdSequence = new AtomicLong(0);

    private LocalStorage() {
        load();
    }

    public static LocalStorage getInstance() {
        return INSTANCE;
    }

    public AccountDao.AccountRecord findAccountByCardNumber(String cardNumber) {
        if (cardNumber == null) return null;
        for (AccountDao.AccountRecord record : accounts.values()) {
            if (cardNumber.equals(record.cardNumber())) {
                return record;
            }
        }
        return null;
    }

    public AccountDao.AccountRecord findAccountById(long accountId) {
        return accounts.get(accountId);
    }

    public long createAccount(String cardNumber, String pinHash, BigDecimal initialBalance) {
        synchronized (LOCK) {
            long newId = accountIdSequence.incrementAndGet();
            BigDecimal balance = initialBalance == null ? BigDecimal.ZERO : initialBalance;
            AccountDao.AccountRecord record = new AccountDao.AccountRecord(
                    newId, cardNumber, pinHash, balance, "ACTIVE", 0
            );
            accounts.put(newId, record);
            transactions.put(newId, new ArrayList<>());

            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                TransactionDao.TransactionRecord tx = new TransactionDao.TransactionRecord(
                        UUID.randomUUID().toString(),
                        "DEPOSIT",
                        balance,
                        balance,
                        Timestamp.from(Instant.now())
                );
                transactions.get(newId).add(tx);
            }
            save();
            return newId;
        }
    }

    public void updateFailedAttempts(long accountId, int attempts, String status) {
        synchronized (LOCK) {
            AccountDao.AccountRecord existing = accounts.get(accountId);
            if (existing != null) {
                AccountDao.AccountRecord updated = new AccountDao.AccountRecord(
                        existing.id(),
                        existing.cardNumber(),
                        existing.pinHash(),
                        existing.balance(),
                        status,
                        attempts
                );
                accounts.put(accountId, updated);
                save();
            }
        }
    }

    public void resetFailedAttempts(long accountId) {
        synchronized (LOCK) {
            AccountDao.AccountRecord existing = accounts.get(accountId);
            if (existing != null) {
                AccountDao.AccountRecord updated = new AccountDao.AccountRecord(
                        existing.id(),
                        existing.cardNumber(),
                        existing.pinHash(),
                        existing.balance(),
                        existing.status(),
                        0
                );
                accounts.put(accountId, updated);
                save();
            }
        }
    }

    public void updatePinHash(long accountId, String newPinHash) {
        synchronized (LOCK) {
            AccountDao.AccountRecord existing = accounts.get(accountId);
            if (existing != null) {
                AccountDao.AccountRecord updated = new AccountDao.AccountRecord(
                        existing.id(),
                        existing.cardNumber(),
                        newPinHash,
                        existing.balance(),
                        existing.status(),
                        0
                );
                accounts.put(accountId, updated);
                save();
            }
        }
    }

    public BigDecimal getBalance(long accountId) {
        AccountDao.AccountRecord account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found.");
        }
        return account.balance();
    }

    public BigDecimal deposit(long accountId, BigDecimal amount) {
        synchronized (LOCK) {
            AccountDao.AccountRecord account = accounts.get(accountId);
            if (account == null || !"ACTIVE".equals(account.status())) {
                throw new IllegalArgumentException("Active account not found.");
            }
            BigDecimal newBalance = account.balance().add(amount);
            AccountDao.AccountRecord updated = new AccountDao.AccountRecord(
                    account.id(),
                    account.cardNumber(),
                    account.pinHash(),
                    newBalance,
                    account.status(),
                    account.failedPinAttempts()
            );
            accounts.put(accountId, updated);

            TransactionDao.TransactionRecord tx = new TransactionDao.TransactionRecord(
                    UUID.randomUUID().toString(),
                    "DEPOSIT",
                    amount,
                    newBalance,
                    Timestamp.from(Instant.now())
            );
            transactions.computeIfAbsent(accountId, k -> new ArrayList<>()).add(tx);
            save();
            return newBalance;
        }
    }

    public BigDecimal withdraw(long accountId, BigDecimal amount) {
        synchronized (LOCK) {
            AccountDao.AccountRecord account = accounts.get(accountId);
            if (account == null || !"ACTIVE".equals(account.status())) {
                throw new IllegalArgumentException("Active account not found.");
            }

            // Check 24-hour limit
            long oneDayAgo = System.currentTimeMillis() - (24L * 60 * 60 * 1000);
            List<TransactionDao.TransactionRecord> txList = transactions.getOrDefault(accountId, Collections.emptyList());
            BigDecimal usedToday = BigDecimal.ZERO;
            for (TransactionDao.TransactionRecord tx : txList) {
                if ("WITHDRAWAL".equals(tx.type()) && tx.createdAt().getTime() >= oneDayAgo) {
                    usedToday = usedToday.add(tx.amount());
                }
            }

            if (usedToday.add(amount).compareTo(new BigDecimal("20000.00")) > 0) {
                throw new IllegalArgumentException("Daily withdrawal limit is Rs. 20,000.");
            }

            if (account.balance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient balance.");
            }

            BigDecimal newBalance = account.balance().subtract(amount);
            AccountDao.AccountRecord updated = new AccountDao.AccountRecord(
                    account.id(),
                    account.cardNumber(),
                    account.pinHash(),
                    newBalance,
                    account.status(),
                    account.failedPinAttempts()
            );
            accounts.put(accountId, updated);

            TransactionDao.TransactionRecord tx = new TransactionDao.TransactionRecord(
                    UUID.randomUUID().toString(),
                    "WITHDRAWAL",
                    amount,
                    newBalance,
                    Timestamp.from(Instant.now())
            );
            transactions.computeIfAbsent(accountId, k -> new ArrayList<>()).add(tx);
            save();
            return newBalance;
        }
    }

    public List<TransactionDao.TransactionRecord> getRecentTransactions(long accountId, int limit) {
        List<TransactionDao.TransactionRecord> list = transactions.getOrDefault(accountId, Collections.emptyList());
        List<TransactionDao.TransactionRecord> copy = new ArrayList<>(list);
        copy.sort((a, b) -> b.createdAt().compareTo(a.createdAt()));
        int safeLimit = Math.max(1, Math.min(limit, copy.size()));
        return new ArrayList<>(copy.subList(0, safeLimit));
    }

    public void flush() {
        save();
    }

    private void seedDefaultAccount() {
        long id = accountIdSequence.incrementAndGet();
        String demoCard = "1234567890123456";
        String demoPinHash = PinHasher.hash("1234");
        BigDecimal initialBalance = new BigDecimal("10000.00");

        AccountDao.AccountRecord demo = new AccountDao.AccountRecord(
                id, demoCard, demoPinHash, initialBalance, "ACTIVE", 0
        );
        accounts.put(id, demo);
        List<TransactionDao.TransactionRecord> txList = new ArrayList<>();
        txList.add(new TransactionDao.TransactionRecord(
                UUID.randomUUID().toString(),
                "DEPOSIT",
                initialBalance,
                initialBalance,
                Timestamp.from(Instant.now())
        ));
        transactions.put(id, txList);
        save();
    }

    private void save() {
        synchronized (LOCK) {
            Path tempPath = Path.of(STORE_PATH.toString() + ".tmp");
            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(tempPath)))) {
                out.writeInt(1); // Format version
                out.writeLong(accountIdSequence.get());

                // Save Accounts
                out.writeInt(accounts.size());
                for (AccountDao.AccountRecord acc : accounts.values()) {
                    out.writeLong(acc.id());
                    out.writeUTF(acc.cardNumber());
                    out.writeUTF(acc.pinHash());
                    out.writeUTF(acc.balance().toPlainString());
                    out.writeUTF(acc.status());
                    out.writeInt(acc.failedPinAttempts());
                }

                // Save Transactions
                out.writeInt(transactions.size());
                for (Map.Entry<Long, List<TransactionDao.TransactionRecord>> entry : transactions.entrySet()) {
                    out.writeLong(entry.getKey());
                    List<TransactionDao.TransactionRecord> txList = entry.getValue();
                    out.writeInt(txList.size());
                    for (TransactionDao.TransactionRecord tx : txList) {
                        out.writeUTF(tx.reference());
                        out.writeUTF(tx.type());
                        out.writeUTF(tx.amount().toPlainString());
                        out.writeUTF(tx.balanceAfter().toPlainString());
                        out.writeLong(tx.createdAt().getTime());
                    }
                }
                out.flush();
                Files.move(tempPath, STORE_PATH, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (Exception e) {
                // If temporary file move fails or writing fails
                try {
                    Files.deleteIfExists(tempPath);
                } catch (IOException ignored) {}
            }
        }
    }

    private void load() {
        synchronized (LOCK) {
            if (!Files.exists(STORE_PATH)) {
                seedDefaultAccount();
                return;
            }

            try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(STORE_PATH)))) {
                int version = in.readInt();
                if (version != 1) {
                    seedDefaultAccount();
                    return;
                }
                long seq = in.readLong();
                accountIdSequence.set(seq);

                // Load Accounts
                int accountCount = in.readInt();
                accounts.clear();
                for (int i = 0; i < accountCount; i++) {
                    long id = in.readLong();
                    String card = in.readUTF();
                    String pinHash = in.readUTF();
                    BigDecimal balance = new BigDecimal(in.readUTF());
                    String status = in.readUTF();
                    int failedAttempts = in.readInt();
                    accounts.put(id, new AccountDao.AccountRecord(id, card, pinHash, balance, status, failedAttempts));
                }

                // Load Transactions
                int txAccountCount = in.readInt();
                transactions.clear();
                for (int i = 0; i < txAccountCount; i++) {
                    long accId = in.readLong();
                    int txCount = in.readInt();
                    List<TransactionDao.TransactionRecord> list = new ArrayList<>();
                    for (int j = 0; j < txCount; j++) {
                        String ref = in.readUTF();
                        String type = in.readUTF();
                        BigDecimal amount = new BigDecimal(in.readUTF());
                        BigDecimal balAfter = new BigDecimal(in.readUTF());
                        long timestamp = in.readLong();
                        list.add(new TransactionDao.TransactionRecord(ref, type, amount, balAfter, new Timestamp(timestamp)));
                    }
                    transactions.put(accId, list);
                }

                if (accounts.isEmpty()) {
                    seedDefaultAccount();
                }
            } catch (Exception e) {
                accounts.clear();
                transactions.clear();
                accountIdSequence.set(0);
                seedDefaultAccount();
            }
        }
    }
}
