package com.example.user_service.specification;

import com.example.user_service.entity.User;
import com.example.user_service.filter.UserFilter;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> userNameSpecification(String name){
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<User> userSurnameSpecification(String surname){
        return (root, query, cb) -> cb.like(cb.lower(root.get("surname")), "%" + surname.toLowerCase() + "%");
    }

    public static Specification<User> userFilterSpecification(UserFilter filter){
        return userNameSpecification(filter.getName()).and(userSurnameSpecification(filter.getSurname()));
    }
}