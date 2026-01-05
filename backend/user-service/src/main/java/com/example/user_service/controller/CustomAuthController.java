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
    private final UserService userService;

    public CustomAuthController(CustomAuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> payload) {
        return authService.register(payload.get("email"), payload.get("password"));
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> payload) {
        return authService.login(payload.get("email"), payload.get("password"));
    }

    @GetMapping("/user_details")
    public String infoUser(Authentication authentication) {
        //Extract the user from the auth obj
        User user = userService.getCurrentUser(authentication);
        Long userId = user.getId();
        String email = user.getEmail();

        return "You are logged in as " + email + " id: " + userId;
    }
}