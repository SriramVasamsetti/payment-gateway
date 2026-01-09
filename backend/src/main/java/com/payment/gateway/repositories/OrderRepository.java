package com.payment.gateway.repositories;

import com.payment.gateway.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByIdAndMerchantId(String id, String merchantId);
}
