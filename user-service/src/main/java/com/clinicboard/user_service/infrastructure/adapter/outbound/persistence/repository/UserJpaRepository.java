package com.clinicboard.user_service.infrastructure.adapter.outbound.persistence.repository;

import com.clinicboard.user_service.domain.model.enums.UserRole;
import com.clinicboard.user_service.infrastructure.adapter.outbound.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

/**
 * Repository JPA - Camada de Infrastructure
 */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {
    
    // UserDetails findByEmail(String email);
    
    Optional<UserJpaEntity> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<UserJpaEntity> findByRole(UserRole role);
    
    @Query("SELECT u FROM users u WHERE u.status = :status")
    List<UserJpaEntity> findByStatus(@Param("status") String status);
    
    @Query("SELECT u FROM users u WHERE u.role = :role AND u.status = 'ACTIVE'")
    List<UserJpaEntity> findActiveUsersByRole(@Param("role") UserRole role);
}
