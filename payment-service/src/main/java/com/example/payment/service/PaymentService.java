package com.example.payment.service;

import com.example.payment.entity.Payment;
import com.example.payment.entity.PaymentStatus;

import com.example.payment.repository.PaymentRepository;
import com.example.shared.events.EventPublisher;
import com.example.shared.events.PaymentProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final EventPublisher eventPublisher;
    
    public PaymentService(PaymentRepository paymentRepository, EventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
    }
    
    public void processPayment(String orderId, double amount) {
        logger.info("Processing payment for orderId: {}, amount: {}", orderId, amount);
        // 1. Tạo payment record
        String paymentId = UUID.randomUUID().toString();
        Payment payment = new Payment(paymentId, orderId, amount);
        
        // 2. Simulate payment processing
        boolean paymentSuccess = simulatePaymentProcessing(amount);
        
        // 3. Update payment status
        payment.setStatus(paymentSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        paymentRepository.save(payment);
        
        // 4. Publish PaymentProcessedEvent
        PaymentProcessedEvent event = new PaymentProcessedEvent(
            orderId, paymentId, amount, paymentSuccess
        );
        eventPublisher.publishEvent("payment.processed", event);
        
        logger.info("PaymentProcessedEvent published for orderId: {}, paymentId: {}, status: {}", orderId, paymentId, payment.getStatus());
    }
    
    private boolean simulatePaymentProcessing(double amount) {
        logger.debug("Simulating payment processing for amount: {}", amount);
        // Simulate payment processing logic
        // For demo: payments > 1000 will fail
        return amount <= 1000;
    }
}