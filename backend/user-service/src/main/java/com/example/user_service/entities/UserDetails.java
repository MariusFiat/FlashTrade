package com.example.user_service.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_details")
@Data
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = true)
    private String phoneNumber;
}