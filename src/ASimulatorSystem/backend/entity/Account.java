package ASimulatorSystem.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name="accounts")
public class Account {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="card_number", nullable=false, unique=true, length=19) private String cardNumber;
    @Column(name="pin_hash", nullable=false, length=255) private String pinHash;
    @Column(nullable=false, precision=15, scale=2) private BigDecimal balance=BigDecimal.ZERO;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Status status=Status.ACTIVE;
    @Column(name="failed_pin_attempts", nullable=false) private int failedPinAttempts;
    @Column(name="created_at", nullable=false, insertable=false, updatable=false) private Instant createdAt;
    @Column(name="updated_at", nullable=false, insertable=false) private Instant updatedAt;
    public enum Status { ACTIVE, BLOCKED, CLOSED }
    public Long getId(){return id;} public String getCardNumber(){return cardNumber;} public void setCardNumber(String v){cardNumber=v;}
    public String getPinHash(){return pinHash;} public void setPinHash(String v){pinHash=v;} public BigDecimal getBalance(){return balance;} public void setBalance(BigDecimal v){balance=v;}
    public Status getStatus(){return status;} public void setStatus(Status v){status=v;} public int getFailedPinAttempts(){return failedPinAttempts;} public void setFailedPinAttempts(int v){failedPinAttempts=v;}
}
