package com.example.shared.events;

import org.springframework.jms.annotation.JmsListener;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractEventHandler {
    
    protected final ObjectMapper objectMapper;
    
    public AbstractEventHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    protected <T> T parseEvent(String eventJson, Class<T> eventClass) {
        try {
            return objectMapper.readValue(eventJson, eventClass);
        } catch (Exception e) {
            System.err.println("Error parsing event: " + e.getMessage());
            throw new RuntimeException("Failed to parse event", e);
        }
    }
}