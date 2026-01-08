package com.fawwaz.transactionmonitor.service;

import com.fawwaz.transactionmonitor.domain.RiskScore;
import com.fawwaz.transactionmonitor.domain.Transaction;
import com.fawwaz.transactionmonitor.domain.enums.RiskLevel;
import com.fawwaz.transactionmonitor.repository.RiskScoreRepository;
import com.fawwaz.transactionmonitor.repository.TransactionRepository;
import com.fawwaz.transactionmonitor.risk.RiskEngine;
import com.fawwaz.transactionmonitor.risk.RiskResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.StringJoiner;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final RiskScoreRepository riskScoreRepository;
    private final AlertService alertService;
    private final RiskEngine riskEngine;

    public TransactionService(
            TransactionRepository transactionRepository,
            RiskScoreRepository riskScoreRepository,
            AlertService alertService,
            RiskEngine riskEngine
    ) {
        this.transactionRepository = transactionRepository;
        this.riskScoreRepository = riskScoreRepository;
        this.alertService = alertService;
        this.riskEngine = riskEngine;
    }

    @Transactional
    public RiskScore ingest(Transaction txn) {
        // 1) Save transaction
        Transaction savedTxn = transactionRepository.save(txn);

        // 2) Evaluate risk
        RiskResult result = riskEngine.evaluate(savedTxn);

        // 3) Save RiskScore
        RiskScore riskScore = new RiskScore();
        riskScore.setTransaction(savedTxn);
        riskScore.setScore(result.score());
        riskScore.setRiskLevel(result.level());
        riskScore.setTriggeredRules(join(result.triggeredRules()));
        RiskScore savedScore = riskScoreRepository.save(riskScore);

        // 4) Create alert if MEDIUM/HIGH (you can change this to HIGH only)
        if (result.level() == RiskLevel.MEDIUM || result.level() == RiskLevel.HIGH) {
            alertService.createOpenAlert(savedTxn, savedScore);
        }

        return savedScore;
    }

    private String join(Iterable<String> rules) {
        StringJoiner sj = new StringJoiner(",");
        for (String r : rules) sj.add(r);
        return sj.toString();
    }
}
