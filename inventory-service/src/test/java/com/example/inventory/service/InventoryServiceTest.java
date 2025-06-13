package com.example.inventory.service;

import com.example.inventory.entity.Inventory;
import com.example.inventory.entity.InventoryReservation;
import com.example.inventory.entity.ReservationStatus;
import com.example.inventory.repository.InventoryRepository;
import com.example.inventory.repository.InventoryReservationRepository;
import com.example.shared.events.EventPublisher;
import com.example.shared.events.InventoryReservedEvent;
import com.example.shared.events.InventoryReleasedEvent;
import com.example.shared.events.InventoryConfirmedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceTest.class);

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryReservationRepository reservationRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this);
    }

    @Test
    void reserveInventory_success() {
        String orderId = "order123";
        String productId = "product123";
        int quantity = 5;
        Inventory inventory = new Inventory(productId, "Product 123", 10, 20.0);

        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        inventoryService.reserveInventory(orderId, productId, quantity);

        verify(inventoryRepository).save(inventory);
        verify(reservationRepository).save(any(InventoryReservation.class));

        ArgumentCaptor<InventoryReservedEvent> eventCaptor = ArgumentCaptor.forClass(InventoryReservedEvent.class);
        verify(eventPublisher).publishEvent(eq("inventory.reserved"), eventCaptor.capture());
        InventoryReservedEvent event = eventCaptor.getValue();
        assertTrue(event.isSuccess());
        logger.info("Event message: {}", event.getMessage());
    }

    @Test
    void reserveInventory_productNotFound() {
        String orderId = "order123";
        String productId = "product123";
        int quantity = 5;

        when(inventoryRepository.findById(productId)).thenReturn(Optional.empty());
        inventoryService.reserveInventory(orderId, productId, quantity);

        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(reservationRepository, never()).save(any(InventoryReservation.class));

        ArgumentCaptor<InventoryReservedEvent> eventCaptor = ArgumentCaptor.forClass(InventoryReservedEvent.class);
        verify(eventPublisher).publishEvent(eq("inventory.reserved"), eventCaptor.capture());
        InventoryReservedEvent event = eventCaptor.getValue();
        assertFalse(event.isSuccess());
        logger.info("Event message: {}", event.getMessage());
    }

    @Test
    void reserveInventory_insufficientInventory() {
        String orderId = "order123";
        String productId = "product123";
        int quantity = 15;
        Inventory inventory = new Inventory(productId, "Product 123", 10, 20.0);

        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        inventoryService.reserveInventory(orderId, productId, quantity);

        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(reservationRepository, never()).save(any(InventoryReservation.class));

        ArgumentCaptor<InventoryReservedEvent> eventCaptor = ArgumentCaptor.forClass(InventoryReservedEvent.class);
        verify(eventPublisher).publishEvent(eq("inventory.reserved"), eventCaptor.capture());
        InventoryReservedEvent event = eventCaptor.getValue();
        assertFalse(event.isSuccess());
        logger.info("Event message: {}", event.getMessage());
    }

    @Test
    void confirmInventoryReservation_setReservedQuantity_success() {
        String orderId = "order123";
        String productId = "product123";
        int quantity = 5;
        InventoryReservation reservation = new InventoryReservation("reservation123", orderId, productId, quantity);
        Inventory inventory = new Inventory(productId, "Product 123", 10, 20.0);
        inventory.setReservedQuantity(quantity);
        when(reservationRepository.findByOrderIdAndProductId(orderId, productId)).thenReturn(Optional.of(reservation));
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));

        inventoryService.confirmInventoryReservation(orderId, productId, quantity);

        verify(inventoryRepository).save(inventory);
        verify(reservationRepository).save(reservation);
        verify(eventPublisher).publishEvent(eq("inventory.confirmed"), any(InventoryConfirmedEvent.class));
    }

    @Test
    void confirmInventoryReservation_reservationNotFound() {
        String orderId = "order123";
        String productId = "product123";
        int quantity = 5;

        when(reservationRepository.findByOrderIdAndProductId(orderId, productId)).thenReturn(Optional.empty());

        inventoryService.confirmInventoryReservation(orderId, productId, quantity);

        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(reservationRepository, never()).save(any(InventoryReservation.class));
        verify(eventPublisher, never()).publishEvent(eq("inventory.confirmed"), any(InventoryConfirmedEvent.class));
    }

    @Test
    void confirmInventoryReservation_inventoryNotFound() {
        String orderId = "order123";
        String productId = "product123";
        int quantity = 5;
        InventoryReservation reservation = new InventoryReservation("reservation123", orderId, productId, quantity);

        when(reservationRepository.findByOrderIdAndProductId(orderId, productId)).thenReturn(Optional.of(reservation));
        when(inventoryRepository.findById(productId)).thenReturn(Optional.empty());

        inventoryService.confirmInventoryReservation(orderId, productId, quantity);

        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(reservationRepository, never()).save(reservation);
        verify(eventPublisher, never()).publishEvent(eq("inventory.confirmed"), any(InventoryConfirmedEvent.class));
    }

    @Test
    void releaseInventoryReservation_setReservedQuantity_success() {
        String orderId = "order123";
        String productId = "product123";
        int quantity = 5;
        InventoryReservation reservation = new InventoryReservation("reservation123", orderId, productId, quantity);
        reservation.setStatus(ReservationStatus.RESERVED);
        Inventory inventory = new Inventory(productId, "Product 123", 10, 20.0);
        inventory.setReservedQuantity(quantity);

        when(reservationRepository.findByOrderIdAndProductId(orderId, productId)).thenReturn(Optional.of(reservation));
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));

        inventoryService.releaseInventoryReservation(orderId, productId, quantity);

        verify(inventoryRepository).save(inventory);
        verify(reservationRepository).save(reservation);
        verify(eventPublisher).publishEvent(eq("inventory.released"), any(InventoryReleasedEvent.class));
    }

    @Test
    void releaseInventoryReservation_reservationNotFound() {
        String orderId = "order123";
        String productId = "product123";
        int quantity = 5;

        when(reservationRepository.findByOrderIdAndProductId(orderId, productId)).thenReturn(Optional.empty());

        inventoryService.releaseInventoryReservation(orderId, productId, quantity);

        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(reservationRepository, never()).save(any(InventoryReservation.class));
        verify(eventPublisher, never()).publishEvent(eq("inventory.released"), any(InventoryReleasedEvent.class));
    }

    @Test
    void releaseInventoryReservation_inventoryNotFound() {
        String orderId = "order123";
        String productId = "product123";
        int quantity = 5;
        InventoryReservation reservation = new InventoryReservation("reservation123", orderId, productId, quantity);
        reservation.setStatus(ReservationStatus.RESERVED);

        when(reservationRepository.findByOrderIdAndProductId(orderId, productId)).thenReturn(Optional.of(reservation));
        when(inventoryRepository.findById(productId)).thenReturn(Optional.empty());

        inventoryService.releaseInventoryReservation(orderId, productId, quantity);

        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(reservationRepository, never()).save(reservation);
        verify(eventPublisher, never()).publishEvent(eq("inventory.released"), any(InventoryReleasedEvent.class));
    }

    @Test
    void releaseInventoryReservation_reservationNotReserved() {
        String orderId = "order123";
        String productId = "product123";
        int quantity = 5;
        InventoryReservation reservation = new InventoryReservation("reservation123", orderId, productId, quantity);
        reservation.setStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findByOrderIdAndProductId(orderId, productId)).thenReturn(Optional.of(reservation));

        inventoryService.releaseInventoryReservation(orderId, productId, quantity);

        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(reservationRepository, never()).save(reservation);
        verify(eventPublisher, never()).publishEvent(eq("inventory.released"), any(InventoryReleasedEvent.class));
    }

    @Test
    void createProduct_success() {
        String productId = "product123";
        String productName = "Product 123";
        int initialQuantity = 10;
        double unitPrice = 20.0;

        Inventory inventory = new Inventory(productId, productName, initialQuantity, unitPrice);

        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        Inventory createdInventory = inventoryService.createProduct(productId, productName, initialQuantity, unitPrice);

        verify(inventoryRepository).save(any(Inventory.class));
        assertEquals(productId, createdInventory.getProductId());
        assertEquals(productName, createdInventory.getProductName());
        assertEquals(initialQuantity, createdInventory.getAvailableQuantity());
        assertEquals(unitPrice, createdInventory.getUnitPrice());
    }

    @Test
    void restockInventory_success() {
        String productId = "product123";
        int additionalQuantity = 5;
        Inventory inventory = new Inventory(productId, "Product 123", 10, 20.0);

        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));

        inventoryService.restockInventory(productId, additionalQuantity);

        verify(inventoryRepository).save(inventory);
        assertEquals(15, inventory.getAvailableQuantity());
        assertEquals(15, inventory.getTotalQuantity());
    }
}
