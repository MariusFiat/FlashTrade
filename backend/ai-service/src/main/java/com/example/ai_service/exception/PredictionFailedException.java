package com.example.ai_service.exception;

public class PredictionFailedException extends RuntimeException {
    public PredictionFailedException(String message) {
        super(message);
    }
    
    public PredictionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}