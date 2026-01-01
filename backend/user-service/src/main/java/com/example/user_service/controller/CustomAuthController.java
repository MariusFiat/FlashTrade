package com.example.user_service.controller;

import com.example.user_service.service.CustomAuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class CustomAuthController {

    private final CustomAuthService authService;

    public CustomAuthController(CustomAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> payload) {
        return authService.register(payload.get("email"), payload.get("password"));
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> payload) {
        return authService.login(payload.get("email"), payload.get("password"));
    }
}