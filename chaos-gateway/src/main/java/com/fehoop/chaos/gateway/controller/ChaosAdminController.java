package com.fehoop.chaos.gateway.controller;

import com.fehoop.chaos.gateway.config.ChaosProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hot-update the chaos profile without restarting the gateway.
 *
 * GET /chaos/status
 * POST /chaos/config { "enabled": true, "latencyProbability": 0.2, ... }
 */
@RestController
public class ChaosAdminController {

    private final ChaosProperties chaosProperties;

    public ChaosAdminController(ChaosProperties chaosProperties) {
        this.chaosProperties = chaosProperties;
    }

    @GetMapping("/chaos/status")
    public ChaosProperties status() {
        return chaosProperties;
    }

    @PostMapping("/chaos/config")
    public ChaosProperties updateConfig(@RequestBody ChaosProperties incoming) {
        chaosProperties.setEnabled(incoming.isEnabled());
        chaosProperties.setLatencyProbability(incoming.getLatencyProbability());
        chaosProperties.setLatencyMs(incoming.getLatencyMs());
        chaosProperties.setErrorProbability(incoming.getErrorProbability());
        chaosProperties.setErrorStatus(incoming.getErrorStatus());
        chaosProperties.setDroppedConnectionProbability(incoming.getDroppedConnectionProbability());
        return chaosProperties;
    }
}
