package com.fawwaz.transactionmonitor.web.mvc;

import com.fawwaz.transactionmonitor.domain.Alert;
import com.fawwaz.transactionmonitor.domain.enums.AlertStatus;
import com.fawwaz.transactionmonitor.repository.AlertRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/alerts")
public class AlertMvcController {

    private final AlertRepository alertRepository;

    public AlertMvcController(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @GetMapping
    public String list(@RequestParam(required = false) AlertStatus status, Model model) {
        List<Alert> alerts = (status == null)
                ? alertRepository.findAll()
                : alertRepository.findByStatusOrderByCreatedAtDesc(status);

        alerts.sort(Comparator.comparing(Alert::getCreatedAt).reversed());

        model.addAttribute("alerts", alerts);
        model.addAttribute("statuses", AlertStatus.values());
        model.addAttribute("selectedStatus", status);
        return "alerts";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + id));

        model.addAttribute("alert", alert);
        model.addAttribute("statuses", AlertStatus.values());
        return "alert-detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam AlertStatus status,
            @RequestParam(required = false) String investigatorNote
    ) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + id));

        alert.setStatus(status);
        alert.setInvestigatorNote(investigatorNote);
        alertRepository.save(alert);

        return "redirect:/alerts/" + id;
    }
}
