package com.example.shared.events;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class EventPublisher {
    
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    
    public EventPublisher(JmsTemplate jmsTemplate, ObjectMapper objectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
    }
    
    public void publishEvent(String destination, DomainEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            jmsTemplate.convertAndSend(destination, eventJson);
            System.out.println("Published event: " + event.getEventType() + " to " + destination);
        } catch (Exception e) {
            System.err.println("Error publishing event: " + e.getMessage());
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}