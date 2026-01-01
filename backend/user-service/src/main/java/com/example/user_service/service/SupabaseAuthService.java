//package com.example.user_service.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import java.util.Map;
//
//@Service
//public class SupabaseAuthService {
//
//    // IntelliJ injectează aceste variabile din .env în procesul Spring
//    @Value("${DB_API_URL}")
//    private String supabaseUrl;
//
//    @Value("${DB_ANON_KEY}")
//    private String supabaseKey;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public String authenticate(String email, String password) {
//        String url = supabaseUrl + "/auth/v1/token?grant_type=password";
//
//        // Configurăm headerele obligatorii pentru Supabase
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("apikey", supabaseKey);
//        headers.set("Authorization", "Bearer " + supabaseKey);
//
//        // Body-ul cererii conform documentației GoTrue (Supabase Auth)
//        Map<String, String> body = Map.of(
//                "email", email,
//                "password", password
//        );
//
//        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
//
//        try {
//            // Returnăm direct răspunsul care conține access_token-ul
//            return restTemplate.postForObject(url, entity, String.class);
//        } catch (Exception e) {
//            return "{\"error\": \"Autentificare eșuată: " + e.getMessage() + "\"}";
//        }
//    }
//
//    public String register(String email, String password) {
//        // Endpoint-ul pentru creare de utilizator nou
//        String url = supabaseUrl.replaceAll("/$", "") + "/auth/v1/signup";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("apikey", supabaseKey);
//        headers.set("Authorization", "Bearer " + supabaseKey);
//
//        Map<String, String> body = Map.of(
//                "email", email,
//                "password", password
//        );
//
//        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
//
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
//            return response.getBody();
//        } catch (org.springframework.web.client.HttpClientErrorException e) {
//            return "Eroare Register: " + e.getResponseBodyAsString();
//        }
//    }
//}