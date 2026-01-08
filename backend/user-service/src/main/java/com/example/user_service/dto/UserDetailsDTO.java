package com.example.user_service.dto;

public record UserDetailsDTO(
        String firstName,
        String lastName,
        String phoneNumber
) {}