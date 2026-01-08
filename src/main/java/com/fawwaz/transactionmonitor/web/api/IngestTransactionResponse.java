package com.fawwaz.transactionmonitor.web.api;

import com.fawwaz.transactionmonitor.domain.enums.RiskLevel;

public class IngestTransactionResponse {

    private Long transactionId;
    private Long riskScoreId;
    private int score;
    private RiskLevel riskLevel;
    private String triggeredRules;

    public IngestTransactionResponse() {}

    public IngestTransactionResponse(Long transactionId, Long riskScoreId, int score, RiskLevel riskLevel, String triggeredRules) {
        this.transactionId = transactionId;
        this.riskScoreId = riskScoreId;
        this.score = score;
        this.riskLevel = riskLevel;
        this.triggeredRules = triggeredRules;
    }

    public Long getTransactionId() { return transactionId; }
    public Long getRiskScoreId() { return riskScoreId; }
    public int getScore() { return score; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public String getTriggeredRules() { return triggeredRules; }
}
