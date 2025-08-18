package com.clinicboard.user_service.application.port.out;

import com.clinicboard.user_service.domain.model.Email;
import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Porta de saída para persistência de usuários.
 * Define o contrato que os adaptadores de persistência devem implementar.
 */
public interface UserRepositoryPort {
    
    /**
     * Salva um usuário no repositório
     */
    User save(User user);
    
    /**
     * Busca um usuário por ID
     */
    Optional<User> findById(UserId id);
    
    /**
     * Busca um usuário por email
     */
    Optional<User> findByEmail(Email email);
    
    /**
     * Lista todos os usuários
     */
    List<User> findAll();
    
    /**
     * Verifica se existe um usuário com o ID especificado
     */
    boolean existsById(UserId id);
    
    /**
     * Remove um usuário por ID
     */
    void deleteById(UserId id);
    
    /**
     * Verifica se existe um usuário com o email especificado
     */
    boolean existsByEmail(Email email);
}
