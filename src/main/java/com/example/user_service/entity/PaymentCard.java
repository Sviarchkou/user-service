package com.example.user_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "payment_cards")
@EnableJpaAuditing
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentCard {
    @Id
    UUID id;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    User user;

    @Column(name = "number", nullable = false)
    String number;

    @Column(name = "holder", nullable = false)
    String holder;

    @Column(name = "expiration_date", nullable = false)
    String expirationDate;

    boolean active = false;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", insertable = false)
    LocalDateTime updatedAt;
}
