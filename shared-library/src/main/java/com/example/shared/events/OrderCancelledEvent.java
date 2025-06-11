package com.example.shared.events;

public class OrderCancelledEvent extends DomainEvent {
    private String orderId;
    private String customerId;
    private double totalAmount;
    private String productId;
    private int quantity;
    private String reason;
    
    public OrderCancelledEvent() {}
    
    public OrderCancelledEvent(String orderId, String customerId, double totalAmount,
                             String productId, int quantity, String reason) {
        super();
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.productId = productId;
        this.quantity = quantity;
        this.reason = reason;
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
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
