package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.Transaction;

public class AmountRule implements RiskRule {

    private final double threshold;
    private final int points;

    public AmountRule(double threshold, int points) {
        this.threshold = threshold;
        this.points = points;
    }

    @Override
    public String code() { return "AMOUNT_HIGH"; }

    @Override
    public int score(Transaction txn) {
        return txn.getAmount() >= threshold ? points : 0;
    }
}
