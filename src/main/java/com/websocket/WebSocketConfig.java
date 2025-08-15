package com.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket Configuration for Spring Boot
 * 
 * This class configures WebSocket support and registers our chat handler.
 * Spring Boot will automatically scan this configuration and set up WebSocket endpoints.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Register our chat handler at the /chat endpoint
        registry.addHandler(new ChatWebSocketHandler(), "/chat")
                .setAllowedOrigins("*"); // Allow connections from any origin
    }
} 