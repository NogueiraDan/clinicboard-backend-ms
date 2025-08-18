package com.clinicboard.user_service.application.port.in;

import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.UserId;

/**
 * Porta de entrada para buscar usuário.
 * Define o contrato para os casos de uso de consulta de usuário.
 */
public interface FindUserUseCase {
    
    /**
     * Busca usuário por ID
     */
    User findById(UserId id);
    
    /**
     * Busca usuário por email
     */
    User findByEmail(String email);
}
