package com.fawwaz.transactionmonitor.repository;

import com.fawwaz.transactionmonitor.domain.RiskScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskScoreRepository extends JpaRepository<RiskScore, Long> {}
