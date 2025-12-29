package com.epam.service;

import com.epam.Calculator;
import org.springframework.web.socket.WebSocketSession;

public class Subscription {
    private final Calculator calculator;
    private final WebSocketSession webSocketSession;
    private final String sessionId;
    private final Long formulaId;

    public Subscription(Calculator calculator, WebSocketSession webSocketSession) {
        this.calculator = calculator;
        this.webSocketSession = webSocketSession;
        this.sessionId = webSocketSession.getId();
        this.formulaId = calculator.getId();
    }
}
