package com.app.bdc_backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseEntity<?> fileSizeExceeded(MaxUploadSizeExceededException e) {
        return ResponseEntity.badRequest().body(Map.of(
            "message", "File size is too large"
        ));
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<?> illegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage()
        ));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> invalidInputException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        if(message == null) message = "Invalid input: " + ex.getBindingResult().getAllErrors().get(0).getObjectName();
        log.warn("Invalid request: {}", message);
        return ResponseEntity.badRequest().body(Map.of(
                "message", message
        ));
    }

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

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<?> runtimeExHandling(RuntimeException e){
        e.printStackTrace();
        log.warn("Runtime Exception: {}", e.getMessage());
        return ResponseEntity.internalServerError().body(Map.of("message", "Exception happened"));
    }

}
