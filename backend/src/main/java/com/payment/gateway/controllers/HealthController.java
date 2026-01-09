package com.payment.gateway.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("timestamp", Instant.now().toString());

        try (Connection connection = dataSource.getConnection()) {
            response.put("database", "connected");
        } catch (Exception e) {
            response.put("database", "disconnected");
        }

        return ResponseEntity.ok(response);
    }
}
