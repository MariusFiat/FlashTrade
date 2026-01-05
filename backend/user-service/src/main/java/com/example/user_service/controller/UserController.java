package com.example.user_service.controller;

import com.example.user_service.entities.User;
import com.example.user_service.entities.UserDetails;
import com.example.user_service.repository.UserDetailsRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.CustomAuthService;
import com.example.user_service.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    @GetMapping("/user_details")
    public String infoUser(Authentication authentication) {
        //Extract the user from the auth obj
        User user = userService.getCurrentUser(authentication);
        Long userId = user.getId();
        String email = user.getEmail();

        return "You are logged in as " + email + " id: " + userId;
    }

    @GetMapping("/list_user_details")
    public Map<String, String> listUserDetails(Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
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

    @PostMapping("/edit_user_details")
    public void editUserDetails(@RequestBody Map<String, String> map, Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);

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
}
