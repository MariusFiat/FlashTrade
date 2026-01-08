package com.example.user_service.controller;

import com.example.user_service.dto.*;
import com.example.user_service.entities.*;
import com.example.user_service.repository.UserDetailsRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user_info")
public class UserController {
    private final UserService userService;
    private final UserDetailsRepository userDetailsRepository;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserDetailsRepository userDetailsRepository,  UserRepository userRepository) {
        this.userService = userService;
        this.userDetailsRepository = userDetailsRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/list_user_details")
    public ResponseEntity<UserProfileDTO> listUserDetails(Authentication authentication) {
        UserProfileDTO userProfile = userService.listUserDetails(authentication);
        return ResponseEntity.ok(userProfile);
    }

    @PostMapping("/edit_user_details")
    public ResponseEntity<MessageResponse> editUserDetails(
            @RequestBody UserProfileDTO profileDTO,
            Authentication authentication) {
        userService.editUserDatails(profileDTO, authentication);
        return ResponseEntity.ok(new MessageResponse("Profile updated successfully"));
    }

    @DeleteMapping("/delete_me")
    public ResponseEntity<MessageResponse> deleteMe(Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("You must be logged in to perform this action."));
            }

            userService.deleteMe(authentication);
            return ResponseEntity.ok(new MessageResponse("Your account has been deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("An error occurred while deleting the account."));
        }
    }

    @DeleteMapping("/delete_user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> deleteUser(@RequestParam String email) {
        try {
            boolean deleted = userService.deleteUser(email);

            if (deleted) {
                return ResponseEntity.ok(new MessageResponse("User with email " + email + " was deleted."));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("User not found with email: " + email));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error occurred while deleting user."));
        }
    }

    @GetMapping("/get_wallet_info")
    public ResponseEntity<WalletDTO> getWalletInfo(Authentication authentication) {
        return ResponseEntity.ok(userService.getWalletInfo(authentication));
    }

    @PostMapping("/deposit")
    public ResponseEntity<MessageResponse> deposit(@RequestParam double amount, Authentication authentication) {
        try {
            if (amount <= 0) {
                return ResponseEntity.badRequest().body(new MessageResponse("Amount must be greater than 0"));
            }

            userService.deposit(amount, authentication);
            return ResponseEntity.ok(new MessageResponse("Deposit of " + amount + " successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error processing deposit"));
        }
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<MessageResponse> withdrawal(@RequestParam double amount, Authentication authentication) {
        try {
            if (amount <= 0) {
                return ResponseEntity.badRequest().body(new MessageResponse("Amount must be greater than 0"));
            }

            boolean success = userService.withdrawal(amount, authentication);

            if (success) {
                return ResponseEntity.ok(new MessageResponse("Withdrawal of " + amount + " successful"));
            } else {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(new MessageResponse("Insufficient funds for this withdrawal"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("An error occurred during withdrawal processing"));
        }
    }

    @GetMapping("/get_transactions")
    public ResponseEntity<List<TransactionHistoryDTO>> getTransactions(Authentication authentication) {
        try {
            List<TransactionHistoryDTO> transactions = userService.getTransactions(authentication);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get_portfolio")
    public ResponseEntity<PortfolioSummaryDTO> getPortfolio(Authentication authentication) {
        try {
            PortfolioSummaryDTO portfolio = userService.getPortfolio(authentication);
            return ResponseEntity.ok(portfolio);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
