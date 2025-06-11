package com.example.payment.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    private String paymentId;
    
    private String orderId;
    private double amount;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    public Payment() {}
    
    public Payment(String paymentId, String orderId, double amount) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }
    
    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
}

