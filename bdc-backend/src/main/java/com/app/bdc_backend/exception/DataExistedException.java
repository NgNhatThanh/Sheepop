package com.app.bdc_backend.exception;

public class DataExistedException extends RuntimeException {

    public DataExistedException(String message) {
        super(message);
    }

}
