package com.example.shared.events;

public class InventoryConfirmedEvent extends DomainEvent {
    private String orderId;
    private String productId;
    private int quantity;
    
    public InventoryConfirmedEvent() {}
    
    public InventoryConfirmedEvent(String orderId, String productId, int quantity) {
        super();
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}