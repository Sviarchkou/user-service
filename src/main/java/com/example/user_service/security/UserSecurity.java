package com.example.user_service.security;

import com.example.user_service.service.PaymentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserSecurity {

    private final PaymentCardService paymentCardService;

    public boolean hasUserId(Authentication authentication, UUID id) {
        UUID userId = (UUID) authentication.getPrincipal();
        return userId.equals(id);
    }

    public boolean hasPaymentCard(Authentication authentication, UUID cardId) {
        UUID userId = (UUID) authentication.getPrincipal();
        var card = paymentCardService.getById(cardId);
        return userId.equals(card.getUserId());
    }
}
