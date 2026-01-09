package com.payment.gateway.services;

import com.payment.gateway.models.Merchant;
import com.payment.gateway.models.Order;
import com.payment.gateway.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final Random random = new Random();

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(
        Merchant merchant,
        Integer amount,
        String currency,
        String receipt,
        String notes
) {
    Order order = new Order();
    order.setId(generateOrderId());
    order.setMerchantId(merchant.getId().toString());
    order.setAmount(amount);
    order.setCurrency(currency);
    order.setReceipt(receipt);
    order.setNotes(notes);
    order.setStatus("created");

    // ✅ FIX — SET TIMESTAMPS
    order.setCreatedAt(java.time.Instant.now());
    order.setUpdatedAt(java.time.Instant.now());

    return orderRepository.save(order);
}


    public Order getOrder(String orderId, String merchantId) {
        return orderRepository
                .findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));
    }
    public Order getOrderPublic(String orderId) {
    return orderRepository
            .findById(orderId)
            .orElseThrow(() -> new RuntimeException("NOT_FOUND"));
}


    private String generateOrderId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder("order_");
        for (int i = 0; i < 16; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
