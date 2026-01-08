package com.fawwaz.transactionmonitor.repository;

import com.fawwaz.transactionmonitor.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {}
