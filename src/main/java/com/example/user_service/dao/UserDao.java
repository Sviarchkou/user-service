package com.example.user_service.dao;

import com.example.user_service.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Objects;

// The class is presented as an example implementation of the create(User user) method.
// This class will not be used on the service layer
// Instead, JPA Repository default methods (save, findAll, ect.) will be used
// @Repository
@RequiredArgsConstructor
public class UserDao {
    private final EntityManager entityManager;

    public User create(User user){
        if (Objects.isNull(user.getName()))
            throw new IllegalArgumentException("User name must be non null on creation");
        if (Objects.isNull(user.getSurname()))
            throw new IllegalArgumentException("User surname must be non null on creation");
        if (Objects.isNull(user.getEmail()))
            throw new IllegalArgumentException("User email must be non null on creation");
        entityManager.persist(user);
        return user;
    }

}
