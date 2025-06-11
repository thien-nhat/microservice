package com.example.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication(scanBasePackages = {
    "com.example.payment",    // Scan payment service packages
    "com.example.shared"    // Scan shared library packages
})
@EnableJms
public class PaymentServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(PaymentServiceApplication.class, args);
  }

}
