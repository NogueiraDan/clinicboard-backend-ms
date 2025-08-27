package com.clinicboard.business_service.application.port.out;

import com.clinicboard.business_service.domain.model.ProfessionalId;

/**
 * Gateway para validação de profissionais.
 * 
 * Esta porta de saída permite validar a existência e status
 * de profissionais através de integração com o user-service.
 * 
 * Segue os princípios de Domain-Driven Design:
 * - Interface definida no domínio (Application Layer)
 * - Implementação na camada de infraestrutura
 * - Comunicação assíncrona ou síncrona dependendo da necessidade
 */
public interface ProfessionalValidationGateway {
    
    /**
     * Verifica se um profissional existe e está ativo no sistema.
     * 
     * Esta validação é crítica para manter a integridade referencial
     * entre pacientes e profissionais responsáveis.
     * 
     * @param professionalId ID do profissional a ser validado
     * @return true se o profissional existe e está ativo, false caso contrário
     * @throws ProfessionalValidationException em caso de erro na validação
     */
    boolean isValidAndActiveProfessional(ProfessionalId professionalId);
    
    /**
     * Verifica apenas se um profissional existe no sistema,
     * independente do seu status.
     * 
     * @param professionalId ID do profissional a ser verificado
     * @return true se o profissional existe, false caso contrário
     * @throws ProfessionalValidationException em caso de erro na verificação
     */
    boolean professionalExists(ProfessionalId professionalId);
}
