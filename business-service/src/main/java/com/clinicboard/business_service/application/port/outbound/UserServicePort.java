package com.clinicboard.business_service.application.port.outbound;

import com.clinicboard.business_service.infrastructure.external.dto.UserResponseDto;

/**
 * Port para comunicação com serviços externos
 * Define o contrato que a camada de aplicação espera da infraestrutura
 */
public interface UserServicePort {
    
    /**
     * Busca informações de um usuário por ID
     */
    UserResponseDto findUserById(String userId);
    
    /**
     * Verifica se um usuário possui perfil de profissional
     */
    boolean isUserProfessional(String userId);
    
    /**
     * Valida se um usuário existe no sistema
     */
    boolean userExists(String userId);
}
