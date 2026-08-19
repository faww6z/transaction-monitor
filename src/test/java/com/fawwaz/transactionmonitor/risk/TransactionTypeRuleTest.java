package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.Transaction;
import com.fawwaz.transactionmonitor.domain.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionTypeRuleTest {

    private final TransactionTypeRule rule = new TransactionTypeRule(
            Set.of(TransactionType.TRANSFER, TransactionType.CASH_OUT), 30);

    @Test
    void code_isStable() {
        assertEquals("TYPE_RISKY", rule.code());
    }

    @ParameterizedTest
    @EnumSource(value = TransactionType.class, names = {"TRANSFER", "CASH_OUT"})
    void scoresPoints_forRiskyTypes(TransactionType type) {
        Transaction txn = TransactionFixtures.consistent(type, 100, 1_000, 0);
        assertEquals(30, rule.score(txn));
    }

    @ParameterizedTest
    @EnumSource(value = TransactionType.class, names = {"PAYMENT", "DEBIT", "CASH_IN"})
    void scoresZero_forNonRiskyTypes(TransactionType type) {
        Transaction txn = TransactionFixtures.consistent(type, 100, 1_000, 0);
        assertEquals(0, rule.score(txn));
    }
}
