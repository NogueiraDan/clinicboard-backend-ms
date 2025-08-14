package com.clinicboard.user_service.application.port.outbound;

import com.clinicboard.user_service.domain.model.User;

/**
 * Porto de saída - Define operações de token para infraestrutura
 */
public interface TokenServicePort {
    
    /**
     * Gera token JWT para um usuário
     * @param user usuário para gerar token
     * @return token JWT
     */
    String generateToken(User user);
    
    /**
     * Valida token JWT
     * @param token JWT token
     * @return ID do usuário se válido, string vazia se inválido
     */
    String validateToken(String token);
    
    /**
     * Obtém tempo de expiração do token em segundos
     * @return tempo em segundos
     */
    Long getExpirationTime();
}
