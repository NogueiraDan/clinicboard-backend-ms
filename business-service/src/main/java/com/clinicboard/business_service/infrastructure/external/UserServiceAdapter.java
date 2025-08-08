package com.clinicboard.business_service.infrastructure.external;

import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;

import com.clinicboard.business_service.application.port.outbound.UserServicePort;
import com.clinicboard.business_service.infrastructure.external.dto.UserResponseDto;
import com.clinicboard.business_service.infrastructure.external.dto.UserRole;

/**
 * Adapter que implementa o UserServicePort usando Feign Client
 * Camada de infraestrutura - conecta com serviços externos
 */
@Component
public class UserServiceAdapter implements UserServicePort {
    
    private final UserFeignClient userFeignClient;
    
    public UserServiceAdapter(UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }
    
    @Override
    public UserResponseDto findUserById(String userId) {
        ResponseEntity<UserResponseDto> response = userFeignClient.findById(userId);
        return response.getBody();
    }
    
    @Override
    public boolean isUserProfessional(String userId) {
        try {
            UserResponseDto user = findUserById(userId);
            return user != null && user.getRole() == UserRole.PROFESSIONAL;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean userExists(String userId) {
        try {
            UserResponseDto user = findUserById(userId);
            return user != null;
        } catch (Exception e) {
            return false;
        }
    }
}
