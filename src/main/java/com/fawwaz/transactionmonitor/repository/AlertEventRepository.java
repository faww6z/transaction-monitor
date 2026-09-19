package com.fawwaz.transactionmonitor.repository;

import com.fawwaz.transactionmonitor.domain.AlertEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertEventRepository extends JpaRepository<AlertEvent, Long> {
    List<AlertEvent> findByAlertIdOrderByCreatedAtDesc(Long alertId);
}
