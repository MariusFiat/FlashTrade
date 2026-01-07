package com.example.user_service.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_history")
@Data
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String actionType; // DEPOSIT, WITHDRAW

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String status; // COMPLETED, REJECTED

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    @JsonIgnore
    private Wallet wallet;
}