package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.Transaction;

import java.math.BigDecimal;

public class BalanceMismatchRule implements RiskRule {

    private final BigDecimal tolerance;
    private final int points;

    public BalanceMismatchRule(double tolerance, int points) {
        this.tolerance = BigDecimal.valueOf(tolerance);
        this.points = points;
    }

    @Override
    public String code() { return "BALANCE_MISMATCH"; }

    @Override
    public int score(Transaction txn) {
        // Origin: expectedNewOrig = old - amount
        BigDecimal expectedNewOrig = txn.getOldBalanceOrig().subtract(txn.getAmount());
        boolean mismatchOrig = exceedsTolerance(txn.getNewBalanceOrig(), expectedNewOrig);

        // Destination: expectedNewDest = old + amount
        BigDecimal expectedNewDest = txn.getOldBalanceDest().add(txn.getAmount());
        boolean mismatchDest = exceedsTolerance(txn.getNewBalanceDest(), expectedNewDest);

        return (mismatchOrig || mismatchDest) ? points : 0;
    }

    /** True when |actual - expected| is strictly greater than the tolerance. */
    private boolean exceedsTolerance(BigDecimal actual, BigDecimal expected) {
        return actual.subtract(expected).abs().compareTo(tolerance) > 0;
    }
}
