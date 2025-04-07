package com.app.bdc_backend.exception;

public class ServerException extends RuntimeException {
    public ServerException(String message) {
        super(message);
    }
}
