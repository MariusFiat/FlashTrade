package com.example.user_service.service;

import com.example.user_service.entities.TransactionHistory;
import com.example.user_service.entities.User;
import com.example.user_service.entities.UserDetails;
import com.example.user_service.entities.Wallet;
import com.example.user_service.repository.TransactionHistoryRepository;
import com.example.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    public UserService(TransactionHistoryRepository transactionHistoryRepository) {
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    public User getCurrentUser(Authentication authentication) {
        if (authentication == null) return null;

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String deposit(double amount, Authentication authentication) {
        User user = getCurrentUser(authentication);
        user.getUserDetails().getWallet().setBalance(user.getUserDetails().getWallet().getBalance() + amount);
        userRepository.save(user);
        return "Deposit action fulfilled!";
    }

    public String updateTotalDeposits(double amount, Authentication authentication) {
        User user = getCurrentUser(authentication);
        user.getUserDetails().getWallet().setTotalDeposit(user.getUserDetails().getWallet().getBalance() + amount);
        userRepository.save(user);
        return "UpdateTtoalDeposits action fulfilled!";
    }

    public Map<String, String> getWalletInfo(Authentication authentication) {
        User user = getCurrentUser(authentication);
        Map<String, String> map = new HashMap<>();

        String availableBalance = user.getUserDetails().getWallet().getBalance().toString();
        map.put("availableBalance", availableBalance);

        String totalDeposits = user.getUserDetails().getWallet().getTotalDeposit().toString();
        map.put("totalDeposits", totalDeposits);

        String totalWithdrawals = user.getUserDetails().getWallet().getTotalWithdrawal().toString();
        map.put("totalWithdrawals", totalWithdrawals);

        String pandingBalance = user.getUserDetails().getWallet().getPandingBalance().toString();
        map.put("pandingBalance", pandingBalance);
        return map;
    }

    public String deleteUser(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()) {
            userRepository.deleteById(user.get().getId());
            return "The user has been deleted.";
        }
        else{
            return "The user was not found.";
        }
    }

    public String deleteMe(Authentication authentication) {
        if(authentication == null){
            return "You have to be logged in to delete your account.";
        }

        String email = authentication.getName();
        userRepository.deleteByEmail(email);
        return "Your account has been deleted.";
    }

    public void editUserDatails(Map<String, String> map, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        UserDetails details = currentUser.getUserDetails();
        if (details == null) {
            details = new UserDetails();
        }

        if(map.get("email") != null) {
            currentUser.setEmail(map.get("email"));
        }

        if(map.get("firstName") != null) {
            details.setFirstName(map.get("firstName"));
        }

        if(map.get("lastName") != null) {
            details.setLastName(map.get("lastName"));
        }

        if(map.get("phoneNumber") != null) {
            details.setPhoneNumber(map.get("phoneNumber"));
        }

        currentUser.setUserDetails(details); //Save the new details

        //Save the new data
        userRepository.save(currentUser);
    }

    public Map<String, String> listUserDetails(Authentication authentication) {
        User user = getCurrentUser(authentication);
        Long userId = user.getId();
        String email = user.getEmail();
        String firstName = user.getUserDetails().getFirstName();
        String lastName = user.getUserDetails().getLastName();
        String phoneNumber = user.getUserDetails().getPhoneNumber();

        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("phoneNumber", phoneNumber);
        return map;
    }

    public boolean withdrawal(double amount, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if(user.getUserDetails().getWallet().getBalance() >= amount){
            user.getUserDetails().getWallet().setBalance(user.getUserDetails().getWallet().getBalance() - amount);
            userRepository.save(user);
            return true;
        }
        else{
            return false;
        }
    }

    public boolean updateTotalWithdrawals(double amount, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if(user.getUserDetails().getWallet().getBalance() >= amount){
            user.getUserDetails().getWallet().setTotalWithdrawal(user.getUserDetails().getWallet().getTotalWithdrawal() + amount);
            userRepository.save(user);
            return true;
        }
        else{
            return false;
        }
    }

    public boolean reserveFunds(double amount, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if(user.getUserDetails().getWallet().getBalance() >= amount){
            user.getUserDetails().getWallet().setBalance(user.getUserDetails().getWallet().getBalance() - amount);
            user.getUserDetails().getWallet().setPandingBalance(user.getUserDetails().getWallet().getPandingBalance() + amount);
            userRepository.save(user);
            return true;
        }
        else{
            return false;
        }
    }

    public boolean releaseFunds(double amount, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if(user.getUserDetails().getWallet().getPandingBalance() >= amount){
            user.getUserDetails().getWallet().setPandingBalance(user.getUserDetails().getWallet().getPandingBalance() - amount);
            user.getUserDetails().getWallet().setBalance(user.getUserDetails().getWallet().getBalance() + amount);
            userRepository.save(user);
            return true;
        }
        else{
            return false;
        }
    }

    public List<TransactionHistory> getTransactions(Authentication authentication) {
        User user = getCurrentUser(authentication);
        Map<String, String> map = new HashMap<>();

        List<TransactionHistory> transactions = transactionHistoryRepository.findTransactionsByWalletId(user.getUserDetails().getWallet().getId());

        return transactions;
    }
}