package com.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket Chat Server Endpoint
 * 
 * IMPORTANT CONCEPTS:
 * 1. Each WebSocket connection creates a NEW INSTANCE of this class
 * 2. Tomcat (the WebSocket container) creates these instances automatically
 * 3. We use STATIC variables to share state across all instances
 * 4. Tomcat creates and manages the Session objects for each connection
 * 
 * Why static variables?
 * - Without static: Each instance would have its own sessions collection
 * - With static: All instances share the same sessions collection
 * - This enables broadcasting messages to all connected clients
 */
@ServerEndpoint("/chat")
public class ChatWebSocket {
    
    /**
     * STATIC VARIABLES - Shared across all instances
     * 
     * Why static? Because Tomcat creates a new ChatWebSocket instance for each connection:
     * - Client A connects → new ChatWebSocket() instance
     * - Client B connects → new ChatWebSocket() instance  
     * - Client C connects → new ChatWebSocket() instance
     * 
     * Without static, each instance would have its own sessions, and clients couldn't communicate!
     */
    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();
    private static final AtomicInteger userCount = new AtomicInteger(0);
    
    /**
     * Called by Tomcat when a new WebSocket connection is established
     * 
     * WHO CALLS THIS: Tomcat (the WebSocket container)
     * WHEN: After Tomcat creates a new ChatWebSocket instance and Session object
     * WHAT: Tomcat passes the Session object it created for this connection
     * 
     * Flow:
     * 1. Client connects to ws://localhost:8080/chat
     * 2. Tomcat creates: new ChatWebSocket() instance
     * 3. Tomcat creates: new Session() object for this connection
     * 4. Tomcat calls: instance.onOpen(session)
     * 5. We add the session to our shared collection
     */
    @OnOpen
    public void onOpen(Session session) {
        // Add this session to our shared collection (all instances see this)
        sessions.add(session);
        int count = userCount.incrementAndGet();
        
        // Send welcome message to the new user
        String welcomeMessage = "Welcome to the chat! Users online: " + count;
        sendMessage(session, welcomeMessage);
        
        // Broadcast to all users that someone joined
        broadcastMessage("A new user joined the chat. Total users: " + count);
        
        System.out.println("New connection opened. Total users: " + count);
    }
    
    /**
     * Called by Tomcat when a client sends a message
     * 
     * WHO CALLS THIS: Tomcat (the WebSocket container)
     * WHEN: Client sends a message via websocket.send("Hello")
     * WHAT: Tomcat passes the message text and the Session object
     * 
     * Note: This method is called on the instance that belongs to the sending client
     * But since we use static variables, we can broadcast to ALL clients
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);
        
        // Broadcast the message to all connected clients
        // This works because all instances share the same static 'sessions' collection
        broadcastMessage("User: " + message);
    }
    
    /**
     * Called by Tomcat when a WebSocket connection is closed
     * 
     * WHO CALLS THIS: Tomcat (the WebSocket container)
     * WHEN: Client disconnects (browser closes, network issues, etc.)
     * WHAT: Tomcat passes the Session object that's being closed
     * 
     * Note: Tomcat automatically marks the Session as closed
     * We just need to clean up our references to it
     */
    @OnClose
    public void onClose(Session session) {
        // Remove this session from our shared collection
        sessions.remove(session);
        int count = userCount.decrementAndGet();
        
        // Broadcast to remaining users that someone left
        broadcastMessage("A user left the chat. Total users: " + count);
        
        System.out.println("Connection closed. Total users: " + count);
    }
    
    /**
     * Called by Tomcat when an error occurs in the WebSocket connection
     * 
     * WHO CALLS THIS: Tomcat (the WebSocket container)
     * WHEN: Network errors, protocol errors, or other WebSocket issues
     * WHAT: Tomcat passes the Session object and the error details
     * 
     * This is our chance to clean up and prevent the error from crashing the server
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
        // Clean up the session reference
        sessions.remove(session);
        userCount.decrementAndGet();
    }
    
    /**
     * Helper method to send a message to a specific client
     * 
     * WHO CREATES THE SESSION: Tomcat creates the Session object
     * WHO MANAGES THE SESSION: Tomcat handles the WebSocket protocol details
     * WHAT WE DO: Just use the Session to send messages
     * 
     * Note: We don't create or manage Session objects - Tomcat does that!
     * We just use the Session objects that Tomcat gives us.
     */
    private void sendMessage(Session session, String message) {
        try {
            // Use Tomcat's Session object to send a text message
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to broadcast a message to ALL connected clients
     * 
     * WHY THIS WORKS: Because we use static 'sessions' collection
     * - All ChatWebSocket instances share the same sessions collection
     * - When we loop through sessions, we see ALL connected clients
     * - This enables real-time broadcasting across all connections
     * 
     * Alternative approaches we discussed:
     * - Singleton pattern: Would break with Tomcat's instance creation
     * - Non-static variables: Each instance would only see its own clients
     * - Static variables: All instances share the same client list ✅
     */
    private void broadcastMessage(String message) {
        // Loop through ALL sessions from ALL instances (thanks to static!)
        for (Session session : sessions) {
            if (session.isOpen()) {
                sendMessage(session, message);
            }
        }
    }
} 