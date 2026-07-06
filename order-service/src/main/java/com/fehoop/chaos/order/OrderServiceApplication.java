package com.fehoop.chaos.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    public RestClient restClient() {
        // Points at the CHAOS GATEWAY(port 8080), not directly at inventory-service
        return RestClient.builder()
                .baseUrl("http://localhost:8080/inventory")
                .build();
    }
}
