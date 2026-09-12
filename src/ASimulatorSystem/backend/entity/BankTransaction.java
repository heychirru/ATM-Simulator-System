package ASimulatorSystem.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name="transactions", indexes=@Index(name="idx_transactions_account_date", columnList="account_id,created_at"))
public class BankTransaction {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="account_id", nullable=false) private Account account;
    @Column(nullable=false, unique=true, length=36) private String reference;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Type type;
    @Column(nullable=false, precision=15, scale=2) private BigDecimal amount;
    @Column(name="balance_after", nullable=false, precision=15, scale=2) private BigDecimal balanceAfter;
    @Column(name="created_at", nullable=false, insertable=false, updatable=false) private Instant createdAt;
    public enum Type { DEPOSIT, WITHDRAWAL }
    public Long getId(){return id;} public Account getAccount(){return account;} public void setAccount(Account a){account=a;} public String getReference(){return reference;} public void setReference(String r){reference=r;}
    public Type getType(){return type;} public void setType(Type t){type=t;} public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal a){amount=a;} public BigDecimal getBalanceAfter(){return balanceAfter;} public void setBalanceAfter(BigDecimal b){balanceAfter=b;} public Instant getCreatedAt(){return createdAt;}
}
