package com.example.order.handler;

import com.example.shared.events.AbstractEventHandler;
import com.example.shared.events.PaymentProcessedEvent;
import com.example.shared.events.InventoryReservedEvent;
import com.example.order.service.OrderService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class OrderEventHandler extends AbstractEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderEventHandler.class);
    private final OrderService orderService;
    
    public OrderEventHandler(ObjectMapper objectMapper, OrderService orderService) {
        super(objectMapper);
        this.orderService = orderService;
    }
    
    @JmsListener(destination = "payment.processed", containerFactory = "topicListenerContainerFactory")
    public void handlePaymentProcessed(String eventJson) {
        PaymentProcessedEvent event = parseEvent(eventJson, PaymentProcessedEvent.class);
        logger.info("Order Service received PaymentProcessedEvent: {}", event.getOrderId());
        
        if (!event.isSuccess()) {
            // Payment failed -> cancel order
            orderService.cancelOrder(event.getOrderId());
        }
        // If payment success, wait for inventory confirmation
    }
    
    @JmsListener(destination = "inventory.reserved", containerFactory = "topicListenerContainerFactory")
    public void handleInventoryReserved(String eventJson) {
        InventoryReservedEvent event = parseEvent(eventJson, InventoryReservedEvent.class);
        logger.info("Order Service received InventoryReservedEvent: {}", event.getOrderId());
        
        if (event.isSuccess()) {
            // Both payment and inventory successful -> confirm order
            orderService.confirmOrder(event.getOrderId());
        } else {
            // Inventory reservation failed -> cancel order
            orderService.cancelOrder(event.getOrderId());
        }
    }
}