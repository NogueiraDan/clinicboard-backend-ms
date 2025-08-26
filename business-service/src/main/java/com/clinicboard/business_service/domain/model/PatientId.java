package com.clinicboard.business_service.domain.model;

import com.clinicboard.business_service.domain.exception.DomainException;
import java.util.regex.Pattern;

/**
 * Value Object que representa o identificador único de um paciente.
 * 
 * Encapsula regras de validação e formatação específicas do domínio clínico.
 */
public record PatientId(String value) {
    
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
        Pattern.CASE_INSENSITIVE
    );

    public PatientId {
        validatePatientId(value);
    }

    private static void validatePatientId(String value) {
        if (value == null) {
            throw new InvalidPatientIdException("ID do paciente não pode ser nulo");
        }
        
        if (value.trim().isEmpty()) {
            throw new InvalidPatientIdException("ID do paciente não pode ser vazio");
        }
        
        if (!UUID_PATTERN.matcher(value).matches()) {
            throw new InvalidPatientIdException(
                "ID do paciente deve estar no formato UUID válido: " + value
            );
        }
    }

    /**
     * Factory method para criação segura
     */
    public static PatientId of(String value) {
        return new PatientId(value);
    }

    /**
     * Gera um novo ID usando UUID aleatório
     */
    public static PatientId generate() {
        return new PatientId(java.util.UUID.randomUUID().toString());
    }

    /**
     * Exceção específica para violações de ID de paciente
     */
    public static class InvalidPatientIdException extends DomainException {
        
        private static final String ERROR_CODE = "INVALID_PATIENT_ID";
        
        public InvalidPatientIdException(String message) {
            super(message);
        }

        @Override
        public String getErrorCode() {
            return ERROR_CODE;
        }
    }
}
