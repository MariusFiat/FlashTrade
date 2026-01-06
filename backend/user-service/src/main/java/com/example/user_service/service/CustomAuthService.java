package com.example.user_service.service;

import com.example.user_service.entities.User;
import com.example.user_service.entities.UserDetails;
import com.example.user_service.repository.UserDetailsRepository;
import com.example.user_service.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

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

    public String register(String email, String password, String firstName, String lastName) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "{\"error\": \"Utilizatorul există deja!\"}";
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER"); //After register, this new account will be a "ROLE_USER" account.

        UserDetails userDetails = new UserDetails();
        userDetails.setFirstName(firstName);
        userDetails.setLastName(lastName);
        userDetailsRepository.save(userDetails);

        user.setUserDetails(userDetails);
        userRepository.save(user);
        return "{\"message\": \"Înregistrare reușită!\"}";
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizator negăsit"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return generateJwtToken(email);
        } else {
            return "{\"error\": \"Parolă incorectă!\"}";
        }
    }

    private String generateJwtToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generăm cheia din secretul din .env
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Expira in 24h
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return "{\"access_token\": \"" + token + "\"}";
    }

    public Claims getClaimsFromToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}