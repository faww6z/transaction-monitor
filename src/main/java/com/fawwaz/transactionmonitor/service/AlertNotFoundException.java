package com.fawwaz.transactionmonitor.service;

public class AlertNotFoundException extends RuntimeException {
    public AlertNotFoundException(Long id) {
        super("Alert not found: " + id);
    }
}
