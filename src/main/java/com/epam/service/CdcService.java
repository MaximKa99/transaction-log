package com.epam.service;

import com.epam.entity.Formula;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.engine.ChangeEvent;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CdcService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CdcService.class);
    
    private final SubscriptionManager subscriptionManager;
    private final ObjectMapper objectMapper;
    
    public CdcService(SubscriptionManager subscriptionManager, ObjectMapper objectMapper) {
        this.subscriptionManager = subscriptionManager;
        this.objectMapper = objectMapper;
    }
    
    public void handleChangeEvent(ChangeEvent<SourceRecord, SourceRecord> changeEvent) {
        SourceRecord sourceRecord = changeEvent.value();
        String topic = sourceRecord.topic();
        
        LOGGER.debug("Received CDC event from topic: {}", topic);
        
        Struct sourceRecordValue = (Struct) sourceRecord.value();
        if (sourceRecordValue == null) {
            LOGGER.debug("Received tombstone event, ignoring");
            return;
        }
        
        String op = sourceRecordValue.getString("op");
        LOGGER.debug("CDC operation: {}", op);
        
        Struct after = sourceRecordValue.getStruct("after");
        Struct before = sourceRecordValue.getStruct("before");
        
        try {
            switch (op) {
                case "c" -> {
                    // Create operation - no action needed, new subscriptions will use the new formula
                    LOGGER.debug("Formula created via CDC, no action needed");
                }
                case "u" -> {
                    // Update operation
                    if (after != null) {
                        Formula updatedFormula = extractFormula(after);
                        LOGGER.info("Formula updated via CDC: ID={}, expression={}", 
                                updatedFormula.getId(), updatedFormula.getExpression());
                        subscriptionManager.updateCalculatorForFormula(updatedFormula.getId(), updatedFormula);
                    }
                }
                case "d" -> {
                    // Delete operation
                    if (before != null) {
                        Long formulaId = before.getInt64("id");
                        LOGGER.info("Formula deleted via CDC: ID={}", formulaId);
                        subscriptionManager.removeCalculatorForFormula(formulaId);
                    }
                }
                default -> LOGGER.warn("Unknown CDC operation: {}", op);
            }
        } catch (Exception e) {
            LOGGER.error("Error processing CDC event: {}", e.getMessage(), e);
        }
    }
    
    private Formula extractFormula(Struct struct) {
        Long id = struct.getInt64("id");
        String expression = struct.getString("expression");
        
        // Variables are stored as JSON array in PostgreSQL
        Set<String> variables = Set.of();
        try {
            Object variablesObj = struct.get("variables");
            if (variablesObj != null) {
                if (variablesObj instanceof String variablesJson) {
                    // If it's a JSON string, parse it
                    JsonNode jsonNode = objectMapper.readTree(variablesJson);
                    if (jsonNode.isArray()) {
                        List<String> varList = new ArrayList<>();
                        for (JsonNode node : jsonNode) {
                            varList.add(node.asText());
                        }
                        variables = Set.copyOf(varList);
                    }
                } else if (variablesObj instanceof List) {
                    // If it's already a list
                    @SuppressWarnings("unchecked")
                    List<String> varList = (List<String>) variablesObj;
                    variables = Set.copyOf(varList);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to parse variables from CDC event: {}", e.getMessage());
            variables = Set.of();
        }
        
        return new Formula(id, expression, variables);
    }
}

