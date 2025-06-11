package com.example.shared.constants;

public class EventDestinations {
    public static final String ORDER_CREATED = "order.created";
    public static final String PAYMENT_PROCESSED = "payment.processed";
    public static final String INVENTORY_RESERVED = "inventory.reserved";
    public static final String ORDER_CONFIRMED = "order.confirmed";
    public static final String ORDER_CANCELLED = "order.cancelled";
    
    private EventDestinations() {
        // Utility class
    }
}