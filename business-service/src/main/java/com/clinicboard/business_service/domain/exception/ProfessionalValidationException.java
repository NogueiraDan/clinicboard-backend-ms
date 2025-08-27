package com.clinicboard.business_service.domain.exception;

/**
 * Exceção lançada quando há problemas na validação de profissionais.
 * 
 * Esta exceção é específica para casos onde:
 * - Profissional não existe no sistema
 * - Profissional existe mas está inativo
 * - Falha na comunicação com o serviço de validação
 */
public class ProfessionalValidationException extends DomainException {
    
    public ProfessionalValidationException(String message) {
        super(message);
    }
    
    public ProfessionalValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public String getErrorCode() {
        return "PROFESSIONAL_VALIDATION_ERROR";
    }
    
    /**
     * Factory method para profissional não encontrado.
     */
    public static ProfessionalValidationException professionalNotFound(String professionalId) {
        return new ProfessionalValidationException(
            String.format("Profissional não encontrado: %s", professionalId)
        );
    }
    
    /**
     * Factory method para profissional inativo.
     */
    public static ProfessionalValidationException professionalInactive(String professionalId) {
        return new ProfessionalValidationException(
            String.format("Profissional inativo: %s", professionalId)
        );
    }
    
    /**
     * Factory method para erro de comunicação.
     */
    public static ProfessionalValidationException communicationError(String professionalId, Throwable cause) {
        return new ProfessionalValidationException(
            String.format("Erro ao validar profissional %s: %s", professionalId, cause.getMessage()),
            cause
        );
    }
}
