package com.epam.service;

import com.epam.Calculator;
import com.epam.view.CalculationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SubscriptionManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(SubscriptionManager.class);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    //The mapping of formula to web socket sessions (thread-safe for concurrent updates)
    private final Map<Calculator, Set<String>> calculatorToSessions = new ConcurrentHashMap<>();
    // Reverse mapping: formulaId -> Calculator for quick lookup (thread-safe)
    private final Map<Long, Calculator> formulaIdToCalculator = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> subscribedSessions = new ConcurrentHashMap<>();
    private final AtomicBoolean processStarted = new AtomicBoolean(false);

    private final FormulaService formulaService;
    private final ObjectMapper objectMapper;

    public SubscriptionManager(FormulaService formulaService, ObjectMapper objectMapper) {
        this.formulaService = formulaService;
        this.objectMapper = objectMapper;
    }

    public void subscribeToFormula(WebSocketSession webSocketSession, Long formulaId) {
         Calculator calculator = formulaIdToCalculator.computeIfAbsent(
             formulaId, 
             id -> new Calculator(formulaService.getFormulaById(id))
         );

         Set<String> toBeModifiedSessions = calculatorToSessions.getOrDefault(calculator, new HashSet<>());
         toBeModifiedSessions.add(webSocketSession.getId());
         calculatorToSessions.put(calculator, toBeModifiedSessions);

         subscribedSessions.put(webSocketSession.getId(), webSocketSession);
         
         // Start the process if not already started
         if (processStarted.compareAndSet(false, true)) {
             executorService.submit(this::process);
         }
    }

    public void removeSession(WebSocketSession webSocketSession) {
        calculatorToSessions.forEach((key, value) -> value.remove(webSocketSession.getId()));
        subscribedSessions.remove(webSocketSession.getId());
    }

    /**
     * Updates the calculator for a given formula ID when the formula changes.
     * This replaces the old calculator with a new one that uses the updated formula,
     * ensuring that subsequent calculations use the new formula immediately.
     * 
     * @param formulaId The ID of the formula that changed
     * @param updatedFormula The updated formula entity
     */
    public void updateCalculatorForFormula(Long formulaId, com.epam.entity.Formula updatedFormula) {
        Calculator oldCalculator = formulaIdToCalculator.get(formulaId);
        
        if (oldCalculator == null) {
            LOGGER.debug("No calculator found for formula ID {}, nothing to update", formulaId);
            return;
        }

        // Create new calculator with updated formula
        Calculator newCalculator = new Calculator(updatedFormula);
        
        // Get the sessions associated with the old calculator
        Set<String> sessions = calculatorToSessions.remove(oldCalculator);
        
        if (sessions != null && !sessions.isEmpty()) {
            // Update the reverse mapping
            formulaIdToCalculator.put(formulaId, newCalculator);
            
            // Associate the sessions with the new calculator
            calculatorToSessions.put(newCalculator, sessions);
            
            LOGGER.info("Updated calculator for formula ID {} with {} active sessions", 
                    formulaId, sessions.size());
        } else {
            // No active sessions, just update the mapping
            formulaIdToCalculator.put(formulaId, newCalculator);
            LOGGER.debug("Updated calculator for formula ID {} (no active sessions)", formulaId);
        }
    }

    /**
     * Removes the calculator for a deleted formula.
     * 
     * @param formulaId The ID of the deleted formula
     */
    public void removeCalculatorForFormula(Long formulaId) {
        Calculator calculator = formulaIdToCalculator.remove(formulaId);
        
        if (calculator != null) {
            Set<String> sessions = calculatorToSessions.remove(calculator);
            if (sessions != null) {
                LOGGER.info("Removed calculator for deleted formula ID {} with {} active sessions", 
                        formulaId, sessions.size());
            } else {
                LOGGER.debug("Removed calculator for deleted formula ID {} (no active sessions)", formulaId);
            }
        }
    }

    public void process() {
        Iterator<Double> numberIterator = NumberSequenceGenerator.getSequence().iterator();
        
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Double value = numberIterator.next();
                
                // Sleep for 1 second between calculations
                Thread.sleep(1000);
                
                // Process each calculator and send results to subscribed sessions
                calculatorToSessions.entrySet().forEach(entry -> {
                    Calculator calculator = entry.getKey();
                    entry.getValue().forEach(sessionId -> {
                        WebSocketSession session = subscribedSessions.get(sessionId);
                        
                        // Check if session exists and is open
                        if (session != null && session.isOpen()) {
                            try {
                                CalculationResult result = calculator.calculate(value);
                                String jsonResult = objectMapper.writeValueAsString(result);
                                session.sendMessage(new TextMessage(jsonResult));
                            } catch (IOException e) {
                                // Session might be closed, remove it
                                LOGGER.warn("Failed to send message to session {}: {}", sessionId, e.getMessage());
                                removeSession(session);
                            } catch (Exception e) {
                                LOGGER.error("Error calculating or sending result for session {}: {}", sessionId, e.getMessage(), e);
                            }
                        } else if (session != null) {
                            // Session is closed, remove it
                            removeSession(session);
                        }
                    });
                });
            } catch (InterruptedException e) {
                LOGGER.info("Process interrupted, shutting down");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                LOGGER.error("Error in process loop: {}", e.getMessage(), e);
                // Continue processing even if there's an error
            }
        }
    }
}
