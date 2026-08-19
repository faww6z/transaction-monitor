package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.Transaction;
import com.fawwaz.transactionmonitor.domain.enums.TransactionType;

import java.math.BigDecimal;

/**
 * Small helper for building {@link Transaction} instances in tests without
 * repeating six setter calls each time. Balances default to a self-consistent
 * state (so {@link BalanceMismatchRule} does NOT trigger unless a test opts in).
 *
 * <p>Callers pass plain numbers for readability; money values are converted to
 * {@link BigDecimal} via {@link BigDecimal#valueOf(double)} and all balance math
 * is done in {@code BigDecimal} to mirror production.
 */
final class TransactionFixtures {

    private TransactionFixtures() {}

    /** A consistent transaction: newOrig = old - amount, newDest = old + amount. */
    static Transaction consistent(TransactionType type, double amount,
                                  double oldBalanceOrig, double oldBalanceDest) {
        BigDecimal amt = BigDecimal.valueOf(amount);
        BigDecimal oldOrig = BigDecimal.valueOf(oldBalanceOrig);
        BigDecimal oldDest = BigDecimal.valueOf(oldBalanceDest);
        Transaction txn = new Transaction();
        txn.setType(type);
        txn.setAmount(amt);
        txn.setOldBalanceOrig(oldOrig);
        txn.setNewBalanceOrig(oldOrig.subtract(amt));
        txn.setOldBalanceDest(oldDest);
        txn.setNewBalanceDest(oldDest.add(amt));
        return txn;
    }

    /** Fully explicit builder for cases that deliberately break balance math. */
    static Transaction of(TransactionType type, double amount,
                          double oldBalanceOrig, double newBalanceOrig,
                          double oldBalanceDest, double newBalanceDest) {
        Transaction txn = new Transaction();
        txn.setType(type);
        txn.setAmount(BigDecimal.valueOf(amount));
        txn.setOldBalanceOrig(BigDecimal.valueOf(oldBalanceOrig));
        txn.setNewBalanceOrig(BigDecimal.valueOf(newBalanceOrig));
        txn.setOldBalanceDest(BigDecimal.valueOf(oldBalanceDest));
        txn.setNewBalanceDest(BigDecimal.valueOf(newBalanceDest));
        return txn;
    }
}
