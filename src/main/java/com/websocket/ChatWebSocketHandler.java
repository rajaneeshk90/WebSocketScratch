package com.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
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

        // ✅ ADD CUSTOM ATTRIBUTES TO THE SESSION
        // Note: userId will be set by client's USER_ID message
        session.getAttributes().put("connectedAt", System.currentTimeMillis());
        session.getAttributes().put("clientIP", session.getRemoteAddress().getAddress().getHostAddress());
        session.getAttributes().put("userAgent", session.getRemoteAddress().getHostName());
        session.getAttributes().put("status", "online");

        System.out.println("New connection established: " + session.getId());
        System.out.println("Session attributes: " + session.getAttributes());
        System.out.println("Client IP: " + session.getAttributes().get("clientIP"));
        System.out.println("User ID: " + session.getAttributes().get("userId"));
        
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
        
        // ✅ HANDLE USER_ID MESSAGE FROM CLIENT
        if (payload.startsWith("USER_ID:")) {
            String clientUserId = payload.substring(8); // Remove "USER_ID:" prefix
            session.getAttributes().put("userId", clientUserId);
            System.out.println("Client set user ID: " + clientUserId);
            return; // Don't broadcast USER_ID messages
        }
        
        // ✅ HANDLE SET_ATTRIBUTE MESSAGE FROM CLIENT
        if (payload.startsWith("SET_ATTRIBUTE:")) {
            // Format: "SET_ATTRIBUTE:key:value"
            String attributeData = payload.substring(14); // Remove "SET_ATTRIBUTE:" prefix
            int colonIndex = attributeData.indexOf(':');
            
            if (colonIndex > 0) {
                String key = attributeData.substring(0, colonIndex);
                String value = attributeData.substring(colonIndex + 1);
                
                // Store the attribute in session
                session.getAttributes().put(key, value);
                System.out.println("✅ Client set attribute: " + key + " = " + value);
                
                // Send confirmation back to client
                String confirmation = "Attribute set: " + key + " = " + value;
                session.sendMessage(new TextMessage(confirmation));
                
                return; // Don't broadcast SET_ATTRIBUTE messages
            } else {
                System.out.println("❌ Invalid SET_ATTRIBUTE format: " + payload);
                session.sendMessage(new TextMessage("Error: Invalid attribute format. Use SET_ATTRIBUTE:key:value"));
                return;
            }
        }
        
        // ✅ HANDLE BATCH SET_ATTRIBUTES MESSAGE FROM CLIENT
        if (payload.startsWith("SET_ATTRIBUTES_BATCH:")) {
            // Format: "SET_ATTRIBUTES_BATCH:key1:value1|key2:value2|key3:value3"
            String batchData = payload.substring(21); // Remove "SET_ATTRIBUTES_BATCH:" prefix
            
            if (batchData.length() > 0) {
                String[] attributePairs = batchData.split("\\|");
                int successCount = 0;
                StringBuilder response = new StringBuilder("Batch attributes set:\n");
                
                for (String pair : attributePairs) {
                    int colonIndex = pair.indexOf(':');
                    if (colonIndex > 0) {
                        String key = pair.substring(0, colonIndex);
                        String value = pair.substring(colonIndex + 1);
                        
                        // Store the attribute in session
                        session.getAttributes().put(key, value);
                        response.append("- ").append(key).append(" = ").append(value).append("\n");
                        successCount++;
                        
                        System.out.println("✅ Client set batch attribute: " + key + " = " + value);
                    }
                }
                
                // Send confirmation back to client
                response.append("Total attributes set: ").append(successCount);
                session.sendMessage(new TextMessage(response.toString()));
                
                return; // Don't broadcast SET_ATTRIBUTES_BATCH messages
            } else {
                System.out.println("❌ Invalid SET_ATTRIBUTES_BATCH format: " + payload);
                session.sendMessage(new TextMessage("Error: Invalid batch format. Use SET_ATTRIBUTES_BATCH:key1:value1|key2:value2"));
                return;
            }
        }
        
        // ✅ HANDLE GET_ATTRIBUTES MESSAGE FROM CLIENT
        if (payload.equals("GET_ATTRIBUTES")) {
            // Send all current session attributes back to client
            StringBuilder attributesList = new StringBuilder("Current attributes:\n");
            for (Map.Entry<String, Object> entry : session.getAttributes().entrySet()) {
                attributesList.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            
            session.sendMessage(new TextMessage(attributesList.toString()));
            System.out.println("✅ Client requested attributes for session: " + session.getId());
            return; // Don't broadcast GET_ATTRIBUTES messages
        }
        
        // ✅ ACCESS EXISTING ATTRIBUTES
        String userId = (String) session.getAttributes().get("userId");
        String clientIP = (String) session.getAttributes().get("clientIP");
        
        // ✅ ADD/UPDATE ATTRIBUTES
        Integer messageCount = (Integer) session.getAttributes().get("messageCount");
        if (messageCount == null) {
            messageCount = 0;
        }
        session.getAttributes().put("messageCount", messageCount + 1);
        session.getAttributes().put("lastMessageTime", System.currentTimeMillis());
        
        System.out.println("Received message from " + userId + " (" + clientIP + "): " + payload);
        System.out.println("User's message count: " + session.getAttributes().get("messageCount"));
        
        // Broadcast the message to all connected clients
        broadcastMessage("User " + userId + ": " + payload);
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