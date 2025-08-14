package com.clinicboard.user_service.infrastructure.config;

import com.clinicboard.user_service.application.port.outbound.UserRepositoryPort;
import com.clinicboard.user_service.domain.service.UserDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração dos Domain Services - Injection Dependency
 */
@Configuration
public class DomainConfig {
    
    @Bean
    public UserDomainService userDomainService(UserRepositoryPort userRepository) {
        return new UserDomainService(userRepository);
    }
}
