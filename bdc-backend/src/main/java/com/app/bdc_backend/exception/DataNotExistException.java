package com.app.bdc_backend.exception;

public class DataNotExistException extends RuntimeException {
    public DataNotExistException(String message) {
        super(message);
    }
}
