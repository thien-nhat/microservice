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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.Optional;

@Service
@Transactional
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;
    private final EventPublisher eventPublisher;
    
    public InventoryService(InventoryRepository inventoryRepository,
                           InventoryReservationRepository reservationRepository,
                           EventPublisher eventPublisher) {
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Reserve inventory cho một order
     * Được gọi khi nhận OrderCreatedEvent
     */
    public void reserveInventory(String orderId, String productId, int quantity) {
        System.out.println("Attempting to reserve inventory - Order: " + orderId + 
                          ", Product: " + productId + ", Quantity: " + quantity);
        
        try {
            // 1. Tìm sản phẩm trong kho
            Optional<Inventory> inventoryOpt = inventoryRepository.findById(productId);
            
            if (!inventoryOpt.isPresent()) {
                publishInventoryReservedEvent(orderId, productId, quantity, false, 
                                            "Product not found in inventory");
                return;
            }
            
            Inventory inventory = inventoryOpt.get();
            
            // 2. Kiểm tra đủ hàng không
            if (!inventory.canReserve(quantity)) {
                publishInventoryReservedEvent(orderId, productId, quantity, false, 
                                            "Insufficient inventory. Available: " + inventory.getAvailableQuantity());
                return;
            }
            
            // 3. Reserve inventory
            inventory.reserve(quantity);
            inventoryRepository.save(inventory);
            
            // 4. Tạo reservation record
            String reservationId = UUID.randomUUID().toString();
            InventoryReservation reservation = new InventoryReservation(reservationId, orderId, productId, quantity);
            reservationRepository.save(reservation);
            
            // 5. Publish success event
            publishInventoryReservedEvent(orderId, productId, quantity, true, 
                                        "Inventory reserved successfully. Reservation ID: " + reservationId);
            
            System.out.println("Inventory reserved successfully - Order: " + orderId + 
                              ", Reservation ID: " + reservationId);
            
        } catch (Exception e) {
            System.err.println("Error reserving inventory: " + e.getMessage());
            publishInventoryReservedEvent(orderId, productId, quantity, false, 
                                        "Error: " + e.getMessage());
        }
    }
    
    /**
     * Confirm inventory reservation (trừ hàng khỏi kho)
     * Được gọi khi order được confirm
     */
    public void confirmInventoryReservation(String orderId, String productId, int quantity) {
        System.out.println("Confirming inventory reservation - Order: " + orderId);
        
        try {
            // 1. Tìm reservation
            Optional<InventoryReservation> reservationOpt = 
                reservationRepository.findByOrderIdAndProductId(orderId, productId);
            
            if (!reservationOpt.isPresent()) {
                System.err.println("Reservation not found for order: " + orderId);
                return;
            }
            
            InventoryReservation reservation = reservationOpt.get();
            
            // 2. Tìm inventory
            Optional<Inventory> inventoryOpt = inventoryRepository.findById(productId);
            if (!inventoryOpt.isPresent()) {
                System.err.println("Inventory not found for product: " + productId);
                return;
            }
            
            Inventory inventory = inventoryOpt.get();
            
            // 3. Confirm reservation (trừ hàng khỏi tổng kho)
            inventory.confirmReservation(quantity);
            inventoryRepository.save(inventory);
            
            // 4. Update reservation status
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
            
            // 5. Publish confirmed event
            InventoryConfirmedEvent event = new InventoryConfirmedEvent(orderId, productId, quantity);
            eventPublisher.publishEvent("inventory.confirmed", event);
            
            System.out.println("Inventory reservation confirmed - Order: " + orderId);
            
        } catch (Exception e) {
            System.err.println("Error confirming inventory reservation: " + e.getMessage());
        }
    }
    
    /**
     * Release inventory reservation (compensation action)
     * Được gọi khi order bị cancel
     */
    public void releaseInventoryReservation(String orderId, String productId, int quantity) {
        System.out.println("Releasing inventory reservation - Order: " + orderId);
        
        try {
            // 1. Tìm reservation
            Optional<InventoryReservation> reservationOpt = 
                reservationRepository.findByOrderIdAndProductId(orderId, productId);
            
            if (!reservationOpt.isPresent()) {
                System.err.println("Reservation not found for order: " + orderId);
                return;
            }
            
            InventoryReservation reservation = reservationOpt.get();
            
            // Chỉ release nếu đang ở trạng thái RESERVED
            if (reservation.getStatus() != ReservationStatus.RESERVED) {
                System.err.println("Cannot release reservation with status: " + reservation.getStatus());
                return;
            }
            
            // 2. Tìm inventory
            Optional<Inventory> inventoryOpt = inventoryRepository.findById(productId);
            if (!inventoryOpt.isPresent()) {
                System.err.println("Inventory not found for product: " + productId);
                return;
            }
            
            Inventory inventory = inventoryOpt.get();
            
            // 3. Release inventory
            inventory.release(quantity);
            inventoryRepository.save(inventory);
            
            // 4. Update reservation status
            reservation.setStatus(ReservationStatus.RELEASED);
            reservationRepository.save(reservation);
            
            // 5. Publish released event
            InventoryReleasedEvent event = new InventoryReleasedEvent(orderId, productId, quantity);
            eventPublisher.publishEvent("inventory.released", event);
            
            System.out.println("Inventory reservation released - Order: " + orderId);
            
        } catch (Exception e) {
            System.err.println("Error releasing inventory reservation: " + e.getMessage());
        }
    }
    
    /**
     * Tạo sản phẩm mới trong kho
     */
    public Inventory createProduct(String productId, String productName, int initialQuantity, double unitPrice) {
        Inventory inventory = new Inventory(productId, productName, initialQuantity, unitPrice);
        return inventoryRepository.save(inventory);
    }
    
    /**
     * Nhập thêm hàng vào kho
     */
    public void restockInventory(String productId, int additionalQuantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(productId);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + additionalQuantity);
            inventory.setTotalQuantity(inventory.getTotalQuantity() + additionalQuantity);
            inventoryRepository.save(inventory);
            
            System.out.println("Restocked inventory - Product: " + productId + 
                              ", Added: " + additionalQuantity);
        }
    }
    
    private void publishInventoryReservedEvent(String orderId, String productId, int quantity, 
                                             boolean success, String message) {
        InventoryReservedEvent event = new InventoryReservedEvent(orderId, productId, quantity, success);
        event.setMessage(message);
        eventPublisher.publishEvent("inventory.reserved", event);
        
        System.out.println("Published InventoryReservedEvent - Order: " + orderId + 
                          ", Success: " + success + ", Message: " + message);
    }
}