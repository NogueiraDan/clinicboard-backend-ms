package com.clinicboard.business_service.domain.model;

import com.clinicboard.business_service.domain.exception.DomainException;
import java.util.regex.Pattern;

/**
 * Value Object que representa o identificador único de um profissional de saúde.
 * 
 * Valida e encapsula regras específicas para identificação de profissionais.
 */
public record ProfessionalId(String value) {
    
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
        Pattern.CASE_INSENSITIVE
    );

    public ProfessionalId {
        validateProfessionalId(value);
    }

    private static void validateProfessionalId(String value) {
        if (value == null) {
            throw new InvalidProfessionalIdException("ID do profissional não pode ser nulo");
        }
        
        if (value.trim().isEmpty()) {
            throw new InvalidProfessionalIdException("ID do profissional não pode ser vazio");
        }
        
        // if (!UUID_PATTERN.matcher(value).matches()) {
        //     throw new InvalidProfessionalIdException(
        //         "ID do profissional deve estar no formato UUID válido: " + value
        //     );
        // }
    }

    /**
     * Factory method para criação segura
     */
    public static ProfessionalId of(String value) {
        return new ProfessionalId(value);
    }

    /**
     * Gera um novo ID usando UUID aleatório
     */
    public static ProfessionalId generate() {
        return new ProfessionalId(java.util.UUID.randomUUID().toString());
    }

    /**
     * Exceção específica para violações de ID de profissional
     */
    public static class InvalidProfessionalIdException extends DomainException {
        
        private static final String ERROR_CODE = "INVALID_PROFESSIONAL_ID";
        
        public InvalidProfessionalIdException(String message) {
            super(message);
        }

        @Override
        public String getErrorCode() {
            return ERROR_CODE;
        }
    }
}
