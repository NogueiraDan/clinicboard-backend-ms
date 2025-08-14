package com.clinicboard.user_service.application.port.inbound;

import com.clinicboard.user_service.application.dto.LoginRequestDto;
import com.clinicboard.user_service.application.dto.LoginResponseDto;

/**
 * Porto de entrada - Define os casos de uso de autenticação
 */
public interface AuthenticationUseCase {
    
    /**
     * Realiza login do usuário e retorna token JWT
     * @param request dados de login (email e senha)
     * @return resposta com token e dados do usuário
     * @throws IllegalArgumentException se credenciais inválidas
     */
    LoginResponseDto login(LoginRequestDto request);
    
    /**
     * Valida se um token é válido
     * @param token JWT token
     * @return true se válido, false caso contrário
     */
    boolean validateToken(String token);
    
    /**
     * Extrai ID do usuário de um token válido
     * @param token JWT token
     * @return ID do usuário ou null se token inválido
     */
    String extractUserIdFromToken(String token);
}
