package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.Transaction;

import java.math.BigDecimal;

/**
 * Reconciles the balances either side of a transaction against its amount, and
 * scores points when the books do not add up.
 *
 * <p>Which direction money moves depends on the {@link
 * com.fawwaz.transactionmonitor.domain.enums.TransactionType}: a deposit credits
 * the originating account, while every other type debits it. Applying a single
 * direction to all types raises an alert on every well-formed {@code CASH_IN}.
 *
 * <p>Counterparties that the source data does not track — merchants, which are
 * recorded with both balances at zero — are skipped rather than reconciled, since
 * there is no balance to compare against. That trades a narrow false-negative
 * (a genuinely untracked destination that should have moved) for removing a large
 * false-positive surface across ordinary merchant payments.
 */
public class BalanceMismatchRule implements RiskRule {

    private final BigDecimal tolerance;
    private final int points;

    public BalanceMismatchRule(double tolerance, int points) {
        this.tolerance = BigDecimal.valueOf(tolerance);
        this.points = points;
    }

    @Override
    public String code() { return "BALANCE_MISMATCH"; }

    @Override
    public int score(Transaction txn) {
        boolean mismatchOrig = exceedsTolerance(txn.getNewBalanceOrig(), expectedNewOrig(txn));

        boolean mismatchDest = isDestinationTracked(txn)
                && exceedsTolerance(txn.getNewBalanceDest(), expectedNewDest(txn));

        return (mismatchOrig || mismatchDest) ? points : 0;
    }

    /** A deposit pays money into the originating account; everything else takes it out. */
    private BigDecimal expectedNewOrig(Transaction txn) {
        return switch (txn.getType()) {
            case CASH_IN -> txn.getOldBalanceOrig().add(txn.getAmount());
            case PAYMENT, TRANSFER, CASH_OUT, DEBIT -> txn.getOldBalanceOrig().subtract(txn.getAmount());
        };
    }

    /** The counterparty mirrors the origin: it funds a deposit and receives everything else. */
    private BigDecimal expectedNewDest(Transaction txn) {
        return switch (txn.getType()) {
            case CASH_IN -> txn.getOldBalanceDest().subtract(txn.getAmount());
            case PAYMENT, TRANSFER, CASH_OUT, DEBIT -> txn.getOldBalanceDest().add(txn.getAmount());
        };
    }

    /**
     * Merchant counterparties carry no balance on either side of the transaction,
     * so there is nothing to reconcile. A destination that holds a balance before
     * or after the transaction is tracked and gets checked.
     */
    private boolean isDestinationTracked(Transaction txn) {
        return txn.getOldBalanceDest().signum() != 0 || txn.getNewBalanceDest().signum() != 0;
    }

    /** True when |actual - expected| is strictly greater than the tolerance. */
    private boolean exceedsTolerance(BigDecimal actual, BigDecimal expected) {
        return actual.subtract(expected).abs().compareTo(tolerance) > 0;
    }
}
