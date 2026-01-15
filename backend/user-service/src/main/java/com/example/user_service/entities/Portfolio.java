package com.example.user_service.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "portofolio")
@Data
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String stock;

    @Column(nullable = false)
    private Double shares;

    @Column(nullable = false)
    private Double portfolioValue;

    @Column(nullable = true)
    private Double allocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_details_id")
    private UserDetails userDetails;
}
