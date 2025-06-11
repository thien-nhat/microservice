package com.example.inventory.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    private String productId;
    
    private String productName;
    private int availableQuantity;    // Số lượng có sẵn
    private int reservedQuantity;     // Số lượng đã reserve
    private int totalQuantity;        // Tổng số lượng = available + reserved
    private double unitPrice;         // Giá đơn vị
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Inventory() {}
    
    public Inventory(String productId, String productName, int totalQuantity, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity;
        this.reservedQuantity = 0;
        this.unitPrice = unitPrice;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean canReserve(int quantity) {
        return availableQuantity >= quantity;
    }
    
    public void reserve(int quantity) {
        if (!canReserve(quantity)) {
            throw new IllegalStateException("Insufficient inventory for product: " + productId);
        }
        this.availableQuantity -= quantity;
        this.reservedQuantity += quantity;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void release(int quantity) {
        if (reservedQuantity < quantity) {
            throw new IllegalStateException("Cannot release more than reserved quantity");
        }
        this.reservedQuantity -= quantity;
        this.availableQuantity += quantity;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void confirmReservation(int quantity) {
        if (reservedQuantity < quantity) {
            throw new IllegalStateException("Cannot confirm more than reserved quantity");
        }
        this.reservedQuantity -= quantity;
        this.totalQuantity -= quantity;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { 
        this.availableQuantity = availableQuantity;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(int reservedQuantity) { 
        this.reservedQuantity = reservedQuantity;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
    
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "Inventory{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", availableQuantity=" + availableQuantity +
                ", reservedQuantity=" + reservedQuantity +
                ", totalQuantity=" + totalQuantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}