package com.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot WebSocket Chat Application
 * 
 * This replaces our custom WebSocketServer with Spring Boot's built-in WebSocket support.
 * Spring Boot automatically handles:
 * - Embedded Tomcat server
 * - WebSocket endpoint registration
 * - Dependency injection
 * - Configuration management
 */
@SpringBootApplication
public class SpringWebSocketApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringWebSocketApplication.class, args);
    }
} 