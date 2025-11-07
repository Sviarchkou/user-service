package com.example.user_service.repository;

import com.example.user_service.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findUserById(UUID id);

    // methods
    // User createUser(User user); -> JPA method save(User user)
    // Page<User> findAll(Specification<User> specification, Pageable pageable);
    // User updateUserById(UUID id, User user); -> JPA method save(User user)
    // have already been implemented in JPA repository

    @Modifying
    @Query(value = """
        UPDATE users u
        SET u.active = true, u.updatedAt = now()
        WHERE u.id = :id
    """, nativeQuery = true)
    void activateUserById(UUID id);

    @Modifying
    @Query(value = """
        UPDATE User user
        SET user.active = false, user.updatedAt = CURRENT_TIMESTAMP
        WHERE user.id = :id
    """)
    void deactivateUserById(UUID id);

}
