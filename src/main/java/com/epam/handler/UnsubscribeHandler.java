package com.epam.handler;

import com.epam.service.SubscriptionManager;
import com.epam.view.UnsubscribeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class UnsubscribeHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final SubscriptionManager subscriptionManager;

    public UnsubscribeHandler(
            ObjectMapper objectMapper,
            SubscriptionManager subscriptionManager
    ) {
        this.objectMapper = objectMapper;
        this.subscriptionManager = subscriptionManager;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        UnsubscribeRequest subscribeRequest = objectMapper.readValue(message.getPayload(), UnsubscribeRequest.class);
        subscriptionManager.removeSession(session);
    }
}
