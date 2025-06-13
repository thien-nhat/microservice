package com.example.shared.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractEventHandler.class);

    protected final ObjectMapper objectMapper;

    public AbstractEventHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected <T> T parseEvent(String eventJson, Class<T> eventClass) {
        try {
            logger.info("Parsing event: {}", eventClass.getSimpleName());
            return objectMapper.readValue(eventJson, eventClass);
        } catch (Exception e) {
            logger.error("Error parsing event: {}", eventClass.getSimpleName(), e);
            throw new RuntimeException("Failed to parse event", e);
        }
    }
}