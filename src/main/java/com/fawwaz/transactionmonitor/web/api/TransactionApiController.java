package com.fawwaz.transactionmonitor.web.api;

import com.fawwaz.transactionmonitor.domain.RiskScore;
import com.fawwaz.transactionmonitor.domain.Transaction;
import com.fawwaz.transactionmonitor.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionApiController {

    private final TransactionService transactionService;

    public TransactionApiController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IngestTransactionResponse ingest(@Valid @RequestBody CreateTransactionRequest req) {
        Transaction txn = new Transaction();
        txn.setType(req.getType());
        txn.setAmount(req.getAmount());
        txn.setOldBalanceOrig(req.getOldBalanceOrig());
        txn.setNewBalanceOrig(req.getNewBalanceOrig());
        txn.setOldBalanceDest(req.getOldBalanceDest());
        txn.setNewBalanceDest(req.getNewBalanceDest());

        RiskScore score = transactionService.ingest(txn);

        return new IngestTransactionResponse(
                score.getTransaction().getId(),
                score.getId(),
                score.getScore(),
                score.getRiskLevel(),
                score.getTriggeredRules()
        );
    }
}
