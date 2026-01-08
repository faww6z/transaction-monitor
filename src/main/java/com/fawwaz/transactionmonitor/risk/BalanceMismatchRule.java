package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.Transaction;

public class BalanceMismatchRule implements RiskRule {

    private final double tolerance;
    private final int points;

    public BalanceMismatchRule(double tolerance, int points) {
        this.tolerance = tolerance;
        this.points = points;
    }

    @Override
    public String code() { return "BALANCE_MISMATCH"; }

    @Override
    public int score(Transaction txn) {
        double expectedNewOrig = txn.getOldBalanceOrig() - txn.getAmount();
        boolean mismatchOrig = Math.abs(txn.getNewBalanceOrig() - expectedNewOrig) > tolerance;

        // Destination: expectedNewDest = old + amount
        double expectedNewDest = txn.getOldBalanceDest() + txn.getAmount();
        boolean mismatchDest = Math.abs(txn.getNewBalanceDest() - expectedNewDest) > tolerance;

        return (mismatchOrig || mismatchDest) ? points : 0;
    }
}
