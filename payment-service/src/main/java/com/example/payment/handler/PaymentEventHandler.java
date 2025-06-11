package com.example.payment.handler;

import com.example.shared.events.AbstractEventHandler;
import com.example.shared.events.OrderCreatedEvent;
import com.example.payment.service.PaymentService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PaymentEventHandler extends AbstractEventHandler {
    
    private final PaymentService paymentService;
    
    public PaymentEventHandler(ObjectMapper objectMapper, PaymentService paymentService) {
        super(objectMapper);
        this.paymentService = paymentService;
    }
    
    @JmsListener(destination = "order.created")
    public void handleOrderCreated(String eventJson) {
        OrderCreatedEvent event = parseEvent(eventJson, OrderCreatedEvent.class);
        System.out.println("Payment Service received OrderCreatedEvent: " + event.getOrderId());
        
        // Process payment for the order
        paymentService.processPayment(event.getOrderId(), event.getTotalAmount());
    }
}