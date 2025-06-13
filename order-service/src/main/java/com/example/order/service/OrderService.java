package com.example.order.service;

import com.example.order.entity.Order;
import com.example.order.entity.OrderStatus;
import com.example.order.repository.OrderRepository;
import com.example.shared.events.EventPublisher;
import com.example.shared.events.OrderCreatedEvent;
import org.springframework.stereotype.Service;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;
    
    public OrderService(OrderRepository orderRepository, EventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }
    public Order getOrder(String orderId) {
        logger.info("Getting order with id: {}", orderId);
        return orderRepository.getReferenceById(orderId);
    }
    public Order createOrder(String customerId, String productId, int quantity, double totalAmount) {
        // 1. Tạo order mới
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, customerId, productId, quantity, totalAmount);
        
        // 2. Lưu vào database
        Order savedOrder = orderRepository.save(order);
        
        // 3. Publish OrderCreatedEvent
        OrderCreatedEvent event = new OrderCreatedEvent(
            orderId, customerId, totalAmount, productId, quantity
        );
        eventPublisher.publishEvent("order.created", event);
        
        logger.info("Order created: {}", orderId);
        return savedOrder;
    }
    
    public void confirmOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            logger.info("Order confirmed: {}", orderId);
        }
    }
    
    public void cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            logger.info("Order cancelled: {}", orderId);
        }
    }
}
