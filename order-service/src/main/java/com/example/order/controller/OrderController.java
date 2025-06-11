package com.example.order.controller;

import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import com.example.shared.dto.CreateOrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(
            request.getCustomerId(),
            request.getProductId(),
            request.getQuantity(),
            request.getTotalAmount()
        );
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        Order order = orderService.getOrder(orderId);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }
}