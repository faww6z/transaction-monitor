package com.fawwaz.transactionmonitor.domain;

import com.fawwaz.transactionmonitor.domain.enums.AlertStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "transaction_id", unique = true)
    private Transaction transaction;

    @OneToOne(optional = false)
    @JoinColumn(name = "risk_score_id", unique = true)
    private RiskScore riskScore;

    @Enumerated(EnumType.STRING)
    private AlertStatus status = AlertStatus.OPEN;

    @Column(length = 2000)
    private String investigatorNote;

    private Instant createdAt = Instant.now();

    public Alert() {}

    public Long getId() { return id; }

    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }

    public RiskScore getRiskScore() { return riskScore; }
    public void setRiskScore(RiskScore riskScore) { this.riskScore = riskScore; }

    public AlertStatus getStatus() { return status; }
    public void setStatus(AlertStatus status) { this.status = status; }

    public String getInvestigatorNote() { return investigatorNote; }
    public void setInvestigatorNote(String investigatorNote) { this.investigatorNote = investigatorNote; }

    public Instant getCreatedAt() { return createdAt; }
}
