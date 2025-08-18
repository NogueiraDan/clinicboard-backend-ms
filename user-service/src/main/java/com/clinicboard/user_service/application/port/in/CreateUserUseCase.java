package com.clinicboard.user_service.application.port.in;

import com.clinicboard.user_service.domain.model.User;

/**
 * Porta de entrada para criar usuário.
 * Define o contrato para o caso de uso de criação de usuário.
 */
public interface CreateUserUseCase {
    
    /**
     * Cria um novo usuário no sistema
     */
    User createUser(CreateUserCommand command);
    
    /**
     * Comando para criação de usuário
     */
    record CreateUserCommand(
            String name,
            String email,
            String password,
            String contact,
            String role
    ) {}
}
