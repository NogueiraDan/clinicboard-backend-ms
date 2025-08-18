package com.clinicboard.user_service.application.port.out;

import com.clinicboard.user_service.domain.model.User;

/**
 * Porta de saída para operações de autenticação.
 */
public interface AuthenticationServicePort {
    
    /**
     * Autentica um usuário com email e senha
     */
    User authenticate(String email, String password);
    
    /**
     * Gera um token JWT para o usuário
     */
    String generateToken(User user);
}
