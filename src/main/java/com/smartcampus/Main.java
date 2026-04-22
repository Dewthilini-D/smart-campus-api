package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main entry point - starts the embedded Grizzly HTTP server.
 * The API will be available at http://localhost:8080/api/v1
 */
public class Main {
    
    public static final String BASE_URI = "http://localhost:8080/api/v1/";
    
    public static HttpServer startServer() {
        // Scan this package (and subpackages) for JAX-RS resources, mappers, filters
        final ResourceConfig config = new ResourceConfig()
                .packages("com.smartcampus");
        
        // Create and start Grizzly HTTP server
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }
    
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        
        System.out.println("======================================================");
        System.out.println("  Smart Campus API Server started!");
        System.out.println("  Base URL: " + BASE_URI);
        System.out.println("  Try: " + BASE_URI);
        System.out.println("  Press Enter to stop the server...");
        System.out.println("======================================================");
        
        // Keep the server running until user presses Enter
        System.in.read();
        server.shutdownNow();
    }
}