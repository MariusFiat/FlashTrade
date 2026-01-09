package com.example.user_service.dto;

public record UserProfileDTO(
        String email,
        String firstName,
        String lastName,
        String phoneNumber
) {}