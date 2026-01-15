package com.example.user_service.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserUnauthorizedException extends RuntimeException {
    public UserUnauthorizedException(String message) {
        super(message);
    }
}