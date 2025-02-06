package com.app.bdc_backend.exception;

public class QuantityExceededException extends RuntimeException {
    public QuantityExceededException(String message) {
        super(message);
    }
}
