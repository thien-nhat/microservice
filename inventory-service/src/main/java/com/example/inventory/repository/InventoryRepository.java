package com.example.inventory.repository;

import com.example.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {
    
    @Query("SELECT i FROM Inventory i WHERE i.availableQuantity > 0")
    List<Inventory> findAvailableProducts();
    
    @Query("SELECT i FROM Inventory i WHERE i.availableQuantity >= :quantity")
    List<Inventory> findProductsWithSufficientStock(int quantity);
}