package com.fawwaz.transactionmonitor.web;

import com.fawwaz.transactionmonitor.service.AlertNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Turns known exceptions into clean HTTP responses instead of a raw 500 page.
 * A missing alert becomes a 404. For MVC (browser) requests, the servlet
 * container renders the standard error view for that status code.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlertNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(AlertNotFoundException ex) {
        return Map.of("error", "Not Found", "message", ex.getMessage());
    }
}
