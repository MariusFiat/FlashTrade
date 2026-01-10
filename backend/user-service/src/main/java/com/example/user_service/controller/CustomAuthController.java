package com.example.user_service.controller;

import com.example.user_service.service.CustomAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class CustomAuthController {

    private final CustomAuthService authService;

    public CustomAuthController(CustomAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        try {
            authService.register(
                    payload.get("email"),
                    payload.get("password"),
                    payload.get("firstName"),
                    payload.get("lastName")
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Register successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        try {
            String token = authService.login(payload.get("email"), payload.get("password"));
            return ResponseEntity.ok(Map.of("access_token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}