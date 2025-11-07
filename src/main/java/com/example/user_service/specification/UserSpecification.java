package com.example.user_service.specification;

import com.example.user_service.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> userNameSpecification(String name){
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<User> userSurnameSpecification(String surname){
        return (root, query, cb) -> cb.like(cb.lower(root.get("surname")), "%" + surname.toLowerCase() + "%");
    }
}