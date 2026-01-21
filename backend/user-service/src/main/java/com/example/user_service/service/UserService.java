package com.example.user_service.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.example.user_service.dto.*;
import com.example.user_service.entities.*;
import com.example.user_service.repository.PortfolioHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.user_service.model.enums.ActionType;
import com.example.user_service.model.enums.TransactionStatus;
import com.example.user_service.repository.TransactionHistoryRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.exception.UserNotFound;
import com.example.user_service.service.exception.UserUnauthorizedException;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    @Autowired
    private PortfolioHistoryRepository portfolioHistoryRepository;

    public UserService(
            TransactionHistoryRepository transactionHistoryRepository,
            UserRepository userRepository
    ) {
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.userRepository = userRepository;
    }

    public User getCurrentUser(Authentication authentication) {
        if (authentication == null) return null;

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("Email not found"));
    }

    @Transactional
    public void deposit(double amount, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Wallet wallet = user.getUserDetails().getWallet();

        wallet.setBalance(wallet.getBalance() + amount);
        wallet.setTotalDeposit(wallet.getTotalDeposit() + amount);

        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setWallet(wallet);
        transactionHistory.setAmount(amount);
        transactionHistory.setTimestamp(LocalDateTime.now());
        transactionHistory.setActionType(String.valueOf(ActionType.DEPOSIT));
        transactionHistory.setStatus(String.valueOf(TransactionStatus.COMPLETED));

        transactionHistoryRepository.save(transactionHistory);
        userRepository.save(user);
    }

    public WalletDTO getWalletInfo(Authentication authentication) {
        User user = getCurrentUser(authentication);
        Wallet wallet = user.getUserDetails().getWallet();

        return new WalletDTO(
                wallet.getBalance(),
                wallet.getTotalDeposit(),
                wallet.getTotalWithdrawal(),
                wallet.getPandingBalance(),
                wallet.getCurrency(),
                wallet.getTotalInvested()
        );
    }

    @Transactional
    public boolean deleteUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            userRepository.deleteById(user.get().getId());
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteMe(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserUnauthorizedException("User not authenticated. You have to log in first.");
        }

        String email = authentication.getName();
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new UserNotFound("Email not found");
        }
        userRepository.deleteByEmail(email);
    }

    public void editUserDetails(UserProfileDTO profileDTO, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        UserDetails details = currentUser.getUserDetails();
        if (details == null) {
            details = new UserDetails();
        }

        if(profileDTO.email() != null) {
            currentUser.setEmail(profileDTO.email());
        }

        if(profileDTO.firstName() != null) {
            details.setFirstName(profileDTO.firstName());
        }

        if(profileDTO.lastName() != null) {
            details.setLastName(profileDTO.lastName());
        }

        if(profileDTO.phoneNumber() != null) {
            details.setPhoneNumber(profileDTO.phoneNumber());
        }

        currentUser.setUserDetails(details); //Save the new details

        //Save the new data
        userRepository.save(currentUser);
    }

    public UserProfileDTO listUserDetails(Authentication authentication) {
        User user = getCurrentUser(authentication);
        UserDetails details = user.getUserDetails();

        return new UserProfileDTO(
                user.getEmail(),
                details.getFirstName(),
                details.getLastName(),
                details.getPhoneNumber()
        );
    }

    @Transactional
    public boolean withdrawal(double amount, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Wallet wallet = user.getUserDetails().getWallet();

        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setWallet(wallet);
        transactionHistory.setAmount(amount);
        transactionHistory.setTimestamp(LocalDateTime.now());
        transactionHistory.setActionType(String.valueOf(ActionType.WITHDRAW));

        if (wallet.getBalance() >= amount) {
            wallet.setBalance(wallet.getBalance() - amount);
            wallet.setTotalWithdrawal(wallet.getTotalWithdrawal() + amount);

            transactionHistory.setStatus(String.valueOf(TransactionStatus.COMPLETED));

            transactionHistoryRepository.save(transactionHistory);
            userRepository.save(user);
            return true;
        } else {
            transactionHistory.setStatus(String.valueOf(TransactionStatus.REJECTED));
            transactionHistoryRepository.save(transactionHistory);
            return false;
        }
    }

    public Integer reserveFunds(double amount, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if(user.getUserDetails().getWallet().getBalance() >= amount){
            user.getUserDetails().getWallet().setBalance(user.getUserDetails().getWallet().getBalance() - amount);
            user.getUserDetails().getWallet().setPandingBalance(user.getUserDetails().getWallet().getPandingBalance() + amount);
            userRepository.save(user);
            return 200;
        }
        else{
            return 412;
        }
    }

    public Integer releaseFunds(double amount, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if(user.getUserDetails().getWallet().getPandingBalance() >= amount){
            user.getUserDetails().getWallet().setPandingBalance(user.getUserDetails().getWallet().getPandingBalance() - amount);
            user.getUserDetails().getWallet().setBalance(user.getUserDetails().getWallet().getBalance() + amount);
            userRepository.save(user);
            return 200;
        }
        else{
            return 412;
        }
    }

    @Transactional
    public List<TransactionHistoryDTO> getTransactions(Authentication authentication) {
        User user = getCurrentUser(authentication);
        Long walletId = user.getUserDetails().getWallet().getId();

        List<TransactionHistory> transactions = transactionHistoryRepository.findTransactionsByWalletId(walletId);

        return transactions.stream()
                .map(t -> new TransactionHistoryDTO(
                        t.getId(),
                        t.getActionType(),
                        t.getAmount(),
                        t.getStatus(),
                        t.getTimestamp()
                ))
                .toList();
    }

    @Transactional
    public PortfolioSummaryDTO getPortfolio(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        UserDetails details = currentUser.getUserDetails();
        Wallet wallet = details.getWallet();

        List<PortfolioItemDTO> items = details.getPortfolioItems().stream()
                .map(p -> new PortfolioItemDTO(
                        p.getId(),
                        p.getStock(),
                        p.getShares(),
                        p.getPortfolioValue(),
                        p.getAllocation()
                ))
                .toList();

        double totalValue = items.stream()
                .mapToDouble(PortfolioItemDTO::portfolioValue)
                .sum();

        double totalInvested = wallet.getTotalInvested();
        double totalReturn = totalValue - totalInvested;

        return new PortfolioSummaryDTO(
                items,
                totalInvested,
                totalValue,
                totalReturn
        );
    }

    public PortfolioPerformanceDTO getPortfolioPerformance(String range, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Long userDetailsId = user.getUserDetails().getId();

        // 1. Stabilim data de start pentru query-ul brut
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = switch (range.toLowerCase()) {
            case "1w" -> now.minusWeeks(1);
            case "1m" -> now.minusMonths(1);
            case "3m" -> now.minusMonths(3);
            case "6m" -> now.minusMonths(6);
            case "1y" -> now.minusYears(1);
            case "all" -> now.minusYears(5);
            default -> now.minusWeeks(1);
        };

        List<PortfolioHistory> allHistory = portfolioHistoryRepository
                .findByUserDetailsIdAndSnapshotDateBetweenOrderBySnapshotDateAsc(userDetailsId, start, now);

        List<PortfolioPerformanceDTO.HistoryPoint> filteredPoints = filterPoints(allHistory, range.toLowerCase());

        Double currentValue = allHistory.isEmpty() ? 0.0 : allHistory.get(allHistory.size() - 1).getTotalValue();

        return new PortfolioPerformanceDTO(filteredPoints, currentValue);
    }

    private List<PortfolioPerformanceDTO.HistoryPoint> filterPoints(List<PortfolioHistory> rawData, String range) {
        if (rawData.isEmpty()) return List.of();

        return switch (range) {
            case "1w" -> rawData.stream()
                    .filter(h -> h.getSnapshotDate().getDayOfWeek().getValue() <= 5)
                    .map(h -> new PortfolioPerformanceDTO.HistoryPoint(h.getTotalValue(), h.getSnapshotDate()))
                    .limit(5)
                    .toList();

            case "1m" -> {
                yield rawData.stream()
                        .collect(Collectors.groupingBy(h ->
                                        h.getSnapshotDate().getYear() + "-" + (h.getSnapshotDate().get(ChronoField.ALIGNED_WEEK_OF_YEAR)),
                                TreeMap::new,
                                Collectors.maxBy(Comparator.comparing(PortfolioHistory::getSnapshotDate))
                        ))
                        .values().stream()
                        .flatMap(Optional::stream)
                        .map(h -> new PortfolioPerformanceDTO.HistoryPoint(h.getTotalValue(), h.getSnapshotDate()))
                        .toList();
            }

            case "3m", "6m", "1y" -> {
                yield rawData.stream()
                        .collect(Collectors.groupingBy(h ->
                                        h.getSnapshotDate().getYear() + "-" + h.getSnapshotDate().getMonthValue(),
                                TreeMap::new,
                                Collectors.maxBy(Comparator.comparing(PortfolioHistory::getSnapshotDate))
                        ))
                        .values().stream()
                        .flatMap(Optional::stream)
                        .map(h -> new PortfolioPerformanceDTO.HistoryPoint(h.getTotalValue(), h.getSnapshotDate()))
                        .toList();
            }

            default -> rawData.stream()
                    .map(h -> new PortfolioPerformanceDTO.HistoryPoint(h.getTotalValue(), h.getSnapshotDate()))
                    .toList();
        };
    }
}