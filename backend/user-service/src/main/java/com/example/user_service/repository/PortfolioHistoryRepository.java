package com.example.user_service.repository;

import com.example.user_service.entities.PortfolioHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface PortfolioHistoryRepository extends JpaRepository<PortfolioHistory, Long> {

    List<PortfolioHistory> findByUserDetailsIdAndSnapshotDateBetweenOrderBySnapshotDateAsc(
            Long userDetailsId,
            LocalDateTime start,
            LocalDateTime end
    );
}