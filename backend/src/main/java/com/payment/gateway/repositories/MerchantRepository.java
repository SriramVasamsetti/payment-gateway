package com.payment.gateway.repositories;

import com.payment.gateway.models.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    Optional<Merchant> findByApiKey(String apiKey);
    Optional<Merchant> findByEmail(String email);
}
