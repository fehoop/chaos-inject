package com.fehoop.chaos.order.controller;

import com.fehoop.chaos.order.client.InventoryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OrderController {

    private final InventoryClient inventoryClient;

    public OrderController(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    /**
     * GET /orders/check/{sku} -> hits inventory-service via the gateway.
     */
    @GetMapping("/orders/check/{sku}")
    public Map<String, Object> checkOrder(@PathVariable String sku) {
        return inventoryClient.checkStock(sku);
    }
}
