package com.example.user_service.controller;

import com.example.user_service.entities.User;
import com.example.user_service.service.CustomAuthService;
import com.example.user_service.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user_info")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user_details")
    public String infoUser(Authentication authentication) {
        //Extract the user from the auth obj
        User user = userService.getCurrentUser(authentication);
        Long userId = user.getId();
        String email = user.getEmail();

        return "You are logged in as " + email + " id: " + userId;
    }
}
