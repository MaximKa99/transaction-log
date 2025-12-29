package com.epam.handler;

import com.epam.service.SubscriptionManager;
import com.epam.view.SubscribeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class SubscribeHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final SubscriptionManager subscriptionManager;

    public SubscribeHandler(
            ObjectMapper objectMapper,
            SubscriptionManager subscriptionManager
    ) {
        this.objectMapper = objectMapper;
        this.subscriptionManager = subscriptionManager;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        SubscribeRequest subscribeRequest = objectMapper.readValue(message.getPayload(), SubscribeRequest.class);
        subscriptionManager.subscribeToFormula(session, subscribeRequest.getFormulaId());
    }
}
