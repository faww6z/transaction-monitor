package com.fawwaz.transactionmonitor.web.mvc;

import com.fawwaz.transactionmonitor.domain.Alert;
import com.fawwaz.transactionmonitor.domain.enums.AlertStatus;
import com.fawwaz.transactionmonitor.repository.AlertRepository;
import com.fawwaz.transactionmonitor.service.AlertService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/alerts")
public class AlertMvcController {

    private final AlertRepository alertRepository;
    private final AlertService alertService;

    public AlertMvcController(AlertRepository alertRepository, AlertService alertService) {
        this.alertRepository = alertRepository;
        this.alertService = alertService;
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
        model.addAttribute("alert", alertService.getAlert(id));
        model.addAttribute("events", alertService.history(id));
        model.addAttribute("statuses", AlertStatus.values());
        return "alert-detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam AlertStatus status,
            @RequestParam(required = false) String investigatorNote,
            Principal principal
    ) {
        alertService.updateStatus(id, status, investigatorNote, principal.getName());
        return "redirect:/alerts/" + id;
    }
}
