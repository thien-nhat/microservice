package com.example.inventory.config;

import com.example.inventory.service.InventoryService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryDataConfig {
    
    @Bean
    public ApplicationRunner initializeInventoryData(InventoryService inventoryService) {
        return args -> {
            // Initialize some sample products
            inventoryService.createProduct("product1", "Laptop Dell XPS 13", 100, 1000.0);
            inventoryService.createProduct("product2", "iPhone 15 Pro", 50, 1200.0);
            inventoryService.createProduct("product3", "Samsung Galaxy S24", 0, 800.0); // Out of stock
            inventoryService.createProduct("product4", "MacBook Pro M3", 25, 2500.0);
            inventoryService.createProduct("product5", "iPad Air", 75, 600.0);
            
            System.out.println("Inventory data initialized successfully!");
        };
    }
}