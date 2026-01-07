package com.example.user_service.repository;

import com.example.user_service.entities.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Optional<Portfolio> findByUserDetailsIdAndStock(Long userDetailsId, String stock);

    List<Portfolio> findAllByUserDetailsId(Long userDetailsId);

    List<Portfolio> findByStockIgnoreCase(String symbol);
}