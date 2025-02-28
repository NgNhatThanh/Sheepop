package com.app.bdc_backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RequestException.class)
    public ResponseEntity<?> runtimeExHandling(RequestException e){
        log.warn("Request Exception: {}", e.getMessage());
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(value = ServerException.class)
    public ResponseEntity<?> runtimeExHandling(ServerException e){
        log.warn("Server Exception: {}", e.getMessage());
        return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
    }

}
