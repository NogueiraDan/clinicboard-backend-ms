package com.clinicboard.business_service.infrastructure.adapter.out.integration.client;

import com.clinicboard.business_service.domain.exception.ProfessionalValidationException;
import com.clinicboard.business_service.infrastructure.adapter.out.integration.dto.UserServiceResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback para o UserServiceFeignClient.
 * 
 * Implementa comportamento alternativo quando o user-service está indisponível.
 * Por enquanto, lança exceção informando que o serviço está inativo.
 */
@Component
public class UserServiceFeignClientFallback implements UserServiceFeignClient {
    
    private static final Logger log = LoggerFactory.getLogger(UserServiceFeignClientFallback.class);
    
    @Override
    public UserServiceResponseDto findUserById(String userId) {
        log.error("Fallback acionado: user-service está indisponível para validação do profissional com ID: {}", userId);
        
        throw new ProfessionalValidationException(
            "Serviço de usuários não está ativo no momento. Tente novamente em alguns instantes."
        );
    }
}
