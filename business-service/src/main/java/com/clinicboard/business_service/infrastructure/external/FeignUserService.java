package com.clinicboard.business_service.infrastructure.external;

import com.clinicboard.business_service.infrastructure.external.dto.UserResponseDto;
import com.clinicboard.business_service.application.port.outbound.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;

/**
 * Adaptador para integração com User Service via Feign
 */
@Component
public class FeignUserService implements UserService {
    
    private final UserFeignClient userFeignClient;
    
    public FeignUserService(UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }
    
    @Override
    @CircuitBreaker(name = "user-service", fallbackMethod = "findUserByIdFallback")
    public UserResponseDto findUserById(String userId) {
        return userFeignClient.findById(userId).getBody();
    }
    
    @Override
    @CircuitBreaker(name = "user-service", fallbackMethod = "isUserProfessionalFallback")
    public boolean isUserProfessional(String userId) {
        try {
            UserResponseDto user = findUserById(userId);
            return user != null && "PROFESSIONAL".equals(user.getRole().toString());
        } catch (Exception e) {
            return false;
        }
    }
    
    // Métodos de fallback
    public UserResponseDto findUserByIdFallback(String userId, Throwable throwable) {
        // Log do erro seria interessante aqui
        return null;
    }
    
    public boolean isUserProfessionalFallback(String userId, Throwable throwable) {
        // Em caso de falha, por segurança, assumimos que não é profissional
        return false;
    }
}
