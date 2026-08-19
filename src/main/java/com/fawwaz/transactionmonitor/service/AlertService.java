package com.fawwaz.transactionmonitor.service;

import com.fawwaz.transactionmonitor.domain.Alert;
import com.fawwaz.transactionmonitor.domain.AlertEvent;
import com.fawwaz.transactionmonitor.domain.RiskScore;
import com.fawwaz.transactionmonitor.domain.Transaction;
import com.fawwaz.transactionmonitor.domain.enums.AlertStatus;
import com.fawwaz.transactionmonitor.repository.AlertEventRepository;
import com.fawwaz.transactionmonitor.repository.AlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertEventRepository alertEventRepository;

    public AlertService(AlertRepository alertRepository, AlertEventRepository alertEventRepository) {
        this.alertRepository = alertRepository;
        this.alertEventRepository = alertEventRepository;
    }

    @Transactional
    public Alert createOpenAlert(Transaction txn, RiskScore riskScore) {
        Alert alert = new Alert();
        alert.setTransaction(txn);
        alert.setRiskScore(riskScore);
        alert.setStatus(AlertStatus.OPEN);
        alert.setInvestigatorNote(null);
        Alert saved = alertRepository.save(alert);

        // First audit entry: the alert coming into existence (from nothing -> OPEN).
        alertEventRepository.save(
                new AlertEvent(saved, null, AlertStatus.OPEN, "Alert opened automatically", "system"));
        return saved;
    }

    @Transactional(readOnly = true)
    public Alert getAlert(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new AlertNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<AlertEvent> history(Long alertId) {
        return alertEventRepository.findByAlertIdOrderByCreatedAtDesc(alertId);
    }

    /**
     * Apply an analyst decision to an alert and record it in the audit trail.
     *
     * @param actor the authenticated username making the change
     */
    @Transactional
    public Alert updateStatus(Long id, AlertStatus newStatus, String note, String actor) {
        Alert alert = getAlert(id);
        AlertStatus previous = alert.getStatus();

        alert.setStatus(newStatus);
        alert.setInvestigatorNote(note);
        alertRepository.save(alert);

        alertEventRepository.save(new AlertEvent(alert, previous, newStatus, note, actor));
        return alert;
    }
}
