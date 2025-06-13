package com.example.inventory.handler;

import com.example.inventory.service.InventoryService;
import com.example.shared.events.OrderCreatedEvent;
import com.example.shared.events.OrderCancelledEvent;
import com.example.shared.events.OrderConfirmedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryEventHandlerTest {

    @Mock
    private InventoryService inventoryService;

    // @Mock
    // private ObjectMapper objectMapper;

    @InjectMocks
    private InventoryEventHandler handler;

    private ObjectMapper realObjectMapper;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this);
        realObjectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(handler, "objectMapper", realObjectMapper);

    }

    @Test
    void handleOrderCreated_shouldReserveInventory()  throws Exception {
        // Given
        String orderId = "order123";
        String productId = "product456";
        int quantity = 2;
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(orderId);
        event.setProductId(productId);
        event.setQuantity(quantity);
        String json = realObjectMapper.writeValueAsString(event);
        // When
        handler.handleOrderCreated(json);
        // Then
        verify(inventoryService).reserveInventory(orderId, productId, quantity);
    }

    @Test
    void handleOrderCreated_badJsonFormat()  throws Exception {
        // Given
        String json = "bad-format";
        // When
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            handler.handleOrderCreated(json);
        });
        assertTrue(ex.getMessage().contains("Failed to parse event"));
        // Then
        verify(inventoryService, never()).reserveInventory(anyString(), anyString(), anyInt());
    }

    @Test
    void handleOrderConfirmed_shouldConfirmInventoryReservation() throws Exception {
        // Given
        String orderId = "order123";
        String productId = "product456";
        int quantity = 2;
        OrderConfirmedEvent event = new OrderConfirmedEvent();
        event.setOrderId(orderId);
        event.setProductId(productId);
        event.setQuantity(quantity);
        String json = realObjectMapper.writeValueAsString(event);
        // When
        handler.handleOrderConfirmed(json);
        // Then
        verify(inventoryService).confirmInventoryReservation(orderId, productId, quantity);
    }

    @Test
    void handleOrderCancelled_shouldReleaseInventoryReservation() throws Exception {
        String orderId = "order123";
        String productId = "product456";
        int quantity = 2;
        OrderCancelledEvent event = new OrderCancelledEvent();
        event.setOrderId(orderId);
        event.setProductId(productId);
        event.setQuantity(quantity);
        String json = realObjectMapper.writeValueAsString(event);

        handler.handleOrderCancelled(json);

        verify(inventoryService).releaseInventoryReservation(orderId, productId, quantity);
    }
}
