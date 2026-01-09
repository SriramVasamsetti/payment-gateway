package com.payment.gateway.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    @GetMapping("/merchant")
    public ResponseEntity<Map<String, Object>> getTestMerchant() {
        Map<String, Object> response = new HashMap<>();
        response.put("id", "550e8400-e29b-41d4-a716-446655440000");
        response.put("email", "test@example.com");
        response.put("api_key", "key_test_abc123");
        response.put("api_secret", "secret_test_xyz789");
        response.put("seeded", true);
        return ResponseEntity.ok(response);
    }
}
