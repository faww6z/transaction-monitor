package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.Transaction;

public interface RiskRule {
    String code();                 // e.g., "AMOUNT_HIGH"
    int score(Transaction txn);    // points to add (0 if not triggered)
}
