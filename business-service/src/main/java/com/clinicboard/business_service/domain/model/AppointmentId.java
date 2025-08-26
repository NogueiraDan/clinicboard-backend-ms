package com.clinicboard.business_service.domain.model;

import com.clinicboard.business_service.domain.exception.DomainException;
import java.util.regex.Pattern;

/**
 * Value Object que representa o identificador único de um agendamento.
 * 
 * Princípios DDD aplicados:
 * - Imutável (final fields)
 * - Igualdade por valor (equals/hashCode)
 * - Validação no construtor
 * - Sem dependências de infraestrutura
 */
public record AppointmentId(String value) {
    
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
        Pattern.CASE_INSENSITIVE
    );

    public AppointmentId {
        validateAppointmentId(value);
    }

    private static void validateAppointmentId(String value) {
        if (value == null) {
            throw new InvalidAppointmentIdException("ID do agendamento não pode ser nulo");
        }
        
        if (value.trim().isEmpty()) {
            throw new InvalidAppointmentIdException("ID do agendamento não pode ser vazio");
        }
        
        if (!UUID_PATTERN.matcher(value).matches()) {
            throw new InvalidAppointmentIdException(
                "ID do agendamento deve estar no formato UUID válido: " + value
            );
        }
    }

    /**
     * Gera um novo ID de agendamento usando UUID aleatório
     */
    public static AppointmentId generate() {
        return new AppointmentId(java.util.UUID.randomUUID().toString());
    }

    /**
     * Factory method para criação segura a partir de string
     */
    public static AppointmentId of(String value) {
        return new AppointmentId(value);
    }

    /**
     * Exceção específica para violações de ID de agendamento
     */
    public static class InvalidAppointmentIdException extends DomainException {
        
        private static final String ERROR_CODE = "INVALID_APPOINTMENT_ID";
        
        public InvalidAppointmentIdException(String message) {
            super(message);
        }

        @Override
        public String getErrorCode() {
            return ERROR_CODE;
        }
    }
}
