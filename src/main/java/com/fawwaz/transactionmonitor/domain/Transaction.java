package com.fawwaz.transactionmonitor.domain;

import com.fawwaz.transactionmonitor.domain.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal oldBalanceOrig;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal newBalanceOrig;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal oldBalanceDest;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal newBalanceDest;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Transaction() {}

    public Long getId() { return id; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getOldBalanceOrig() { return oldBalanceOrig; }
    public void setOldBalanceOrig(BigDecimal oldBalanceOrig) { this.oldBalanceOrig = oldBalanceOrig; }

    public BigDecimal getNewBalanceOrig() { return newBalanceOrig; }
    public void setNewBalanceOrig(BigDecimal newBalanceOrig) { this.newBalanceOrig = newBalanceOrig; }

    public BigDecimal getOldBalanceDest() { return oldBalanceDest; }
    public void setOldBalanceDest(BigDecimal oldBalanceDest) { this.oldBalanceDest = oldBalanceDest; }

    public BigDecimal getNewBalanceDest() { return newBalanceDest; }
    public void setNewBalanceDest(BigDecimal newBalanceDest) { this.newBalanceDest = newBalanceDest; }

    public Instant getCreatedAt() { return createdAt; }
}
