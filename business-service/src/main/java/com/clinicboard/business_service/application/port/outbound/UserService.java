package com.clinicboard.business_service.application.port.outbound;

import com.clinicboard.business_service.infrastructure.external.dto.UserResponseDto;

/**
 * Porta de saída para integração com serviço de usuários
 */
public interface UserService {
    
    UserResponseDto findUserById(String userId);
    
    boolean isUserProfessional(String userId);
}
