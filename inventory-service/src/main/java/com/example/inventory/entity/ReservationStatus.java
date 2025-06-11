package com.example.inventory.entity;

public enum ReservationStatus {
    RESERVED,    // Đã reserve thành công
    CONFIRMED,   // Đã confirm (trừ khỏi kho)
    RELEASED     // Đã release (trả lại kho)
}
