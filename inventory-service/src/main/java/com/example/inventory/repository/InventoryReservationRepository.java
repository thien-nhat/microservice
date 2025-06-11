package com.example.inventory.repository;

import com.example.inventory.entity.InventoryReservation;
import com.example.inventory.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, String> {
    
    List<InventoryReservation> findByOrderId(String orderId);
    
    List<InventoryReservation> findByProductId(String productId);
    
    List<InventoryReservation> findByStatus(ReservationStatus status);
    
    Optional<InventoryReservation> findByOrderIdAndProductId(String orderId, String productId);
}
