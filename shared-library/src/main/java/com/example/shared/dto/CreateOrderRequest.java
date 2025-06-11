package com.example.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class CreateOrderRequest {
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotBlank(message = "Product ID is required")
    private String productId;
    
    @Positive(message = "Quantity must be positive")
    private int quantity;
    
    @Positive(message = "Total amount must be positive")
    private double totalAmount;
    
    // Default constructor
    public CreateOrderRequest() {}
    
    // Constructor with all fields
    public CreateOrderRequest(String customerId, String productId, int quantity, double totalAmount) {
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }
    
    // Getters and Setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}