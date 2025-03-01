package com.jonnie.elearning.websocketconfig;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServletServerHttpRequest;

import java.util.Map;

public class UserHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            // Extract userId from query parameters (SockJS) or headers
            String userId = servletRequest.getServletRequest().getParameter("userId");

            if (userId != null) {
                attributes.put("userId", userId);
                System.out.println("WebSocket handshake successful. User ID: " + userId);
            } else {
                System.out.println("WebSocket handshake warning: No userId found in request!");
            }

            // Log Request Headers
            System.out.println("Request Headers: " + request.getHeaders());
        } else {
            System.out.println("⚠ WebSocket handshake request is not an instance of ServletServerHttpRequest.");
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        if (exception != null) {
            System.out.println("WebSocket handshake failed! Error: " + exception.getMessage());
        } else {
            System.out.println("WebSocket handshake completed successfully.");
        }

        System.out.println("Response Headers: " + response.getHeaders());
    }
}
