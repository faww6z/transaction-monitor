package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.Transaction;
import com.fawwaz.transactionmonitor.domain.enums.TransactionType;

import java.util.Set;

public class TransactionTypeRule implements RiskRule {

    private final Set<TransactionType> riskyTypes;
    private final int points;

    public TransactionTypeRule(Set<TransactionType> riskyTypes, int points) {
        this.riskyTypes = riskyTypes;
        this.points = points;
    }

    @Override
    public String code() { return "TYPE_RISKY"; }

    @Override
    public int score(Transaction txn) {
        return riskyTypes.contains(txn.getType()) ? points : 0;
    }
}
