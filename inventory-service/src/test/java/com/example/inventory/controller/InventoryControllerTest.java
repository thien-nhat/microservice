package com.example.inventory.controller;

import com.example.inventory.entity.Inventory;
import com.example.inventory.entity.InventoryReservation;
import com.example.inventory.service.InventoryService;
import com.example.inventory.repository.InventoryRepository;
import com.example.inventory.repository.InventoryReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryReservationRepository reservationRepository;

    @InjectMocks
    private InventoryController inventoryController;

    @Test
    void getAllInventory_shouldReturnListOfInventory() {
        // Arrange
        Inventory inventory1 = new Inventory("1", "Product 1", 10, 20.0);
        Inventory inventory2 = new Inventory("2", "Product 2", 5, 10.0);
        List<Inventory> inventoryList = Arrays.asList(inventory1, inventory2);
        when(inventoryRepository.findAll()).thenReturn(inventoryList);

        // Act
        ResponseEntity<List<Inventory>> response = inventoryController.getAllInventory();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inventoryList, response.getBody());
    }

    @Test
    void getInventory_shouldReturnInventoryForGivenProductId() {
        // Arrange
        String productId = "1";
        Inventory inventory = new Inventory(productId, "Product 1", 10, 20.0);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));

        // Act
        ResponseEntity<Inventory> response = inventoryController.getInventory(productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inventory, response.getBody());
    }

    @Test
    void getInventory_shouldReturnNotFoundForNonExistingProductId() {
        // Arrange
        String productId = "1";
        when(inventoryRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Inventory> response = inventoryController.getInventory(productId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createProduct_shouldCreateNewProduct() {
        // Arrange
        CreateProductRequest request = new CreateProductRequest();
        request.setProductId("1");
        request.setProductName("Product 1");
        request.setInitialQuantity(10);
        request.setUnitPrice(20.0);
        Inventory inventory = new Inventory("1", "Product 1", 10, 20.0);
        when(inventoryService.createProduct(request.getProductId(), request.getProductName(), request.getInitialQuantity(), request.getUnitPrice())).thenReturn(inventory);

        // Act
        ResponseEntity<Inventory> response = inventoryController.createProduct(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inventory, response.getBody());
    }

    @Test
    void restockInventory_shouldRestockExistingProduct() {
        // Arrange
        String productId = "1";
        RestockRequest request = new RestockRequest();
        request.setQuantity(5);

        // Act
        ResponseEntity<String> response = inventoryController.restockInventory(productId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Inventory restocked successfully", response.getBody());
        verify(inventoryService, times(1)).restockInventory(productId, request.getQuantity());
    }

    @Test
    void getAllReservations_shouldReturnListOfReservations() {
        // Arrange
        InventoryReservation reservation1 = new InventoryReservation();
        InventoryReservation reservation2 = new InventoryReservation();
        List<InventoryReservation> reservationList = Arrays.asList(reservation1, reservation2);
        when(reservationRepository.findAll()).thenReturn(reservationList);

        // Act
        ResponseEntity<List<InventoryReservation>> response = inventoryController.getAllReservations();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservationList, response.getBody());
    }

    @Test
    void getReservationsByOrder_shouldReturnListOfReservationsForGivenOrderId() {
        // Arrange
        String orderId = "123";
        InventoryReservation reservation1 = new InventoryReservation();
        InventoryReservation reservation2 = new InventoryReservation();
        List<InventoryReservation> reservationList = Arrays.asList(reservation1, reservation2);
        when(reservationRepository.findByOrderId(orderId)).thenReturn(reservationList);

        // Act
        ResponseEntity<List<InventoryReservation>> response = inventoryController.getReservationsByOrder(orderId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservationList, response.getBody());
    }
}