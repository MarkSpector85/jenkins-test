package com.tarento.payment.exception;

public class PaymentProcessingException extends Exception {

    private String errorCode;

    public PaymentProcessingException(String message) {
        super(message);
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentProcessingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
