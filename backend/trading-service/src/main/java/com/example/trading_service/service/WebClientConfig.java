package com.example.trading_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpRequest;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient finnhubWebClient(WebClient.Builder builder,
                                      @Value("${finnhub.base.url}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
