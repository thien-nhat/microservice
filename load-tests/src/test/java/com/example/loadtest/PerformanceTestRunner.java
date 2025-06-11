package com.example.loadtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceTestRunner {
    
    private static final String ORDER_SERVICE_URL = "http://localhost:8081/api/orders";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final RestTemplate restTemplate = new RestTemplate();
    
    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger errorCount = new AtomicInteger(0);
    private static final AtomicLong totalResponseTime = new AtomicLong(0);
    
    public static void main(String[] args) throws InterruptedException {
        int totalRequests = 1000;
        int concurrentThreads = 50;
        int requestsPerThread = totalRequests / concurrentThreads;
        
        System.out.println("Starting performance test...");
        System.out.println("Total requests: " + totalRequests);
        System.out.println("Concurrent threads: " + concurrentThreads);
        
        ExecutorService executor = Executors.newFixedThreadPool(concurrentThreads);
        long startTime = System.currentTimeMillis();
        
        // Submit tasks
        CompletableFuture<?>[] futures = new CompletableFuture[concurrentThreads];
        for (int i = 0; i < concurrentThreads; i++) {
            final int threadId = i;
            futures[i] = CompletableFuture.runAsync(() -> {
                for (int j = 0; j < requestsPerThread; j++) {
                    createOrderWithMetrics(threadId, j);
                    
                    // Small delay between requests
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, executor);
        }
        
        // Wait for all tasks to complete
        CompletableFuture.allOf(futures).join();
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        // Print results
        printResults(totalTime, totalRequests);
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
    
    private static void createOrderWithMetrics(int threadId, int requestId) {
        long requestStart = System.currentTimeMillis();
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            String requestBody = String.format("""
                {
                    "customerId": "customer%d_%d",
                    "productId": "product1",
                    "quantity": 1,
                    "totalAmount": 100.0
                }
                """, threadId, requestId);
            
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            restTemplate.postForObject(ORDER_SERVICE_URL, request, String.class);
            
            successCount.incrementAndGet();
            
        } catch (Exception e) {
            errorCount.incrementAndGet();
            System.err.println("Error in thread " + threadId + ", request " + requestId + ": " + e.getMessage());
        } finally {
            long requestTime = System.currentTimeMillis() - requestStart;
            totalResponseTime.addAndGet(requestTime);
        }
    }
    
    private static void printResults(long totalTime, int totalRequests) {
        System.out.println("\n=== Performance Test Results ===");
        System.out.println("Total time: " + totalTime + " ms");
        System.out.println("Total requests: " + totalRequests);
        System.out.println("Successful requests: " + successCount.get());
        System.out.println("Failed requests: " + errorCount.get());
        System.out.println("Success rate: " + (successCount.get() * 100.0 / totalRequests) + "%");
        System.out.println("Requests per second: " + (totalRequests * 1000.0 / totalTime));
        System.out.println("Average response time: " + (totalResponseTime.get() / totalRequests) + " ms");
    }
}