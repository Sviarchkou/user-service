package com.example.user_service.specification;

import com.example.user_service.entity.PaymentCard;
import com.example.user_service.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class PaymentCardSpecification {

    public static Specification<PaymentCard> cardUserNameSpecification(String name){
        return (root, query, cb) -> {
            Join<PaymentCard, User> user = root.join("user", JoinType.INNER);
            return cb.like(cb.lower(user.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<PaymentCard> cardUserSurnameSpecification(String surname){
        return (root, query, cb) -> {
            Join<PaymentCard, User> user = root.join("user", JoinType.INNER);
            return cb.like(cb.lower(user.get("surname")), "%" + surname.toLowerCase() + "%");
        };
    }
}
