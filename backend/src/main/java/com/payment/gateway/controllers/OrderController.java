package com.payment.gateway.controllers;

import com.payment.gateway.models.Merchant;
import com.payment.gateway.models.Order;
import com.payment.gateway.services.AuthenticationService;
import com.payment.gateway.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final AuthenticationService authService;
    private final OrderService orderService;

    public OrderController(AuthenticationService authService, OrderService orderService) {
        this.authService = authService;
        this.orderService = orderService;
    }

    // =========================================================
    // PUBLIC ORDER (CHECKOUT — NO AUTH)
    // =========================================================
   @GetMapping("/{orderId}/public")
public ResponseEntity<?> getOrderPublic(@PathVariable String orderId) {
    Order order = orderService.getOrderPublic(orderId);

    return ResponseEntity.ok(Map.of(
            "id", order.getId(),
            "amount", order.getAmount(),
            "currency", order.getCurrency(),
            "status", order.getStatus()
    ));
}


    // =========================================================
    // CREATE ORDER (AUTH)
    // =========================================================
    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestHeader("X-Api-Key") String apiKey,
            @RequestHeader("X-Api-Secret") String apiSecret,
            @RequestBody Map<String, Object> request
    ) {
        Merchant merchant;
        try {
            merchant = authService.authenticate(apiKey, apiSecret);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(error(
                    "AUTHENTICATION_ERROR",
                    "Invalid API credentials"
            ));
        }

        Object amountObj = request.get("amount");
        if (!(amountObj instanceof Number) || ((Number) amountObj).intValue() < 100) {
            return ResponseEntity.badRequest().body(error(
                    "BAD_REQUEST_ERROR",
                    "amount must be at least 100"
            ));
        }

        int amount = ((Number) amountObj).intValue();
        String currency = (String) request.getOrDefault("currency", "INR");
        String receipt = (String) request.get("receipt");
        String notes = request.containsKey("notes")
                ? request.get("notes").toString()
                : null;

        Order order = orderService.createOrder(
                merchant,
                amount,
                currency,
                receipt,
                notes
        );

        return ResponseEntity.status(201).body(Map.of(
                "id", order.getId(),
                "merchant_id", order.getMerchantId(),
                "amount", order.getAmount(),
                "currency", order.getCurrency(),
                "receipt", order.getReceipt(),
                "notes", notes,
                "status", order.getStatus(),
                "created_at", order.getCreatedAt().toString()
        ));
    }

    // =========================================================
    // GET ORDER (AUTH — MERCHANT)
    // =========================================================
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(
            @PathVariable String orderId,
            @RequestHeader("X-Api-Key") String apiKey,
            @RequestHeader("X-Api-Secret") String apiSecret
    ) {
        Merchant merchant;
        try {
            merchant = authService.authenticate(apiKey, apiSecret);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(error(
                    "AUTHENTICATION_ERROR",
                    "Invalid API credentials"
            ));
        }

        try {
            Order order = orderService.getOrder(orderId, merchant.getId().toString());

            return ResponseEntity.ok(Map.of(
                    "id", order.getId(),
                    "merchant_id", order.getMerchantId(),
                    "amount", order.getAmount(),
                    "currency", order.getCurrency(),
                    "receipt", order.getReceipt(),
                    "notes", order.getNotes(),
                    "status", order.getStatus(),
                    "created_at", order.getCreatedAt().toString(),
                    "updated_at", order.getUpdatedAt().toString()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(error(
                    "NOT_FOUND_ERROR",
                    "Order not found"
            ));
        }
    }

    // =========================================================
    // ERROR HELPER
    // =========================================================
    private Map<String, Object> error(String code, String description) {
        return Map.of(
                "error", Map.of(
                        "code", code,
                        "description", description
                )
        );
    }
}
