package com.example.user_service.dao;

import com.example.user_service.entity.PaymentCard;
import com.example.user_service.repository.PaymentCardRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Objects;

// The class is presented as an example implementation of the create(PaymentCard paymentCard) method.
// This class will not be used on the service layer
// Instead, JPA Repository default methods (save, findAll, ect.) will be used
// @Repository
@RequiredArgsConstructor
public class PaymentCardDao {
    private final EntityManager entityManager;
    private final PaymentCardRepository paymentCardRepository;

    public PaymentCard create(PaymentCard paymentCard){
        if (Objects.isNull(paymentCard.getNumber()))
            throw new IllegalArgumentException("Payment card number must be non null on creation");
        if (Objects.isNull(paymentCard.getHolder()))
            throw new IllegalArgumentException("Payment card holder must be non null on creation");
        if (Objects.isNull(paymentCard.getExpirationDate()))
            throw new IllegalArgumentException("Payment card expiration date must be non null on creation");
        if (Objects.isNull(paymentCard.getUser()))
            throw new IllegalArgumentException("Payment card user must be non null on creation");
        if (Objects.isNull(paymentCard.getUser().getId()))
            throw new IllegalArgumentException("Payment card user_id must be non null on creation");

        if (paymentCardRepository.countPaymentCardByUserId(paymentCard.getUser().getId()) >= 5)
            throw new IllegalArgumentException("One user should have no more than 5 cards");
        entityManager.persist(paymentCard);
        return paymentCard;
    }
}
