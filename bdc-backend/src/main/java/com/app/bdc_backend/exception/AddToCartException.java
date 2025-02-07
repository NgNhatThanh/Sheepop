package com.app.bdc_backend.exception;

public class AddToCartException extends RuntimeException {
    public AddToCartException(String message) {
        super(message);
    }
}
