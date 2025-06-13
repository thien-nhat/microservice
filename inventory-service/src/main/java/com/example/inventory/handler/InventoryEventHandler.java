package com.example.inventory.handler;

import com.example.shared.events.AbstractEventHandler;
import com.example.shared.events.OrderCreatedEvent;
import com.example.shared.events.OrderCancelledEvent;
import com.example.shared.events.OrderConfirmedEvent;
import com.example.inventory.service.InventoryService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class InventoryEventHandler extends AbstractEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryEventHandler.class);
    private final InventoryService inventoryService;
    
    public InventoryEventHandler(ObjectMapper objectMapper, InventoryService inventoryService) {
        super(objectMapper);
        this.inventoryService = inventoryService;
    }
    
    @JmsListener(destination = "order.created")
    public void handleOrderCreated(String eventJson) {
        OrderCreatedEvent event = parseEvent(eventJson, OrderCreatedEvent.class);
        logger.info("Inventory Service received OrderCreatedEvent: {}", event.getOrderId());
        
        // Reserve inventory for the order
        inventoryService.reserveInventory(
            event.getOrderId(), 
            event.getProductId(), 
            event.getQuantity()
        );
    }
    
    @JmsListener(destination = "order.confirmed")
    public void handleOrderConfirmed(String eventJson) {
        OrderConfirmedEvent event = parseEvent(eventJson, OrderConfirmedEvent.class);
        logger.info("Inventory Service received OrderConfirmedEvent: {}", event.getOrderId());
        
        // Confirm inventory reservation (actually remove from inventory)
        inventoryService.confirmInventoryReservation(
            event.getOrderId(),
            event.getProductId(),
            event.getQuantity()
        );
    }
    
    @JmsListener(destination = "order.cancelled")
    public void handleOrderCancelled(String eventJson) {
        OrderCancelledEvent event = parseEvent(eventJson, OrderCancelledEvent.class);
        logger.info("Inventory Service received OrderCancelledEvent: {}", event.getOrderId());
        
        // Release inventory reservation (compensation action)
        inventoryService.releaseInventoryReservation(
            event.getOrderId(),
            event.getProductId(),
            event.getQuantity()
        );
    }
}