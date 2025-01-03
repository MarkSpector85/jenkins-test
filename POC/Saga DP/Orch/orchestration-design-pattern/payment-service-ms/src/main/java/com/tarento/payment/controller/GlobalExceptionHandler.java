package com.tarento.payment.controller;

import com.tarento.payment.exception.PaymentProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<String> handlePaymentProcessingException(PaymentProcessingException ex) {
        return new ResponseEntity<>("Error: " + ex.getMessage() + " [Error Code: " + ex.getErrorCode() + "]", HttpStatus.BAD_REQUEST);
    }
}
