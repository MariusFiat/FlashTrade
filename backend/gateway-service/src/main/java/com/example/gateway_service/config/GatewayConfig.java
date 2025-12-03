package com.example.gateway_service.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("trading-service", r -> r
                        .path("/api/trading/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("http://localhost:8081"))
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("http://localhost:8082"))
                .build();
    }
}
