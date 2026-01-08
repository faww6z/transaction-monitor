package com.fawwaz.transactionmonitor.web.api;

import com.fawwaz.transactionmonitor.domain.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateTransactionRequest {

    @NotNull
    private TransactionType type;

    @Min(0)
    private double amount;

    @Min(0)
    private double oldBalanceOrig;

    @Min(0)
    private double newBalanceOrig;

    @Min(0)
    private double oldBalanceDest;

    @Min(0)
    private double newBalanceDest;

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getOldBalanceOrig() { return oldBalanceOrig; }
    public void setOldBalanceOrig(double oldBalanceOrig) { this.oldBalanceOrig = oldBalanceOrig; }

    public double getNewBalanceOrig() { return newBalanceOrig; }
    public void setNewBalanceOrig(double newBalanceOrig) { this.newBalanceOrig = newBalanceOrig; }

    public double getOldBalanceDest() { return oldBalanceDest; }
    public void setOldBalanceDest(double oldBalanceDest) { this.oldBalanceDest = oldBalanceDest; }

    public double getNewBalanceDest() { return newBalanceDest; }
    public void setNewBalanceDest(double newBalanceDest) { this.newBalanceDest = newBalanceDest; }
}
