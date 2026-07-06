package com.fehoop.chaos.gateway.filter;

import com.fehoop.chaos.gateway.config.ChaosProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A GlobalFilter that runs for every request
 * passing through the gateway and probabilistically injects failure modes.
 *
 * Order matters - this runs early (HIGHEST_PRECEDENCE + 1) so latency/errors
 * are applied before the request is actually routed downstream.
 */
@Component
public class ChaosGlobalFilter implements GlobalFilter, Ordered {

    private final ChaosProperties chaosProperties;

    public ChaosGlobalFilter(ChaosProperties chaosProperties) {
        this.chaosProperties = chaosProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!chaosProperties.isEnabled()) {
            return chain.filter(exchange);
        }

        double roll = ThreadLocalRandom.current().nextDouble();

        // Simulate a dropped connection / network partition: never
        // complete the exchange, just let it hang until the caller's
        // own client-side timeout fires
        if (roll < chaosProperties.getDroppedConnectionProbability()) {
            return Mono.never();
        }

        // Simulate an upstream error without even calling the real service
        if (roll < chaosProperties.getDroppedConnectionProbability() + chaosProperties.getErrorProbability()) {
            exchange.getResponse().setStatusCode(HttpStatus.valueOf(chaosProperties.getErrorStatus()));
            return exchange.getResponse().setComplete();
        }

        // Simulate added latency, then continue as normal
        if (roll < chaosProperties.getDroppedConnectionProbability()
                + chaosProperties.getErrorProbability()
                + chaosProperties.getLatencyProbability()) {
            return Mono.delay(Duration.ofMillis(chaosProperties.getLatencyMs()))
                    .then(chain.filter(exchange));
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
