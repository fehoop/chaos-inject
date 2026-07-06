package com.fehoop.chaos.order.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Calls inventory-service through the chaos-gateway.
 */
@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackCheckStock")
    @Retry(name = "inventoryService")
    public Map<String, Object> checkStock(String sku) {
        return restClient.get()
                .uri("/stock/{sku}", sku)
                .retrieve()
                .body(Map.class);
    }

    // Fallback signature must mirror the original method + a Throwable param.
    private Map<String, Object> fallbackCheckStock(String sku, Throwable t) {
        return Map.of(
                "sku", sku,
                "available", false,
                "reason", "inventory-service unavailable, degraded response: " + t.getClass().getSimpleName());
    }
}
