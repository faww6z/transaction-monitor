package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.Transaction;
import com.fawwaz.transactionmonitor.domain.enums.RiskLevel;
import com.fawwaz.transactionmonitor.domain.enums.TransactionType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the {@link RiskEngine} wired with the same rules and thresholds as
 * production ({@code RiskConfig}): AmountRule(10k,50), TypeRule(TRANSFER/CASH_OUT,30),
 * BalanceMismatchRule($1,40); MEDIUM &gt;= 40, HIGH &gt;= 70.
 */
class RiskEngineTest {

    private RiskEngine engine() {
        return new RiskEngine(
                List.of(
                        new AmountRule(10_000.0, 50),
                        new TransactionTypeRule(Set.of(TransactionType.TRANSFER, TransactionType.CASH_OUT), 30),
                        new BalanceMismatchRule(1.0, 40)
                ),
                40, 70);
    }

    @Test
    void lowRisk_whenNoRulesTrigger() {
        // PAYMENT (not risky), small amount, consistent balances
        Transaction txn = TransactionFixtures.consistent(TransactionType.PAYMENT, 100, 1_000, 500);

        RiskResult result = engine().evaluate(txn);

        assertEquals(0, result.score());
        assertEquals(RiskLevel.LOW, result.level());
        assertTrue(result.triggeredRules().isEmpty());
    }

    @Test
    void singleRule_belowMediumThreshold_staysLow() {
        // Only TYPE_RISKY (30) -> below MEDIUM (40)
        Transaction txn = TransactionFixtures.consistent(TransactionType.CASH_OUT, 100, 1_000, 500);

        RiskResult result = engine().evaluate(txn);

        assertEquals(30, result.score());
        assertEquals(RiskLevel.LOW, result.level());
        assertEquals(List.of("TYPE_RISKY"), result.triggeredRules());
    }

    @Test
    void mediumRisk_atExactThreshold() {
        // Only AMOUNT_HIGH (50) -> MEDIUM (>= 40, < 70)
        Transaction txn = TransactionFixtures.consistent(TransactionType.PAYMENT, 10_000, 50_000, 0);

        RiskResult result = engine().evaluate(txn);

        assertEquals(50, result.score());
        assertEquals(RiskLevel.MEDIUM, result.level());
        assertEquals(List.of("AMOUNT_HIGH"), result.triggeredRules());
    }

    @Test
    void highRisk_atExactThreshold() {
        // TYPE_RISKY (30) + BALANCE_MISMATCH (40) = 70 -> HIGH boundary.
        // amount 100 is below the amount threshold, so AMOUNT_HIGH does not fire.
        Transaction txn = TransactionFixtures.of(
                TransactionType.CASH_OUT, 100, 20_000, 3_000, 100, 200);

        RiskResult result = engine().evaluate(txn);

        assertEquals(70, result.score());
        assertEquals(RiskLevel.HIGH, result.level());
        assertEquals(List.of("TYPE_RISKY", "BALANCE_MISMATCH"), result.triggeredRules());
    }

    @Test
    void scoreIsCappedAt100_whenAllRulesTrigger() {
        // 50 + 30 + 40 = 120 -> capped to 100
        Transaction txn = TransactionFixtures.of(
                TransactionType.CASH_OUT, 15_000, 20_000, 3_000, 100, 200);

        RiskResult result = engine().evaluate(txn);

        assertEquals(100, result.score());
        assertEquals(RiskLevel.HIGH, result.level());
        assertEquals(List.of("AMOUNT_HIGH", "TYPE_RISKY", "BALANCE_MISMATCH"), result.triggeredRules());
    }

    @Test
    void triggeredRules_preserveRuleEvaluationOrder() {
        // AMOUNT_HIGH (50) + BALANCE_MISMATCH (40), skipping TYPE_RISKY (PAYMENT).
        Transaction txn = TransactionFixtures.of(
                TransactionType.PAYMENT, 12_000, 20_000, 3_000, 100, 200);

        RiskResult result = engine().evaluate(txn);

        assertEquals(90, result.score());
        assertEquals(RiskLevel.HIGH, result.level());
        assertEquals(List.of("AMOUNT_HIGH", "BALANCE_MISMATCH"), result.triggeredRules());
    }
}
