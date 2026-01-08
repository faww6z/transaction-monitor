package com.fawwaz.transactionmonitor.service;

import com.fawwaz.transactionmonitor.domain.Alert;
import com.fawwaz.transactionmonitor.domain.RiskScore;
import com.fawwaz.transactionmonitor.domain.Transaction;
import com.fawwaz.transactionmonitor.domain.enums.AlertStatus;
import com.fawwaz.transactionmonitor.repository.AlertRepository;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public Alert createOpenAlert(Transaction txn, RiskScore riskScore) {
        Alert alert = new Alert();
        alert.setTransaction(txn);
        alert.setRiskScore(riskScore);
        alert.setStatus(AlertStatus.OPEN);
        alert.setInvestigatorNote(null);
        return alertRepository.save(alert);
    }
}
