package com.fawwaz.transactionmonitor.web.api;

import com.fawwaz.transactionmonitor.domain.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class CreateTransactionRequest {

    @NotNull
    private TransactionType type;

    @NotNull
    @PositiveOrZero
    private BigDecimal amount;

    @NotNull
    @PositiveOrZero
    private BigDecimal oldBalanceOrig;

    @NotNull
    @PositiveOrZero
    private BigDecimal newBalanceOrig;

    @NotNull
    @PositiveOrZero
    private BigDecimal oldBalanceDest;

    @NotNull
    @PositiveOrZero
    private BigDecimal newBalanceDest;

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
}
