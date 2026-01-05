package com.example.user_service.controller;

import com.example.user_service.entities.User;
import com.example.user_service.service.CustomAuthService;
import com.example.user_service.service.UserService;
import org.springframework.security.core.Authentication;
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
    public String register(@RequestBody Map<String, String> payload) {
        return authService.register(payload.get("email"), payload.get("password"), payload.get("firstName"), payload.get("lastName"));
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> payload) {
        return authService.login(payload.get("email"), payload.get("password"));
    }
}