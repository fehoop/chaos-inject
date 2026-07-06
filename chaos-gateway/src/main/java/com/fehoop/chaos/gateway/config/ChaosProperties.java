package com.fehoop.chaos.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bound from the "chaos" prefix in application.yml, and mutable at runtime
 * via ChaosAdminController - no restart needed to change the failure profile.
 *
 * Example application.yml:
 * chaos:
 * enabled: true
 * latency-probability: 0.1
 * latency-ms: 3000
 * error-probability: 0.05
 * error-status: 503
 * dropped-connection-probability: 0.02
 */
@Data
@Component
@ConfigurationProperties(prefix = "chaos")
public class ChaosProperties {
    private boolean enabled = false;
    private double latencyProbability = 0.0;
    private long latencyMs = 2000;
    private double errorProbability = 0.0;
    private int errorStatus = 503;
    private double droppedConnectionProbability = 0.0;
}
