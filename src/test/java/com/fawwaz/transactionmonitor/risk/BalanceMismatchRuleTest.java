package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.Transaction;
import com.fawwaz.transactionmonitor.domain.enums.TransactionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BalanceMismatchRuleTest {

    private final BalanceMismatchRule rule = new BalanceMismatchRule(1.0, 40);

    @Test
    void code_isStable() {
        assertEquals("BALANCE_MISMATCH", rule.code());
    }

    @Test
    void scoresZero_whenBalancesAreConsistent() {
        // orig: 20000 - 15000 = 5000 ; dest: 100 + 15000 = 15100
        Transaction txn = TransactionFixtures.of(
                TransactionType.CASH_OUT, 15_000, 20_000, 5_000, 100, 15_100);
        assertEquals(0, rule.score(txn));
    }

    @Test
    void scoresZero_whenWithinTolerance() {
        // orig off by exactly 1.0 (== tolerance, not > tolerance) -> not triggered
        Transaction txn = TransactionFixtures.of(
                TransactionType.CASH_OUT, 15_000, 20_000, 5_001, 100, 15_100);
        assertEquals(0, rule.score(txn));
    }

    @Test
    void scoresPoints_whenOriginBalanceMismatches() {
        // expected newOrig = 5000, actual 3000 -> mismatch of 2000
        Transaction txn = TransactionFixtures.of(
                TransactionType.CASH_OUT, 15_000, 20_000, 3_000, 100, 15_100);
        assertEquals(40, rule.score(txn));
    }

    @Test
    void scoresPoints_whenDestinationBalanceMismatches() {
        // orig consistent, dest expected 15100 but actual 9999 -> mismatch
        Transaction txn = TransactionFixtures.of(
                TransactionType.CASH_OUT, 15_000, 20_000, 5_000, 100, 9_999);
        assertEquals(40, rule.score(txn));
    }

    @Test
    void scoresPoints_justBeyondTolerance() {
        // orig off by 1.5 (> 1.0 tolerance) -> triggered
        Transaction txn = TransactionFixtures.of(
                TransactionType.TRANSFER, 15_000, 20_000, 5_001.5, 100, 15_100);
        assertEquals(40, rule.score(txn));
    }
}
