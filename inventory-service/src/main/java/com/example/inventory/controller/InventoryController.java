package com.example.inventory.controller;

import com.example.inventory.entity.Inventory;
import com.example.inventory.entity.InventoryReservation;
import com.example.inventory.service.InventoryService;
import com.example.inventory.repository.InventoryRepository;
import com.example.inventory.repository.InventoryReservationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;
    
    public InventoryController(InventoryService inventoryService,
                              InventoryRepository inventoryRepository,
                              InventoryReservationRepository reservationRepository) {
        this.inventoryService = inventoryService;
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
    }
    
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        logger.info("Getting all inventory");
        List<Inventory> inventory = inventoryRepository.findAll();
        return ResponseEntity.ok(inventory);
    }
    
    @GetMapping("/{productId}")
    public ResponseEntity<Inventory> getInventory(@PathVariable String productId) {
         logger.info("Getting inventory for product: {}", productId);
        return inventoryRepository.findById(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Inventory> createProduct(@RequestBody CreateProductRequest request) {
        logger.info("Creating product: {}", request.getProductName());
        Inventory inventory = inventoryService.createProduct(
            request.getProductId(),
            request.getProductName(),
            request.getInitialQuantity(),
            request.getUnitPrice()
        );
        return ResponseEntity.ok(inventory);
    }
    
    @PutMapping("/{productId}/restock")
    public ResponseEntity<String> restockInventory(@PathVariable String productId, 
                                                  @RequestBody RestockRequest request) {
        logger.info("Restocking inventory for product: {}, quantity: {}", productId, request.getQuantity());
        inventoryService.restockInventory(productId, request.getQuantity());
        return ResponseEntity.ok("Inventory restocked successfully");
    }
    
    @GetMapping("/reservations")
    public ResponseEntity<List<InventoryReservation>> getAllReservations() {
        logger.info("Getting all inventory reservations");
        List<InventoryReservation> reservations = reservationRepository.findAll();
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/reservations/order/{orderId}")
    public ResponseEntity<List<InventoryReservation>> getReservationsByOrder(@PathVariable String orderId) {
        logger.info("Getting inventory reservations for order: {}", orderId);
        List<InventoryReservation> reservations = reservationRepository.findByOrderId(orderId);
        return ResponseEntity.ok(reservations);
    }
}

// Request DTOs
class CreateProductRequest {
    private String productId;
    private String productName;
    private int initialQuantity;
    private double unitPrice;
    
    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public int getInitialQuantity() { return initialQuantity; }
    public void setInitialQuantity(int initialQuantity) { this.initialQuantity = initialQuantity; }
    
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
}

class RestockRequest {
    private int quantity;
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}