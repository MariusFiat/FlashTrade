package com.example.ai_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PythonBridgeConfig {

    @Value("${python.executable:python3}")
    private String pythonExecutable;

    @Value("${python.scripts.path:src/main/python}")
    private String scriptsPath;

    @Bean
    public ProcessBuilder pythonProcessBuilder() {
        ProcessBuilder pb = new ProcessBuilder();
        pb.environment().put("PYTHONPATH", scriptsPath);
        return pb;
    }
}
