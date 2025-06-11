package com.example.inventory.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_reservations")
public class InventoryReservation {
    @Id
    private String reservationId;
    
    private String orderId;
    private String productId;
    private int quantity;
    
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public InventoryReservation() {}
    
    public InventoryReservation(String reservationId, String orderId, String productId, int quantity) {
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = ReservationStatus.RESERVED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

