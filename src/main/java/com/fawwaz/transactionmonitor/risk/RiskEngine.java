package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.Transaction;
import com.fawwaz.transactionmonitor.domain.enums.RiskLevel;

import java.util.ArrayList;
import java.util.List;

public class RiskEngine {

    private final List<RiskRule> rules;
    private final int mediumThreshold;
    private final int highThreshold;

    public RiskEngine(List<RiskRule> rules, int mediumThreshold, int highThreshold) {
        this.rules = rules;
        this.mediumThreshold = mediumThreshold;
        this.highThreshold = highThreshold;
    }

    public RiskResult evaluate(Transaction txn) {
        int total = 0;
        List<String> triggered = new ArrayList<>();

        for (RiskRule rule : rules) {
            int pts = rule.score(txn);
            if (pts > 0) triggered.add(rule.code());
            total += pts;
        }

        int capped = Math.min(100, total);
        RiskLevel level =
                (capped >= highThreshold) ? RiskLevel.HIGH :
                        (capped >= mediumThreshold) ? RiskLevel.MEDIUM :
                                RiskLevel.LOW;

        return new RiskResult(capped, level, triggered);
    }
}
