package com.example.user_service.repository;

import com.example.user_service.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, UUID>, JpaSpecificationExecutor<PaymentCard> {

    //  methods:
    //      PaymentCard create(PaymentCard paymentCard); -> JPA method save(PaymentCard paymentCard)
    //      PaymentCard updatePaymentCardById(UUID id, PaymentCard paymentCard); -> JPA method save(PaymentCard paymentCard)
    //      @NonNull
    //      Page<PaymentCard> findAll(@NonNull Pageable pageable);
    //  have already been implemented in JPA Repository

    //  Logic of maximum count of user's payment cards (max 5 card) will be brought to the service layer

    PaymentCard findPaymentCardById(UUID id);

    @Query(value = """
        SELECT * FROM payment_cards p
        WHERE p.user_id = :user_id
    """, nativeQuery = true)
    List<PaymentCard> findAllByUserId(@Param("user_id") UUID user_id);

    int countPaymentCardByUserId(UUID user_id);

    @Modifying
    @Query(value = """
        UPDATE PaymentCard p
        SET p.active = true, p.updatedAt = CURRENT_TIMESTAMP
        WHERE p.id = :id
    """)
    void activatePaymentCardById(@Param("id") UUID id);

    @Modifying
    @Query(value = """
        UPDATE PaymentCard p
        SET p.active = false, p.updatedAt = CURRENT_TIMESTAMP
        WHERE p.id = :id
    """)
    void deactivatePaymentCardById(@Param("id") UUID id);
}
