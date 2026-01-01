package com.example.user_service.controller;

import com.example.user_service.service.SupabaseAuthService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SupabaseAuthService authService;

    public AuthController(SupabaseAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> payload) {
        return authService.authenticate(
                payload.get("email"),
                payload.get("password")
        );
    }

    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> payload) {
        try{
            return authService.register(
                    payload.get("email"),
                    payload.get("password")
            );
        }catch (org.springframework.web.client.HttpClientErrorException e) {
            // Aceasta linie va printa in consola IntelliJ eroarea JSON detaliata de la Supabase
            System.out.println("DEBUG Supabase Error Body: " + e.getResponseBodyAsString());
            return "Eroare Register: " + e.getResponseBodyAsString();
        }

    }
}