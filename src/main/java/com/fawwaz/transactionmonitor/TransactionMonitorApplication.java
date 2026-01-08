package com.fawwaz.transactionmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.fawwaz.transactionmonitor")
public class TransactionMonitorApplication {
	public static void main(String[] args) {
		SpringApplication.run(TransactionMonitorApplication.class, args);
	}
}

