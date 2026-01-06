package com.example.user_service.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_credentials")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Aici vom stoca hash-ul (parola criptată)

    @OneToOne(cascade = CascadeType.ALL) //Delete the user_details too if this user is deleted.
    @JoinColumn(name = "user_details_id", referencedColumnName = "id")
    private UserDetails userDetails;

    @Column(nullable = false)
    private String role;
}