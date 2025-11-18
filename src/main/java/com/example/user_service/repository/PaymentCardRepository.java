package com.example.user_service.repository;

import com.example.user_service.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, UUID>, JpaSpecificationExecutor<PaymentCard> {

    Optional<PaymentCard> findPaymentCardById(UUID id);
    boolean existsByNumber(String number);

    @Query(value = """
        SELECT * FROM payment_cards p
        WHERE p.user_id = :userId
    """, nativeQuery = true)
    List<PaymentCard> findAllByUserId(@Param("userId") UUID userId);

    @Query(value = """
        SELECT COUNT(*) FROM PaymentCard p
        JOIN User u ON p.user.id = u.id
        WHERE u.id = :userId
    """)
    int countPaymentCardByUserId(@Param("userId") UUID userId);

}
