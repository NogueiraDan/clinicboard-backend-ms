package com.clinicboard.user_service.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório JPA para UserJpaEntity.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {
    
    Optional<UserJpaEntity> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
