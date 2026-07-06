# Chaos Injector

A hands-on chaos-engineering playground built with Spring Boot + Spring
Cloud Gateway + Resilience4j. Three independent Maven projects, run each
with `mvn spring-boot:run` in its own terminal.

```
client -> chaos-gateway (8080) -> order-service (8081) -> [via gateway again] -> inventory-service (8082)
```

`order-service` never calls `inventory-service` directly - it calls it
through the gateway on purpose, so the gateway's ChaosGlobalFilter can
inject latency / errors / dropped connections on that hop, and you can
watch order-service's Resilience4j circuit breaker react.

## Run order
1. `cd inventory-service && mvn spring-boot:run`
2. `cd order-service && mvn spring-boot:run`
3. `cd chaos-gateway && mvn spring-boot:run`
4. `docker compose up` (Prometheus on :9090, Grafana on :3000, default login admin/admin)

## Try it

Baseline (chaos disabled by default):
```
curl http://localhost:8080/orders/check/ABC123
```
(Note: order-service listens on 8081, but hits inventory-service through
the gateway on 8080 internally - call order-service directly at :8081/orders/check/ABC123)

Turn chaos on:
```
curl -X POST http://localhost:8080/chaos/config \
  -H "Content-Type: application/json" \
  -d '{"enabled": true, "latencyProbability": 0.3, "latencyMs": 4000, "errorProbability": 0.4, "errorStatus": 503, "droppedConnectionProbability": 0.0}'
```

Now hammer the endpoint (e.g. with `hey` or a simple bash loop) and watch:
- `curl http://localhost:8081/actuator/circuitbreakers` - state flipping CLOSED -> OPEN -> HALF_OPEN
- Grafana dashboard built on the Prometheus metrics from all three services
- order-service falling back gracefully instead of hanging/crashing once
  the circuit opens

## Suggested build order
1. Get all three services running with chaos disabled, confirm the happy path
2. Turn on `errorProbability` only, watch retries absorb transient failures
3. Turn on `errorProbability` high enough to open the circuit breaker,
   watch the fallback kick in
4. Add `latencyProbability` and see how it interacts with Resilience4j's
   default timeouts (you may need to add a TimeLimiter config too - left as
   an extension)
5. Wire up a Grafana dashboard (panels: request rate, error rate, p99
   latency, circuit breaker state) - this is the "wow" deliverable to screenshot
6. (Optional) add a second downstream (payment-service) and a bulkhead
   config so one flaky dependency can't exhaust all threads
