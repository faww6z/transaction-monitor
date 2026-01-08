package com.fawwaz.transactionmonitor.domain;

import com.fawwaz.transactionmonitor.domain.enums.RiskLevel;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "risk_scores")
public class RiskScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "transaction_id", unique = true)
    private Transaction transaction;

    private int score; // 0–100

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(length = 1000)
    private String triggeredRules; // "AMOUNT_HIGH,TYPE_CASH_OUT"

    private Instant scoredAt = Instant.now();

    public RiskScore() {}

    public Long getId() { return id; }

    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public String getTriggeredRules() { return triggeredRules; }
    public void setTriggeredRules(String triggeredRules) { this.triggeredRules = triggeredRules; }

    public Instant getScoredAt() { return scoredAt; }
}
