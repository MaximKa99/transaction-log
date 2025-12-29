package com.epam.config;

import com.epam.handler.SubscribeHandler;
import com.epam.service.SubscriptionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final ObjectMapper objectMapper;
    private final SubscriptionManager subscriptionManager;

    public WebSocketConfig(ObjectMapper objectMapper, SubscriptionManager subscriptionManager) {
        this.objectMapper = objectMapper;
        this.subscriptionManager = subscriptionManager;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SubscribeHandler(objectMapper, subscriptionManager), "/subscribe");
        registry.addHandler(new SubscribeHandler(objectMapper, subscriptionManager), "/unsubscribe");
    }
}
