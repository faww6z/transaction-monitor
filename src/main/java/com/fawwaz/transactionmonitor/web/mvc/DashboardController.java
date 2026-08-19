package com.fawwaz.transactionmonitor.web.mvc;

import com.fawwaz.transactionmonitor.domain.enums.AlertStatus;
import com.fawwaz.transactionmonitor.domain.enums.RiskLevel;
import com.fawwaz.transactionmonitor.repository.AlertRepository;
import com.fawwaz.transactionmonitor.repository.RiskScoreRepository;
import com.fawwaz.transactionmonitor.repository.TransactionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Landing page: a small operational overview of the monitoring system —
 * totals, the open-alert workload, and how risk is distributed.
 */
@Controller
public class DashboardController {

    private final TransactionRepository transactionRepository;
    private final RiskScoreRepository riskScoreRepository;
    private final AlertRepository alertRepository;

    public DashboardController(TransactionRepository transactionRepository,
                               RiskScoreRepository riskScoreRepository,
                               AlertRepository alertRepository) {
        this.transactionRepository = transactionRepository;
        this.riskScoreRepository = riskScoreRepository;
        this.alertRepository = alertRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        long totalTransactions = transactionRepository.count();
        long totalAlerts = alertRepository.count();
        long openAlerts = alertRepository.countByStatus(AlertStatus.OPEN);

        // Ordered maps so the template renders them in a stable, meaningful order.
        Map<AlertStatus, Long> byStatus = new LinkedHashMap<>();
        for (AlertStatus s : AlertStatus.values()) {
            byStatus.put(s, alertRepository.countByStatus(s));
        }

        Map<RiskLevel, Long> byRisk = new LinkedHashMap<>();
        for (RiskLevel r : RiskLevel.values()) {
            byRisk.put(r, riskScoreRepository.countByRiskLevel(r));
        }

        model.addAttribute("totalTransactions", totalTransactions);
        model.addAttribute("totalAlerts", totalAlerts);
        model.addAttribute("openAlerts", openAlerts);
        model.addAttribute("byStatus", byStatus);
        model.addAttribute("byRisk", byRisk);
        model.addAttribute("recentAlerts", alertRepository.findTop10ByOrderByCreatedAtDesc());
        return "dashboard";
    }
}
