# WebSocket Chat Application

A simple real-time chat application built with Java WebSocket API (JSR 356) and HTML/JavaScript client.

## What This Application Demonstrates

- **WebSocket Server**: Java server using Jakarta WebSocket API
- **Real-time Communication**: Bidirectional messaging between clients
- **Connection Management**: Handling multiple client connections
- **Message Broadcasting**: Sending messages to all connected clients
- **Error Handling**: Graceful handling of connection errors and disconnections

## Project Structure

```
websocket/
├── pom.xml                          # Maven configuration
├── src/main/java/com/websocket/
│   ├── ChatWebSocket.java          # WebSocket server endpoint
│   └── WebSocketServer.java        # Standalone server launcher
├── src/main/webapp/
│   └── index.html                  # Web client interface
└── README.md                       # This file
```

## Key WebSocket Concepts Demonstrated

### 1. **@ServerEndpoint**
- Defines the WebSocket endpoint at `/chat`
- Handles the WebSocket lifecycle events

### 2. **Lifecycle Methods**
- `@OnOpen`: Called when a client connects
- `@OnMessage`: Called when a message is received
- `@OnClose`: Called when a client disconnects
- `@OnError`: Called when an error occurs

### 3. **Session Management**
- Thread-safe collection of active sessions
- User count tracking
- Connection state monitoring

### 4. **Message Broadcasting**
- Sending messages to all connected clients
- Individual message sending to specific clients

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Setup and Running

### 1. Compile the Project
```bash
mvn compile
```

### 2. Start the Server
```bash
mvn exec:java -Dexec.mainClass="com.websocket.WebSocketServer"
```

Or run the main class directly:
```bash
java -cp target/classes com.websocket.WebSocketServer
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
4. **Connection Events**: Watch for join/leave notifications

## Features

- ✅ Real-time messaging
- ✅ Multiple client support
- ✅ Connection status indicators
- ✅ Automatic reconnection
- ✅ User count tracking
- ✅ Join/leave notifications
- ✅ Error handling
- ✅ Responsive UI

## WebSocket Protocol Flow

1. **Handshake**: Client initiates HTTP upgrade request
2. **Connection**: Server upgrades to WebSocket protocol
3. **Communication**: Bidirectional message exchange
4. **Disconnection**: Graceful connection termination

## Learning Points

### Server Side (Java)
- `@ServerEndpoint` annotation for endpoint definition
- Session management with thread-safe collections
- Message broadcasting to multiple clients
- Error handling and connection lifecycle

### Client Side (JavaScript)
- WebSocket API usage
- Event handling (open, message, close, error)
- Real-time UI updates
- Connection state management

## Next Steps for Learning

1. **Add User Names**: Implement user identification
2. **Private Messages**: Add direct messaging between users
3. **Message History**: Store messages in a database
4. **Authentication**: Add user login/logout
5. **File Sharing**: Implement file upload/download
6. **Spring Integration**: Migrate to Spring WebSocket

## Troubleshooting

### Common Issues

1. **Port Already in Use**: Change port in `WebSocketServer.java`
2. **Connection Refused**: Ensure server is running before opening client
3. **Compilation Errors**: Check Java version compatibility

### Debug Tips

- Check browser console for client-side errors
- Monitor server console for connection logs
- Use browser developer tools to inspect WebSocket traffic

## Dependencies

- **Jakarta WebSocket API**: Standard WebSocket implementation
- **Tomcat Embed**: Lightweight server for standalone deployment
- **Maven**: Build and dependency management

This application provides a solid foundation for understanding WebSocket concepts and can be extended with additional features as you learn more about real-time web communication. 