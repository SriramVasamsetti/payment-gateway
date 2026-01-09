package com.payment.gateway.services;

import com.payment.gateway.models.Merchant;
import com.payment.gateway.repositories.MerchantRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final MerchantRepository merchantRepository;

    public AuthenticationService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public Merchant authenticate(String apiKey, String apiSecret) {
        Merchant merchant = merchantRepository
                .findByApiKey(apiKey)
                .orElseThrow(() ->
                        new RuntimeException("AUTHENTICATION_ERROR"));

        if (!merchant.getApiSecret().equals(apiSecret)) {
            throw new RuntimeException("AUTHENTICATION_ERROR");
        }

        if (!Boolean.TRUE.equals(merchant.getIsActive())) {
            throw new RuntimeException("AUTHENTICATION_ERROR");
        }

        return merchant;
    }
}
