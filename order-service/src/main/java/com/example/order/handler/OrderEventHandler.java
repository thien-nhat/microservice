package com.example.order.handler;

import com.example.shared.events.AbstractEventHandler;
import com.example.shared.events.PaymentProcessedEvent;
import com.example.shared.events.InventoryReservedEvent;
import com.example.order.service.OrderService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OrderEventHandler extends AbstractEventHandler {
    
    private final OrderService orderService;
    
    public OrderEventHandler(ObjectMapper objectMapper, OrderService orderService) {
        super(objectMapper);
        this.orderService = orderService;
    }
    
    @JmsListener(destination = "payment.processed")
    public void handlePaymentProcessed(String eventJson) {
        PaymentProcessedEvent event = parseEvent(eventJson, PaymentProcessedEvent.class);
        System.out.println("Order Service received PaymentProcessedEvent: " + event.getOrderId());
        
        if (!event.isSuccess()) {
            // Payment failed -> cancel order
            orderService.cancelOrder(event.getOrderId());
        }
        // If payment success, wait for inventory confirmation
    }
    
    @JmsListener(destination = "inventory.reserved")
    public void handleInventoryReserved(String eventJson) {
        InventoryReservedEvent event = parseEvent(eventJson, InventoryReservedEvent.class);
        System.out.println("Order Service received InventoryReservedEvent: " + event.getOrderId());
        
        if (event.isSuccess()) {
            // Both payment and inventory successful -> confirm order
            orderService.confirmOrder(event.getOrderId());
        } else {
            // Inventory reservation failed -> cancel order
            orderService.cancelOrder(event.getOrderId());
        }
    }
}