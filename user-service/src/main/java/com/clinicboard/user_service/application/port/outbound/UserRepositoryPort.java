package com.clinicboard.user_service.application.port.outbound;

import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.valueobjects.Email;
import com.clinicboard.user_service.domain.model.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Porta de saída para persistência de usuários (Repository Pattern)
 * Define o contrato que será implementado na camada de Infrastructure
 */
public interface UserRepositoryPort {
    
    User save(User user);
    
    Optional<User> findById(UserId id);
    
    Optional<User> findByEmail(Email email);
    
    List<User> findAll();
    
    List<User> findByRole(String role);
    
    boolean existsByEmail(Email email);
    
    void deleteById(UserId id);
}
