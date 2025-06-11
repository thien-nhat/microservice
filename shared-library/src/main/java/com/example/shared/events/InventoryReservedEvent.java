package com.example.shared.events;

public class InventoryReservedEvent extends DomainEvent {
    private String orderId;
    private String productId;
    private int quantity;
    private boolean success;
    private String message;  // Additional field for error messages
    
    public InventoryReservedEvent() {}
    
    public InventoryReservedEvent(String orderId, String productId, int quantity, boolean success) {
        super();
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.success = success;
    }
    
    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}