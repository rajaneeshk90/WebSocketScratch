package com.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Spring WebSocket Chat Handler
 * 
 * This replaces our JSR 356 ChatWebSocket with Spring's WebSocketHandler.
 * Spring Boot automatically manages the WebSocket lifecycle and calls these methods.
 * 
 * Key differences from JSR 356:
 * - Extends TextWebSocketHandler instead of using @ServerEndpoint
 * - Spring manages the WebSocket lifecycle
 * - No need for static variables - Spring handles instance management
 * - More reliable and easier to configure
 */
public class ChatWebSocketHandler extends TextWebSocketHandler {
    
    // Store all active WebSocket sessions
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final AtomicInteger userCount = new AtomicInteger(0);
    
    /**
     * Called when a new WebSocket connection is established
     * 
     * WHO CALLS THIS: Spring Boot WebSocket framework
     * WHEN: After a client successfully connects to ws://localhost:8080/chat
     * WHAT: Spring passes the WebSocketSession object
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        int count = userCount.incrementAndGet();
        
        // Send welcome message to the new user
        String welcomeMessage = "Welcome to the chat! Users online: " + count;
        session.sendMessage(new TextMessage(welcomeMessage));
        
        // Broadcast to all users that someone joined
        broadcastMessage("A new user joined the chat. Total users: " + count);
        
        System.out.println("New connection established. Total users: " + count);
    }
    
    /**
     * Called when a client sends a message
     * 
     * WHO CALLS THIS: Spring Boot WebSocket framework
     * WHEN: Client sends a message via websocket.send("Hello")
     * WHAT: Spring passes the message text and the WebSocketSession
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received message: " + payload);
        
        // Broadcast the message to all connected clients
        broadcastMessage("User: " + payload);
    }
    
    /**
     * Called when a WebSocket connection is closed
     * 
     * WHO CALLS THIS: Spring Boot WebSocket framework
     * WHEN: Client disconnects (browser closes, network issues, etc.)
     * WHAT: Spring passes the WebSocketSession and close status
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        int count = userCount.decrementAndGet();
        
        // Broadcast to remaining users that someone left
        broadcastMessage("A user left the chat. Total users: " + count);
        
        System.out.println("Connection closed. Total users: " + count);
    }
    
    /**
     * Called when an error occurs in the WebSocket connection
     * 
     * WHO CALLS THIS: Spring Boot WebSocket framework
     * WHEN: Network errors, protocol errors, or other WebSocket issues
     * WHAT: Spring passes the WebSocketSession and the error details
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket transport error: " + exception.getMessage());
        sessions.remove(session);
        userCount.decrementAndGet();
    }
    
    /**
     * Helper method to broadcast a message to ALL connected clients
     * 
     * WHY THIS WORKS: Spring manages all WebSocket sessions
     * - All sessions are stored in our sessions collection
     * - Spring ensures thread safety
     * - This enables real-time broadcasting across all connections
     */
    private void broadcastMessage(String message) {
        TextMessage textMessage = new TextMessage(message);
        
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(textMessage);
                } catch (IOException e) {
                    System.err.println("Error sending message to session: " + e.getMessage());
                }
            }
        }
    }
} 