package ASimulatorSystem.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions", indexes = @Index(name = "idx_transactions_account_date", columnList = "account_id,created_at"))
public class BankTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "account_id", nullable = false) private Account account;
    @Column(nullable = false, unique = true, length = 36) private String reference;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Type type;
    @Column(nullable = false, precision = 15, scale = 2) private BigDecimal amount;
    @Column(name = "balance_after", nullable = false, precision = 15, scale = 2) private BigDecimal balanceAfter;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false) private Instant createdAt;

    public enum Type { DEPOSIT, WITHDRAWAL }
    public Long getId() { return id; }
    public Account getAccount() { return account; }
    public void setAccount(Account value) { account = value; }
    public String getReference() { return reference; }
    public void setReference(String value) { reference = value; }
    public Type getType() { return type; }
    public void setType(Type value) { type = value; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal value) { amount = value; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal value) { balanceAfter = value; }
    public Instant getCreatedAt() { return createdAt; }
}
