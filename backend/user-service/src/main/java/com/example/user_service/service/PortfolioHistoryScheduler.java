package com.example.user_service.service;

import com.example.user_service.entities.PortfolioHistory;
import com.example.user_service.entities.User;
import com.example.user_service.entities.UserDetails;
import com.example.user_service.repository.PortfolioHistoryRepository;
import com.example.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PortfolioHistoryScheduler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioHistoryRepository portfolioHistoryRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void saveDailyPortfolioSnapshots() {
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            UserDetails details = user.getUserDetails();
            if (details != null && details.getPortfolioItems() != null) {

                double totalValue = details.getPortfolioItems().stream()
                        .mapToDouble(item -> item.getPortfolioValue())
                        .sum();

                PortfolioHistory snapshot = new PortfolioHistory();
                snapshot.setTotalValue(totalValue);
                snapshot.setSnapshotDate(LocalDateTime.now());
                snapshot.setUserDetails(details);

                portfolioHistoryRepository.save(snapshot);
            }
        }
        System.out.println("Snapshot saved for all users: " + LocalDateTime.now());
    }
}
