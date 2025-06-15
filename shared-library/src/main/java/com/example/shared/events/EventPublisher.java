package com.example.shared.events;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    
    public EventPublisher(JmsTemplate topicJmsTemplate, ObjectMapper objectMapper) {
        this.jmsTemplate = topicJmsTemplate;
        this.objectMapper = objectMapper;
    }
    
    public void publishEvent(String destination, DomainEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            jmsTemplate.convertAndSend(destination, eventJson);
            logger.info("Published event: {} to {}", event.getEventType(), destination);
        } catch (Exception e) {
            logger.error("Error publishing event: {}", e.getMessage());
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}