# WebSocket Chat Application

A real-time chat application built with **Spring Boot** and **WebSocket** technology.

## What This Application Demonstrates

- **Spring Boot WebSocket Server**: Reliable WebSocket endpoint management
- **Real-time Communication**: Bidirectional messaging between clients
- **Connection Management**: Handling multiple client connections
- **Message Broadcasting**: Sending messages to all connected clients
- **Modern Architecture**: Spring Boot's built-in WebSocket support

## Project Structure

```
websocket/
├── pom.xml                          # Maven configuration with Spring Boot
├── src/main/java/com/websocket/
│   ├── SpringWebSocketApplication.java  # Spring Boot main application
│   ├── WebSocketConfig.java             # WebSocket configuration
│   └── ChatWebSocketHandler.java        # WebSocket message handler
├── src/main/resources/
│   └── application.properties           # Spring Boot configuration
├── src/main/webapp/
│   └── index.html                      # Web client interface
└── README.md                           # This file
```

## Key WebSocket Concepts Demonstrated

### 1. **Spring Boot WebSocket Support**
- `@EnableWebSocket` annotation enables WebSocket functionality
- `WebSocketConfigurer` interface for endpoint registration
- Automatic WebSocket lifecycle management

### 2. **WebSocket Session Creation**
- **Spring Framework creates** `WebSocketSession` objects, not Tomcat or our application
- **Tomcat handles** raw network connections and WebSocket protocol handshake
- **Spring wraps** Tomcat's connection in `WebSocketSession` objects
- **Our handler receives** pre-created sessions from Spring's WebSocket infrastructure
- **No static variables needed** - Spring ensures single handler instance for all connections

### 3. **WebSocket Handler Lifecycle**
- `afterConnectionEstablished()`: Called when a client connects
- `handleTextMessage()`: Called when a message is received
- `afterConnectionClosed()`: Called when a client disconnects
- `handleTransportError()`: Called when an error occurs

### 4. **Session Management**
- Spring-managed WebSocket sessions
- Thread-safe session collection
- User count tracking
- Connection state monitoring

### 5. **Message Broadcasting**
- Sending messages to all connected clients
- Real-time message delivery
- Error handling for failed message delivery

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Setup and Running

### 1. Compile the Project
```bash
mvn clean compile
```

### 2. Start the Server
```bash
mvn spring-boot:run
```

### 3. Access the Chat Client
Open your web browser and navigate to:
```
http://localhost:8080
```

## How to Test

1. **Single User Test**: Open the chat in one browser tab and send messages
2. **Multiple Users Test**: Open the chat in multiple browser tabs/windows
3. **Real-time Messaging**: Messages appear instantly across all connected clients
4. **Connection Events**: Watch for join/leave notifications in the server console

## Features

- ✅ Real-time messaging
- ✅ Multiple client support
- ✅ Connection status indicators
- ✅ Automatic reconnection
- ✅ User count tracking
- ✅ Join/leave notifications
- ✅ Error handling
- ✅ Responsive UI
- ✅ Spring Boot reliability

## Spring Boot Benefits

### **Why Spring Boot is Better:**
- **Automatic WebSocket registration** - No more 404 errors
- **Built-in Tomcat server** - No custom server setup needed
- **Dependency management** - Spring Boot handles all dependencies
- **Production ready** - Enterprise-grade reliability
- **Easy configuration** - Simple properties file setup
- **Auto-reload** - Development tools for faster iteration

## Architecture

```
Client Browser → Spring Boot Server → WebSocket Handler
     ↓                    ↓                    ↓
  JavaScript         Embedded Tomcat    ChatWebSocketHandler
  WebSocket         Port 8080         Session Management
  Connection        /chat endpoint    Message Broadcasting
```

## WebSocket Protocol Flow

1. **Handshake**: Client initiates HTTP upgrade request to `/chat`
2. **Connection**: Spring Boot upgrades to WebSocket protocol
3. **Registration**: Handler receives `afterConnectionEstablished` callback
4. **Communication**: Bidirectional message exchange
5. **Cleanup**: Handler receives `afterConnectionClosed` callback

## Learning Points

### **Server Side (Spring Boot)**
- `@EnableWebSocket` for WebSocket support
- `WebSocketConfigurer` for endpoint registration
- `TextWebSocketHandler` for message handling
- Spring-managed session lifecycle

### **Client Side (JavaScript)**
- WebSocket API usage
- Event handling (open, message, close, error)
- Real-time UI updates
- Connection state management

## Next Steps for Learning

1. **Add User Names**: Implement user identification
2. **Private Messages**: Add direct messaging between users
3. **Message History**: Store messages in a database
4. **Authentication**: Add user login/logout with Spring Security
5. **File Sharing**: Implement file upload/download
6. **Advanced Features**: Add typing indicators, read receipts

## Troubleshooting

### **Common Issues**

1. **Port Already in Use**: Change port in `application.properties`
2. **Compilation Errors**: Ensure Java 17+ is installed
3. **WebSocket Connection Fails**: Check server logs for errors

### **Debug Tips**

- Check Spring Boot console for connection logs
- Monitor WebSocket endpoint registration
- Use browser developer tools to inspect WebSocket traffic

## Dependencies

- **Spring Boot Web**: Web application support
- **Spring Boot WebSocket**: WebSocket functionality
- **Spring Boot DevTools**: Development utilities
- **Embedded Tomcat**: Web server (included automatically)

## Summary

This application demonstrates **modern WebSocket development** using Spring Boot, providing a solid foundation for real-time web applications. Spring Boot eliminates the complexity of manual WebSocket setup while providing enterprise-grade reliability and performance.

The key advantage is that **Spring Boot handles all the WebSocket infrastructure**, allowing you to focus on your application logic rather than server configuration. 