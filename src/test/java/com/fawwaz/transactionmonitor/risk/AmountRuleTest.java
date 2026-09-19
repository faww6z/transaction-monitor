package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.Transaction;
import com.fawwaz.transactionmonitor.domain.enums.TransactionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AmountRuleTest {

    private final AmountRule rule = new AmountRule(10_000.0, 50);

    @Test
    void code_isStable() {
        assertEquals("AMOUNT_HIGH", rule.code());
    }

    @Test
    void scoresZero_belowThreshold() {
        Transaction txn = TransactionFixtures.consistent(TransactionType.PAYMENT, 9_999.99, 20_000, 0);
        assertEquals(0, rule.score(txn));
    }

    @Test
    void scoresPoints_atThreshold() {
        // threshold is inclusive (>=)
        Transaction txn = TransactionFixtures.consistent(TransactionType.PAYMENT, 10_000.0, 20_000, 0);
        assertEquals(50, rule.score(txn));
    }

    @Test
    void scoresPoints_aboveThreshold() {
        Transaction txn = TransactionFixtures.consistent(TransactionType.PAYMENT, 250_000, 500_000, 0);
        assertEquals(50, rule.score(txn));
    }
}
