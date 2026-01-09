package com.payment.gateway.repositories;

import com.payment.gateway.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;   // ✅ REQUIRED IMPORT

public interface PaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findByMerchantId(String merchantId);

}
