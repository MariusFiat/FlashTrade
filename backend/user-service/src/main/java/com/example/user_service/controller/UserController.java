package com.example.user_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.dto.PortfolioPerformanceDTO;
import com.example.user_service.dto.PortfolioSummaryDTO;
import com.example.user_service.dto.TransactionHistoryDTO;
import com.example.user_service.dto.UserProfileDTO;
import com.example.user_service.dto.WalletDTO;
import com.example.user_service.messaging.dto.MessageResponse;
import com.example.user_service.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/user_info")
@Tag(name = "User Management", description = "User profile, wallet, and portfolio management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list_user_details")
    @Operation(summary = "Get user profile", description = "Retrieve the authenticated user's profile information")
    @ApiResponse(responseCode = "200", description = "User profile retrieved successfully")
    public ResponseEntity<UserProfileDTO> listUserDetails(Authentication authentication) {
        UserProfileDTO userProfile = userService.listUserDetails(authentication);
        return ResponseEntity.ok(userProfile);
    }

    @PostMapping("/edit_user_details")
    @Operation(summary = "Update user profile", description = "Update the authenticated user's profile information")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    public ResponseEntity<MessageResponse> editUserDetails(
            @RequestBody UserProfileDTO profileDTO,
            Authentication authentication) {
        userService.editUserDatails(profileDTO, authentication);
        return ResponseEntity.ok(new MessageResponse("Profile updated successfully"));
    }

    @DeleteMapping("/delete_me")
    @Operation(summary = "Delete own account", description = "Delete the authenticated user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
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
    @Operation(summary = "Delete user by email (Admin only)", description = "Delete a user account by email address. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
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
    @Operation(summary = "Get wallet information", description = "Retrieve the authenticated user's wallet details")
    @ApiResponse(responseCode = "200", description = "Wallet information retrieved successfully")
    public ResponseEntity<WalletDTO> getWalletInfo(Authentication authentication) {
        return ResponseEntity.ok(userService.getWalletInfo(authentication));
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit funds", description = "Deposit funds into the authenticated user's wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit successful"),
            @ApiResponse(responseCode = "400", description = "Invalid amount")
    })
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
    @Operation(summary = "Withdraw funds", description = "Withdraw funds from the authenticated user's wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal successful"),
            @ApiResponse(responseCode = "400", description = "Invalid amount"),
            @ApiResponse(responseCode = "412", description = "Insufficient funds")
    })
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
    @Operation(summary = "Get transaction history", description = "Retrieve the authenticated user's transaction history")
    @ApiResponse(responseCode = "200", description = "Transaction history retrieved successfully")
    public ResponseEntity<List<TransactionHistoryDTO>> getTransactions(Authentication authentication) {
        try {
            List<TransactionHistoryDTO> transactions = userService.getTransactions(authentication);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get_portfolio")
    @Operation(summary = "Get portfolio summary", description = "Retrieve the authenticated user's portfolio summary with holdings")
    @ApiResponse(responseCode = "200", description = "Portfolio retrieved successfully")
    public ResponseEntity<PortfolioSummaryDTO> getPortfolio(Authentication authentication) {
        try {
            PortfolioSummaryDTO portfolio = userService.getPortfolio(authentication);
            return ResponseEntity.ok(portfolio);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get_portfolio_performance")
    public ResponseEntity<PortfolioPerformanceDTO> getPortfolioPerformance(
            @RequestParam(defaultValue = "1w") String range,
            Authentication authentication) {
        try {
            PortfolioPerformanceDTO performance = userService.getPortfolioPerformance(range, authentication);
            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
