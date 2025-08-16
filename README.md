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

### 6. **WebSocket Connection Limitations and Attribute Setting**
- **Connection Request (No Payload)**: WebSocket connection requests are HTTP upgrade requests that cannot contain payloads
- **No Data During Handshake**: Custom attributes cannot be sent during the initial WebSocket connection
- **Post-Connection Attribute Setting**: Attributes must be set using `websocket.send()` calls after the connection is established
- **Message Queue System**: The application implements a queue system to handle attribute requests made before connection
- **Attribute Protocol**: Custom protocol for setting session attributes: `SET_ATTRIBUTE:key:value` and `SET_ATTRIBUTES_BATCH:key1:value1|key2:value2`

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
- ✅ **Session attribute management** - Client can set custom session attributes
- ✅ **Message queue system** - Handles requests before WebSocket connection
- ✅ **Batch attribute setting** - Set multiple attributes in single message
- ✅ **Smart attribute protocol** - Custom message format for efficient attribute management

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

## Production Architecture

### **Real-World WebSocket Setup with Reverse Proxy**

```
Client (Chrome) → Nginx (Reverse Proxy) → Multiple Backend Instances
     ↓                    ↓                           ↓
  WebSocket         Forwards Request           Tomcat + Spring
  Connection        (HTTP Proxy Only)         WebSocket Handler
                    Load Balancing            Session Management
```

### **What Each Component Does:**

#### **1. Client (Browser)**
- Initiates WebSocket connection to `ws://example.com/chat`
- Handles WebSocket protocol on client side
- Manages connection lifecycle and message handling

#### **2. Nginx (Reverse Proxy)**
- **Receives** initial WebSocket connection request
- **Cannot handle** WebSocket protocol directly (HTTP-only)
- **Forwards** request to backend application instances
- **Load balances** across multiple backend servers
- **Handles** SSL termination, static files, and HTTP requests

#### **3. Backend Instances (Tomcat + Spring)**
- **Multiple instances** for scalability and high availability
- **Tomcat** handles HTTP upgrade to WebSocket protocol
- **Spring Boot** manages WebSocket lifecycle and routing
- **Creates** `WebSocketSession` objects from raw connections
- **Your handler** receives pre-created sessions and manages business logic

### **Connection Flow:**

```
1. Client → Nginx: ws://example.com/chat
2. Nginx → Backend: Forwards HTTP upgrade request
3. Backend → Tomcat: HTTP upgrade to WebSocket
4. Tomcat → Spring: Raw WebSocket connection
5. Spring → Handler: WebSocketSession object
6. Handler → Business Logic: Session management and messaging
```

### **Why This Architecture:**

#### **1. Separation of Concerns**
- **Nginx**: HTTP proxy, SSL, load balancing, static files
- **Backend**: WebSocket protocol, business logic, session management

#### **2. Scalability**
- **Nginx**: Handle thousands of HTTP connections efficiently
- **Backend**: Scale horizontally with multiple instances
- **Load balancing**: Distribute WebSocket connections across instances

#### **3. Technology Specialization**
- **Nginx**: Best at HTTP serving and proxying
- **Tomcat**: Best at Java application hosting
- **Spring**: Best at application logic and WebSocket management

### **Key Points:**

- ✅ **Nginx cannot handle WebSocket protocol** - it's HTTP-only
- ✅ **Nginx acts as reverse proxy** - forwards requests to backend
- ✅ **Backend applications handle WebSocket** - protocol upgrade and management
- ✅ **Multiple backend instances** - for load balancing and high availability
- ✅ **Spring wraps raw connections** - creates `WebSocketSession` objects

## WebSocket Protocol Flow

1. **Handshake**: Client initiates HTTP upgrade request to `/chat`
2. **Connection**: Spring Boot upgrades to WebSocket protocol
3. **Registration**: Handler receives `afterConnectionEstablished` callback
4. **Communication**: Bidirectional message exchange
5. **Cleanup**: Handler receives `afterConnectionClosed` callback

## WebSocket Connection Technical Details

### **Connection Request Limitations**

#### **1. No Payload During Handshake**
```
❌ NOT POSSIBLE - WebSocket connection request cannot contain payloads
Browser → HTTP GET /chat HTTP/1.1
         Upgrade: websocket
         Connection: Upgrade
         Sec-WebSocket-Key: [key]
         [NO BODY/PAYLOAD - Just HTTP headers]
```

#### **2. Why No Payload?**
- **WebSocket connection** is an **HTTP upgrade request**
- **HTTP upgrade requests** don't support request bodies
- **WebSocket handshake** is **header-only** by design
- **Data transfer** happens **after** connection establishment

#### **3. What You CAN Do During Handshake**
```javascript
// ✅ Query parameters in URL (limited data)
let ws = new WebSocket('ws://localhost:8080/chat?userId=123&theme=dark');

// ❌ Custom headers (not supported from JavaScript)
let ws = new WebSocket('ws://localhost:8080/chat', {
    headers: { 'X-Theme': 'dark' }  // ← This won't work
});

// ❌ Request body (not possible)
let ws = new WebSocket('ws://localhost:8080/chat', {
    body: "theme=dark&language=en"  // ← This won't work
});
```

### **Post-Connection Attribute Setting**

#### **1. Correct Approach**
```javascript
// ✅ Set attributes AFTER connection is established
websocket.onopen = function() {
    // Now you can send data
    websocket.send('SET_ATTRIBUTE:theme:dark');
    websocket.send('SET_ATTRIBUTE:language:en');
};
```

#### **2. Message Queue System**
The application implements a **smart queue system** to handle this limitation:

```javascript
// User clicks button before connection
setAttribute('theme', 'dark');

// Message gets queued if not connected
if (websocket && websocket.readyState === WebSocket.OPEN) {
    websocket.send(message);  // Send immediately
} else {
    messageQueue.push(message);  // Queue for later
    addMessage('Attribute request queued - will send when connected', 'system');
}

// When connection opens, all queued messages are sent automatically
websocket.onopen = function() {
    processMessageQueue();  // ← Sends all queued messages!
};
```

#### **3. Attribute Protocol**
Custom protocol for efficient attribute management:

```
Single Attribute: SET_ATTRIBUTE:key:value
Batch Attributes: SET_ATTRIBUTES_BATCH:key1:value1|key2:value2|key3:value3
Get Attributes: GET_ATTRIBUTES
```

### **Technical Benefits of This Approach**

#### **1. Protocol Compliance**
- **Follows WebSocket standards** correctly
- **No workarounds** for handshake limitations
- **Clean separation** between connection and data

#### **2. User Experience**
- **No lost requests** due to connection timing
- **Immediate feedback** (queued status)
- **Automatic retry** when connection opens

#### **3. Scalability**
- **Efficient batch operations** (multiple attributes in one message)
- **Reduced network overhead** (fewer messages)
- **Better performance** for multiple attributes

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