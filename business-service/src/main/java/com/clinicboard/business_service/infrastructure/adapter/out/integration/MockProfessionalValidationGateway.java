package com.clinicboard.business_service.infrastructure.adapter.out.integration;

import com.clinicboard.business_service.application.port.out.ProfessionalValidationGateway;
import com.clinicboard.business_service.domain.model.ProfessionalId;
import com.clinicboard.business_service.domain.exception.ProfessionalValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementação temporária/mock do gateway de validação de profissionais.
 * 
 * IMPORTANTE: Esta é uma implementação temporária para não quebrar a compilação.
 * Em um ambiente real, este adaptador deve:
 * 
 * 1. Fazer chamadas HTTP para o user-service
 * 2. Implementar Circuit Breaker pattern
 * 3. Ter cache para reduzir latência
 * 4. Implementar retry com backoff
 * 5. Ter fallback strategies
 * 
 * Para produção, considere usar:
 * - OpenFeign para chamadas HTTP
 * - Resilience4j para Circuit Breaker
 * - Redis para cache
 * - Observabilidade com spans distribuídos
 */
@Component
@Slf4j
public class MockProfessionalValidationGateway implements ProfessionalValidationGateway {

    @Override
    public boolean isValidAndActiveProfessional(ProfessionalId professionalId) {
        log.info("Validando profissional: {}", professionalId.value());
        
        try {
            // MOCK: Para desenvolvimento, aceita qualquer ID que comece com "PROF-"
            if (professionalId.value() != null && professionalId.value().startsWith("PROF-")) {
                log.info("Profissional {} validado com sucesso (MOCK)", professionalId.value());
                return true;
            }
            
            log.warn("Profissional {} rejeitado - ID inválido (MOCK)", professionalId.value());
            return false;
            
        } catch (Exception e) {
            log.error("Erro ao validar profissional {}: {}", professionalId.value(), e.getMessage(), e);
            throw ProfessionalValidationException.communicationError(professionalId.value(), e);
        }
    }

    @Override
    public boolean professionalExists(ProfessionalId professionalId) {
        log.info("Verificando existência do profissional: {}", professionalId.value());
        
        try {
            // MOCK: Para desenvolvimento, aceita qualquer ID que comece com "PROF-"
            boolean exists = professionalId.value() != null && professionalId.value().startsWith("PROF-");
            
            log.info("Profissional {} existe: {} (MOCK)", professionalId.value(), exists);
            return exists;
            
        } catch (Exception e) {
            log.error("Erro ao verificar profissional {}: {}", professionalId.value(), e.getMessage(), e);
            throw ProfessionalValidationException.communicationError(professionalId.value(), e);
        }
    }
}
