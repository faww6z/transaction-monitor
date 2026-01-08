package com.fawwaz.transactionmonitor.config;

import com.fawwaz.transactionmonitor.domain.enums.TransactionType;
import com.fawwaz.transactionmonitor.risk.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Configuration
public class RiskConfig {

    @Bean
    public RiskEngine riskEngine() {
        List<RiskRule> rules = List.of(
                new AmountRule(10_000.0, 50),
                new TransactionTypeRule(Set.of(TransactionType.TRANSFER, TransactionType.CASH_OUT), 30),
                new BalanceMismatchRule(1.0, 40) // tolerance = $1
        );

        return new RiskEngine(rules, 40, 70); // MEDIUM >= 40, HIGH >= 70
    }
}
