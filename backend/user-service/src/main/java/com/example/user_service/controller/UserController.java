package com.example.user_service.controller;

import com.example.user_service.entities.User;
import com.example.user_service.entities.UserDetails;
import com.example.user_service.entities.Wallet;
import com.example.user_service.repository.UserDetailsRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.CustomAuthService;
import com.example.user_service.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public Map<String, String> listUserDetails(Authentication authentication) {
        return userService.listUserDetails(authentication);
    }

    @PostMapping("/edit_user_details")
    public void editUserDetails(@RequestBody Map<String, String> map, Authentication authentication) {
        userService.editUserDatails(map, authentication);
    }

    @DeleteMapping("/delete_me")
    public String deleteMe(Authentication authentication) {
        return userService.deleteMe(authentication);
    }

    @DeleteMapping("/delete_user")
    @PreAuthorize("hasRole('ROLE_ADMIN')") //Just an admin account can delete another users.
    public String deleteUser(@RequestParam String email) {
        return  userService.deleteUser(email);
    }

    @GetMapping("/get_wallet_info")
    public Map<String, String> getWalletInfo(Authentication authentication) {
        return userService.getWalletInfo(authentication);
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam double amount, Authentication authentication) {
        return userService.updateTotalDeposits(amount, authentication) + "\n" + userService.deposit(amount, authentication) + "\n";
    }

    @PostMapping("/withdrawal")
    public boolean withdrawal(@RequestParam double amount, Authentication authentication) {
        return userService.withdrawal(amount, authentication) && userService.updateTotalWithdrawals(amount, authentication);
    }

    @PostMapping("/reserve_funds")
    public boolean reserveFunds(@RequestParam double amount, Authentication authentication) {
        return userService.reserveFunds(amount, authentication);
    }

    @PostMapping("/release_reserved_funds")
    public boolean releaseReservedFunds(@RequestParam double amount, Authentication authentication) {
        return userService.releaseFunds(amount, authentication);
    }
}
