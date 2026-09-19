package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.Transaction;
import com.fawwaz.transactionmonitor.domain.enums.TransactionType;

import java.math.BigDecimal;

/**
 * Small helper for building {@link Transaction} instances in tests without
 * repeating six setter calls each time.
 *
 * <p>Callers pass plain numbers for readability; money values are converted to
 * {@link BigDecimal} via {@link BigDecimal#valueOf(double)} to mirror production.
 *
 * <p>Note that {@link #consistent} deliberately does not call into
 * {@link BalanceMismatchRule} to work out its balances. Sharing that logic would
 * make the fixture agree with the rule by construction, which is exactly how the
 * {@code CASH_IN} direction bug stayed invisible to a green suite. Tests that pin
 * down direction use {@link #of} with literal numbers instead.
 */
final class TransactionFixtures {

    private TransactionFixtures() {}

    /**
     * A transaction whose balances reconcile against its amount.
     *
     * <p>For {@link TransactionType#CASH_IN} the originating account is credited
     * and the counterparty is left untracked, since deposited cash comes from a
     * merchant. For every other type the origin is debited and the destination
     * credited, so {@code oldBalanceDest} is used as given.
     */
    static Transaction consistent(TransactionType type, double amount,
                                  double oldBalanceOrig, double oldBalanceDest) {
        BigDecimal amt = BigDecimal.valueOf(amount);
        BigDecimal oldOrig = BigDecimal.valueOf(oldBalanceOrig);
        BigDecimal oldDest = BigDecimal.valueOf(oldBalanceDest);

        if (type == TransactionType.CASH_IN) {
            return build(type, amt, oldOrig, oldOrig.add(amt), BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return build(type, amt, oldOrig, oldOrig.subtract(amt), oldDest, oldDest.add(amt));
    }

    /** Fully explicit builder for cases that pin down exact balances. */
    static Transaction of(TransactionType type, double amount,
                          double oldBalanceOrig, double newBalanceOrig,
                          double oldBalanceDest, double newBalanceDest) {
        return build(type,
                BigDecimal.valueOf(amount),
                BigDecimal.valueOf(oldBalanceOrig), BigDecimal.valueOf(newBalanceOrig),
                BigDecimal.valueOf(oldBalanceDest), BigDecimal.valueOf(newBalanceDest));
    }

    private static Transaction build(TransactionType type, BigDecimal amount,
                                     BigDecimal oldOrig, BigDecimal newOrig,
                                     BigDecimal oldDest, BigDecimal newDest) {
        Transaction txn = new Transaction();
        txn.setType(type);
        txn.setAmount(amount);
        txn.setOldBalanceOrig(oldOrig);
        txn.setNewBalanceOrig(newOrig);
        txn.setOldBalanceDest(oldDest);
        txn.setNewBalanceDest(newDest);
        return txn;
    }
}
