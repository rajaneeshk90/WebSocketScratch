package com.websocket;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.catalina.connector.Connector;
import jakarta.websocket.server.ServerEndpointConfig;

import java.io.File;

public class WebSocketServer {
    
    public static void main(String[] args) throws Exception {
        try {
            System.out.println("Starting WebSocket Chat Server...");
            
            // Create Tomcat instance
            Tomcat tomcat = new Tomcat();
            
            // Set the base directory for temporary files
            String tempDir = System.getProperty("java.io.tmpdir");
            tomcat.setBaseDir(tempDir);
            
            // Create and configure the connector
            Connector connector = new Connector();
            connector.setPort(8081);
            connector.setProperty("socket.directBuffer", "false");
            tomcat.setConnector(connector);
            
            // Get the base directory for the application
            String webappDirLocation = "src/main/webapp/";
            File webappDir = new File(webappDirLocation);
            
            System.out.println("Webapp directory: " + webappDir.getAbsolutePath());
            System.out.println("Webapp directory exists: " + webappDir.exists());
            System.out.println("Webapp directory is readable: " + webappDir.canRead());
            
            if (!webappDir.exists()) {
                throw new RuntimeException("Webapp directory does not exist: " + webappDir.getAbsolutePath());
            }
            
            Context context = tomcat.addWebapp("", webappDir.getAbsolutePath());
            System.out.println("Added webapp context: " + context.getName());
            
            // Add WebSocket support - Programmatic registration approach
            context.addServletContainerInitializer(new org.apache.tomcat.websocket.server.WsSci(), null);
            System.out.println("Added WebSocket support with WsSci");
            
            // Try to programmatically register the endpoint
            try {
                ServerEndpointConfig config = ServerEndpointConfig.Builder
                    .create(com.websocket.ChatWebSocket.class, "/chat")
                    .build();
                
                // Get the WebSocket container and add the endpoint
                Object wsContainer = context.getServletContext().getAttribute("org.apache.tomcat.websocket.server.WsServerContainer");
                if (wsContainer != null) {
                    java.lang.reflect.Method addEndpoint = wsContainer.getClass().getMethod("addEndpoint", ServerEndpointConfig.class);
                    addEndpoint.invoke(wsContainer, config);
                    System.out.println("✅ Programmatically registered WebSocket endpoint");
                } else {
                    System.err.println("❌ WebSocket container not found");
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to programmatically register endpoint: " + e.getMessage());
            }
            
            // Set up resources
            WebResourceRoot resources = new StandardRoot(context);
            File classesDir = new File("target/classes");
            System.out.println("Classes directory: " + classesDir.getAbsolutePath());
            System.out.println("Classes directory exists: " + classesDir.exists());
            
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", 
                    classesDir.getAbsolutePath(), "/"));
            context.setResources(resources);
            System.out.println("Set up resources");
            
            // Start the server
            System.out.println("Starting Tomcat...");
            tomcat.start();
            System.out.println("Tomcat started successfully");
            
            // Check if server is actually listening
            System.out.println("Checking if server is listening on port 8081...");
            try {
                java.net.Socket socket = new java.net.Socket("localhost", 8081);
                socket.close();
                System.out.println("✅ Server is accessible on localhost:8081");
            } catch (Exception e) {
                System.err.println("❌ Server is NOT accessible on localhost:8081: " + e.getMessage());
            }
            
            // Test WebSocket endpoint
            System.out.println("Testing WebSocket endpoint...");
            try {
                java.net.Socket socket = new java.net.Socket("localhost", 8081);
                java.io.OutputStream out = socket.getOutputStream();
                String request = "GET /chat HTTP/1.1\r\n" +
                               "Host: localhost:8081\r\n" +
                               "Upgrade: websocket\r\n" +
                               "Connection: Upgrade\r\n" +
                               "Sec-WebSocket-Key: x3JJHMbDL1EzLkh9GBhXDw==\r\n" +
                               "Sec-WebSocket-Version: 13\r\n\r\n";
                out.write(request.getBytes());
                out.flush();
                
                java.io.InputStream in = socket.getInputStream();
                byte[] response = new byte[1024];
                int bytesRead = in.read(response);
                String responseStr = new String(response, 0, bytesRead);
                
                if (responseStr.contains("101") || responseStr.contains("Switching Protocols")) {
                    System.out.println("✅ WebSocket endpoint is working");
                } else {
                    System.out.println("❌ WebSocket endpoint test failed: " + responseStr);
                }
                
                socket.close();
            } catch (Exception e) {
                System.err.println("❌ WebSocket endpoint test error: " + e.getMessage());
            }
            
            System.out.println("WebSocket Chat Server started on http://localhost:8081");
            System.out.println("Open the chat client in your browser!");
            
            // Keep the server running
            tomcat.getServer().await();
            
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
} 