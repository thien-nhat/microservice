package com.example.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication(scanBasePackages = {
    "com.example.inventory",    // Scan payment service packages
    "com.example.shared"    // Scan shared library packages
})
@EnableJms
public class InventoryServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(InventoryServiceApplication.class, args);
  }

}
