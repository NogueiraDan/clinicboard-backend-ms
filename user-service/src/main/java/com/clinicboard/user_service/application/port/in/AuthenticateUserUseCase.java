package com.clinicboard.user_service.application.port.in;

import com.clinicboard.user_service.domain.model.User;

/**
 * Porta de entrada para autenticação de usuário.
 */
public interface AuthenticateUserUseCase {
    
    /**
     * Autentica um usuário e retorna o token JWT
     */
    AuthenticationResult authenticate(AuthenticationCommand command);
    
    /**
     * Comando para autenticação
     */
    record AuthenticationCommand(
            String email,
            String password
    ) {}
    
    /**
     * Resultado da autenticação
     */
    record AuthenticationResult(
            User user,
            String token
    ) {}
}
