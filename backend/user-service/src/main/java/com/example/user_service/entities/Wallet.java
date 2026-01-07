package com.example.user_service.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallet")
@Data
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double balance = 0.0;

    @Column(nullable = false)
    private Double totalDeposit = 0.0;

    @Column(nullable = false)
    private Double totalWithdrawal = 0.0;

    @Column(nullable = false)
    private Double pandingBalance = 0.0;

    @Column(nullable = false)
    private String currency = "DOLLARS";

    @Column(nullable = false)
    private Double totalInvested = 0.0; //When a buy order is fulfilled, increment the totalInvested field.

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionHistory> history = new ArrayList<>();
}