package com.example.integration;

import com.example.events.OrderCreatedEvent;
import com.example.events.EventPublisher;
import com.example.saga.SagaState;
import com.example.saga.SagaStatus;
import com.example.saga.service.SagaStateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = IntegrationTestConfig.class)
@Testcontainers
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb"
})
public class SagaIntegrationTest {
    
    @Container
    static GenericContainer<?> activemqContainer = new GenericContainer<>("rmohr/activemq:5.15.9")
            .withExposedPorts(61616);
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.activemq.broker-url", 
            () -> "tcp://localhost:" + activemqContainer.getMappedPort(61616));
    }
    
    @Autowired
    private EventPublisher eventPublisher;
    
    @Autowired
    private SagaStateService sagaStateService;
    
    @BeforeEach
    void setUp() {
        // Clean up any existing saga state
    }
    
    @Test
    void testSuccessfulSagaFlow() {
        // Given
        String orderId = UUID.randomUUID().toString();
        String customerId = "customer1";
        String productId = "product1";
        int quantity = 5;
        double totalAmount = 500.0;
        
        // Create saga state
        SagaState sagaState = sagaStateService.createSagaState(orderId);
        assertNotNull(sagaState);
        assertEquals(SagaStatus.STARTED, sagaState.getStatus());
        
        // When - Publish OrderCreatedEvent
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, customerId, totalAmount, productId, quantity);
        eventPublisher.publishEvent("order.created", event);
        
        // Then - Wait for saga completion
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    SagaState updatedState = sagaStateService.findByOrderId(orderId);
                    assertNotNull(updatedState);
                    assertTrue(updatedState.isPaymentCompleted());
                    assertTrue(updatedState.isInventoryReserved());
                    assertEquals(SagaStatus.COMPLETED, updatedState.getStatus());
                });
    }
    
    @Test
    void testFailedPaymentCompensation() {
        // Given
        String orderId = UUID.randomUUID().toString();
        String customerId = "customer1";
        String productId = "product1";
        int quantity = 5;
        double totalAmount = 1500.0; // This will fail payment
        
        // Create saga state
        SagaState sagaState = sagaStateService.createSagaState(orderId);
        
        // When - Publish OrderCreatedEvent with high amount
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, customerId, totalAmount, productId, quantity);
        eventPublisher.publishEvent("order.created", event);
        
        // Then - Wait for compensation
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    SagaState updatedState = sagaStateService.findByOrderId(orderId);
                    assertNotNull(updatedState);
                    assertEquals(SagaStatus.COMPENSATING, updatedState.getStatus());
                });
    }
    
    @Test
    void testOutOfStockCompensation() {
        // Given
        String orderId = UUID.randomUUID().toString();
        String customerId = "customer1";
        String productId = "product3"; // Out of stock product
        int quantity = 5;
        double totalAmount = 500.0;
        
        // Create saga state
        SagaState sagaState = sagaStateService.createSagaState(orderId);
        
        // When - Publish OrderCreatedEvent with out-of-stock product
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, customerId, totalAmount, productId, quantity);
        eventPublisher.publishEvent("order.created", event);
        
        // Then - Wait for compensation
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    SagaState updatedState = sagaStateService.findByOrderId(orderId);
                    assertNotNull(updatedState);
                    assertEquals(SagaStatus.COMPENSATING, updatedState.getStatus());
                });
    }
}