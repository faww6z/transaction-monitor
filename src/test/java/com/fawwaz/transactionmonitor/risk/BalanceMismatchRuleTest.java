package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.Transaction;
import com.fawwaz.transactionmonitor.domain.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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

    // --- direction of movement, per transaction type ---

    @ParameterizedTest
    @EnumSource(TransactionType.class)
    void scoresZero_forEveryTypeWhenBalancesReconcile(TransactionType type) {
        Transaction txn = TransactionFixtures.consistent(type, 100, 1_000, 500);
        assertEquals(0, rule.score(txn));
    }

    @Test
    void scoresZero_whenDepositCreditsTheOriginatingAccount() {
        // CASH_IN pays money IN: 100 + 50 = 150. The funding counterparty is
        // tracked here and gives up the cash: 500 - 50 = 450.
        Transaction txn = TransactionFixtures.of(
                TransactionType.CASH_IN, 50, 100, 150, 500, 450);
        assertEquals(0, rule.score(txn));
    }

    @Test
    void scoresPoints_whenDepositDebitsTheOriginatingAccount() {
        // Regression: this is the shape the rule used to treat as consistent for
        // every type. For a deposit it is genuinely wrong (100 - 50 instead of
        // 100 + 50) and must score.
        Transaction txn = TransactionFixtures.of(
                TransactionType.CASH_IN, 50, 100, 50, 0, 0);
        assertEquals(40, rule.score(txn));
    }

    // --- untracked (merchant) counterparties ---

    @Test
    void scoresZero_whenDestinationIsUntracked() {
        // Merchant destinations carry no balance on either side. Origin still
        // reconciles (100 - 20 = 80), so nothing should fire.
        Transaction txn = TransactionFixtures.of(
                TransactionType.PAYMENT, 20, 100, 80, 0, 0);
        assertEquals(0, rule.score(txn));
    }

    @Test
    void stillScoresOnOrigin_whenDestinationIsUntracked() {
        // Skipping an untracked destination must not blind the origin check:
        // expected newOrig = 80, actual 55.
        Transaction txn = TransactionFixtures.of(
                TransactionType.PAYMENT, 20, 100, 55, 0, 0);
        assertEquals(40, rule.score(txn));
    }

    @Test
    void scoresPoints_whenTrackedDestinationDoesNotMove() {
        // A destination holding a balance IS reconciled: 200 should have become
        // 700, but the money never arrived.
        Transaction txn = TransactionFixtures.of(
                TransactionType.TRANSFER, 500, 1_000, 500, 200, 200);
        assertEquals(40, rule.score(txn));
    }
}
