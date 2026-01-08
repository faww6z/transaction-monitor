package com.fawwaz.transactionmonitor.risk;

import com.fawwaz.transactionmonitor.domain.enums.RiskLevel;

import java.util.List;

public record RiskResult(int score, RiskLevel level, List<String> triggeredRules) {}
