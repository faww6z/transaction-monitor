package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.Transaction;

import java.math.BigDecimal;

public class AmountRule implements RiskRule {

    private final BigDecimal threshold;
    private final int points;

    public AmountRule(double threshold, int points) {
        this.threshold = BigDecimal.valueOf(threshold);
        this.points = points;
    }

    @Override
    public String code() { return "AMOUNT_HIGH"; }

    @Override
    public int score(Transaction txn) {
        // triggered when amount >= threshold (inclusive)
        return txn.getAmount().compareTo(threshold) >= 0 ? points : 0;
    }
}
