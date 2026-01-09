package com.payment.gateway.controllers;

import com.payment.gateway.models.Merchant;
import com.payment.gateway.models.Order;
import com.payment.gateway.models.Payment;
import com.payment.gateway.repositories.OrderRepository;
import com.payment.gateway.repositories.PaymentRepository;
import com.payment.gateway.services.AuthenticationService;
import com.payment.gateway.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final AuthenticationService authService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public PaymentController(AuthenticationService authService,
                             OrderRepository orderRepository,
                             PaymentRepository paymentRepository,
                             PaymentService paymentService) {
        this.authService = authService;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
    }

    // =========================================================
    // PUBLIC PAYMENT (CHECKOUT — NO AUTH HEADERS)
    // =========================================================
    @PostMapping("/public")
    public ResponseEntity<?> createPaymentPublic(@RequestBody Map<String, Object> request) {

        String orderId = (String) request.get("order_id");
        String method = (String) request.get("method");

        if (orderId == null || method == null) {
            return ResponseEntity.badRequest().body(error(
                    "BAD_REQUEST_ERROR",
                    "order_id and method are required"
            ));
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.status(404).body(error(
                    "NOT_FOUND_ERROR",
                    "Order not found"
            ));
        }

        try {
            Payment payment;

            if ("upi".equals(method)) {
                String vpa = (String) request.get("vpa");
                payment = paymentService.createPayment(
                        order, "upi", vpa, null, null, null
                );
            } else if ("card".equals(method)) {
                Map<String, String> card =
                        (Map<String, String>) request.get("card");

                if (card == null) {
                    return ResponseEntity.badRequest().body(error(
                            "BAD_REQUEST_ERROR",
                            "card details required"
                    ));
                }

                payment = paymentService.createPayment(
                        order,
                        "card",
                        null,
                        card.get("number"),
                        card.get("expiry_month"),
                        card.get("expiry_year")
                );
            } else {
                return ResponseEntity.badRequest().body(error(
                        "BAD_REQUEST_ERROR",
                        "invalid payment method"
                ));
            }

            return ResponseEntity.status(201).body(buildPaymentResponse(payment));

        } catch (IllegalArgumentException e) {
            String code = e.getMessage();
            return ResponseEntity.badRequest().body(error(code, errorMessage(code)));
        }
    }
    

    // =========================================================
    // AUTH PAYMENT (MERCHANT)
    // =========================================================
    @PostMapping
    public ResponseEntity<?> createPayment(
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

        String orderId = (String) request.get("order_id");
        if (orderId == null) {
            return ResponseEntity.badRequest().body(error(
                    "BAD_REQUEST_ERROR",
                    "order_id is required"
            ));
        }

        Order order = orderRepository
                .findByIdAndMerchantId(orderId, merchant.getId().toString())
                .orElse(null);

        if (order == null) {
            return ResponseEntity.status(404).body(error(
                    "NOT_FOUND_ERROR",
                    "Order not found"
            ));
        }

        String method = (String) request.get("method");
        if (method == null) {
            return ResponseEntity.badRequest().body(error(
                    "BAD_REQUEST_ERROR",
                    "payment method is required"
            ));
        }

        try {
            Payment payment;

            if ("upi".equals(method)) {
                payment = paymentService.createPayment(
                        order, "upi", (String) request.get("vpa"),
                        null, null, null
                );
            } else if ("card".equals(method)) {
                Map<String, String> card =
                        (Map<String, String>) request.get("card");

                payment = paymentService.createPayment(
                        order,
                        "card",
                        null,
                        card.get("number"),
                        card.get("expiry_month"),
                        card.get("expiry_year")
                );
            } else {
                return ResponseEntity.badRequest().body(error(
                        "BAD_REQUEST_ERROR",
                        "invalid payment method"
                ));
            }

            return ResponseEntity.status(201).body(buildPaymentResponse(payment));

        } catch (IllegalArgumentException e) {
            String code = e.getMessage();
            return ResponseEntity.badRequest().body(error(code, errorMessage(code)));
        }
    }

    // =========================================================
    // GET PAYMENT (POLLING — AUTH)
    // =========================================================
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPayment(
            @PathVariable String paymentId,
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

        Payment payment = paymentRepository.findById(paymentId).orElse(null);

        if (payment == null ||
                !payment.getMerchantId().equals(merchant.getId().toString())) {
            return ResponseEntity.status(404).body(error(
                    "NOT_FOUND_ERROR",
                    "Payment not found"
            ));
        }

        return ResponseEntity.ok(buildPaymentResponse(payment));
    }

    // =========================================================
    // HELPERS
    // =========================================================
    private Map<String, Object> buildPaymentResponse(Payment p) {
        Map<String, Object> res = new HashMap<>();
        res.put("id", p.getId());
        res.put("order_id", p.getOrderId());
        res.put("amount", p.getAmount());
        res.put("currency", p.getCurrency());
        res.put("method", p.getMethod());
        res.put("status", p.getStatus());
        res.put("created_at", p.getCreatedAt().toString());
        res.put("updated_at", p.getUpdatedAt().toString());

        if ("upi".equals(p.getMethod())) res.put("vpa", p.getVpa());
        if ("card".equals(p.getMethod())) {
            res.put("card_network", p.getCardNetwork());
            res.put("card_last4", p.getCardLast4());
        }

        if ("failed".equals(p.getStatus())) {
            res.put("error_code", p.getErrorCode());
            res.put("error_description", p.getErrorDescription());
        }

        return res;
    }

    private Map<String, Object> error(String code, String description) {
        return Map.of(
                "error", Map.of(
                        "code", code,
                        "description", description
                )
        );
    }

    private String errorMessage(String code) {
        return switch (code) {
            case "INVALID_VPA" -> "VPA format invalid";
            case "INVALID_CARD" -> "Card validation failed";
            case "EXPIRED_CARD" -> "Card expiry date invalid";
            default -> "Payment processing failed";
        };
    }
    @GetMapping
public ResponseEntity<?> listPayments(
        @RequestHeader("X-Api-Key") String apiKey,
        @RequestHeader("X-Api-Secret") String apiSecret
) {
    Merchant merchant;
    try {
        merchant = authService.authenticate(apiKey, apiSecret);
    } catch (RuntimeException e) {
        return ResponseEntity.status(401).body(Map.of(
                "error", Map.of(
                        "code", "AUTHENTICATION_ERROR",
                        "description", "Invalid API credentials"
                )
        ));
    }

    return ResponseEntity.ok(
            paymentService.getPaymentsByMerchant(
                    merchant.getId().toString()
            )
    );
}

}
