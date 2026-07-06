package com.fehoop.chaos.inventory.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class InventoryController {

    @GetMapping("/stock/{sku}")
    public Map<String, Object> checkStock(@PathVariable String sku) {
        boolean available = ThreadLocalRandom.current().nextInt(10) > 1; // 80% in stock
        return Map.of("sku", sku, "available", available);
    }
}
