package com.clinicboard.business_service.infrastructure.adapter.out.integration;

import com.clinicboard.business_service.application.port.out.ProfessionalValidationGateway;
import com.clinicboard.business_service.domain.exception.ProfessionalValidationException;
import com.clinicboard.business_service.domain.model.ProfessionalId;
import com.clinicboard.business_service.infrastructure.adapter.out.integration.client.UserServiceFeignClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementação real do gateway de validação de profissionais.
 * 
 * Utiliza Feign Client para comunicação com o user-service,
 * com Circuit Breaker para resiliência.
 */
@Component
public class FeignProfessionalValidationGateway implements ProfessionalValidationGateway {
    
    private static final Logger log = LoggerFactory.getLogger(FeignProfessionalValidationGateway.class);
    private static final String CIRCUIT_BREAKER_NAME = "business-service";

    
    private final UserServiceFeignClient userServiceClient;
    
    public FeignProfessionalValidationGateway(UserServiceFeignClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }
    
    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "fallbackIsValidAndActiveProfessional")
    public boolean isValidAndActiveProfessional(ProfessionalId professionalId) {
        log.debug("Validando se profissional está ativo com ID: {}", professionalId.value());
        
       try {
        Optional<?> user = userServiceClient.findById(professionalId.value());
        
        if (user == null) {
            log.warn("Usuário não encontrado para ID: {}", professionalId.value());
            return false;
        }
        
        if (user.isPresent()) {
            log.debug("Profissional validado com sucesso: {}", professionalId.value());
            return true;
        } else {
            log.warn("Usuário não encontrado para ID: {}", professionalId.value());
            return false;
        }
        
    } catch (Exception e) {
        log.error("Erro ao validar profissional {}: {}", professionalId.value(), e.getMessage(), e);
        throw new ProfessionalValidationException(
            "Erro ao validar profissional. Tente novamente."
        );
    }
    }
    
    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "fallbackProfessionalExists")
    public boolean professionalExists(ProfessionalId professionalId) {
        log.debug("Verificando existência do profissional com ID: {}", professionalId.value());
        
        try {
            Optional<?> user = userServiceClient.findById(professionalId.value());
            
            boolean exists = user != null && user.isPresent();
            
            if (exists) {
                log.debug("Profissional encontrado: {}", professionalId.value());
            } else {
                log.warn("Profissional não encontrado para ID: {}", professionalId.value());
            }
            
            return exists;
            
        } catch (Exception e) {
            log.error("Erro ao verificar existência do profissional {}: {}", professionalId.value(), e.getMessage(), e);
            throw new ProfessionalValidationException(
                "Erro ao verificar profissional. Tente novamente."
            );
        }
    }
    
    /**
     * Método de fallback para isValidAndActiveProfessional quando o Circuit Breaker está aberto.
     */
    public boolean fallbackIsValidAndActiveProfessional(ProfessionalId professionalId, Exception ex) {
        log.error("Circuit Breaker ativo - Fallback para validação do profissional {}: {}", 
                 professionalId.value(), ex.getMessage());
        
        throw new ProfessionalValidationException(
            "Serviço de validação de profissionais temporariamente indisponível. Tente novamente em alguns instantes."
        );
    }
    
    /**
     * Método de fallback para professionalExists quando o Circuit Breaker está aberto.
     */
    public boolean fallbackProfessionalExists(ProfessionalId professionalId, Exception ex) {
        log.error("Circuit Breaker ativo - Fallback para verificação do profissional {}: {}", 
                 professionalId.value(), ex.getMessage());
        
        throw new ProfessionalValidationException(
            "Serviço de validação de profissionais temporariamente indisponível. Tente novamente em alguns instantes."
        );
    }
}
