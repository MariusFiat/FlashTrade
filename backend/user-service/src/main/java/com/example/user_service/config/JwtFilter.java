package com.example.user_service.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.user_service.service.CustomAuthService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private CustomAuthService authService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/auth/") || 
               path.contains("/swagger-ui") || 
               path.contains("/api-docs") || 
               path.contains("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Claims claims = authService.getClaimsFromToken(token);  //Extract email and roles from the token

                if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String email = claims.getSubject();
                    
                    // Validate that the user still exists with this email
                    if (!authService.validateUserExists(email)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"error\":\"Invalid token - user not found\"}");
                        response.setContentType("application/json");
                        return;
                    }
                    
                    String role = claims.get("role", String.class);

                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(email, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (RuntimeException e) {
                // Invalid token - return 403
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
                response.setContentType("application/json");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}