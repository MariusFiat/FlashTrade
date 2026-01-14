package com.example.user_service.service;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user_service.entities.User;
import com.example.user_service.entities.UserDetails;
import com.example.user_service.entities.Wallet;
import com.example.user_service.model.enums.Currency;
import com.example.user_service.model.enums.UserRole;
import com.example.user_service.repository.UserDetailsRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.exception.InvalidCredentialsException;
import com.example.user_service.service.exception.UserNotFound;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class CustomAuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserDetailsRepository userDetailsRepository;

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    public CustomAuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserDetailsRepository userDetailsRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsRepository = userDetailsRepository;
    }

    @Transactional
    public void register(String email, String password, String firstName, String lastName) {
        if (validateUserExists(email)) {
            throw new RuntimeException("User already exists!");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(String.valueOf(UserRole.ROLE_USER));

        UserDetails userDetails = new UserDetails();
        userDetails.setFirstName(firstName);
        userDetails.setLastName(lastName);

        Wallet wallet = new Wallet();
        wallet.setCurrency(String.valueOf(Currency.Dollar));
        userDetails.setWallet(wallet);

        userDetailsRepository.save(userDetails);

        user.setUserDetails(userDetails);
        userRepository.save(user);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("Email or password is invalid! User not found!"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return generateJwtToken(email);
        } else {
            throw new InvalidCredentialsException("Email or password is invalid!");
        }
    }

    private String generateJwtToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(email)
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateUserExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}