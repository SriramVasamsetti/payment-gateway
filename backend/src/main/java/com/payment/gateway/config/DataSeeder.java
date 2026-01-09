package com.payment.gateway.config;

import com.payment.gateway.models.Merchant;
import com.payment.gateway.repositories.MerchantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedMerchant(MerchantRepository repository) {
        return args -> {
            repository.findByEmail("test@example.com").ifPresentOrElse(
                merchant -> {
                    // Already exists → do nothing
                },
                () -> {
                    Merchant merchant = new Merchant();
                    merchant.setId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
                    merchant.setName("Test Merchant");
                    merchant.setEmail("test@example.com");
                    merchant.setApiKey("key_test_abc123");
                    merchant.setApiSecret("secret_test_xyz789");
                    repository.save(merchant);
                }
            );
        };
    }
}
