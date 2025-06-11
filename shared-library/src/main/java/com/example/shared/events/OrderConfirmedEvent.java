package com.example.shared.events;

public class OrderConfirmedEvent extends DomainEvent {
    private String orderId;
    private String customerId;
    private double totalAmount;
    private String productId;
    private int quantity;
    
    public OrderConfirmedEvent() {}
    
    public OrderConfirmedEvent(String orderId, String customerId, double totalAmount,
                             String productId, int quantity) {
        super();
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.productId = productId;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
