package com.example.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class SagaEndToEndTest {
    
    @LocalServerPort
    private int port;
    
    private final TestRestTemplate restTemplate = new TestRestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    void testCompleteOrderFlow() throws Exception {
        // Given
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String orderRequest = """
            {
                "customerId": "customer1",
                "productId": "product1",
                "quantity": 2,
                "totalAmount": 200.0
            }
            """;
        
        // When - Create order
        HttpEntity<String> request = new HttpEntity<>(orderRequest, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/api/orders", request, String.class);
        
        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        // Extract order ID from response
        var orderResponse = objectMapper.readTree(response.getBody());
        String orderId = orderResponse.get("orderId").asText();
        assertNotNull(orderId);
        
        // Wait and verify saga completion
        Thread.sleep(3000); // Wait for async processing
        
        // Check order status endpoint (you'd need to implement this)
        ResponseEntity<String> statusResponse = restTemplate.getForEntity(
            baseUrl + "/api/orders/" + orderId + "/status", String.class);
        
        assertEquals(200, statusResponse.getStatusCodeValue());
    }
}