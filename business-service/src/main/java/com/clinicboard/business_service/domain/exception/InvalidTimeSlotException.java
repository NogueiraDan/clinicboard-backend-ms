package com.clinicboard.business_service.domain.exception;

/**
 * Exceção lançada quando tentativa de agendamento é feita fora do horário comercial
 * ou com intervalo inadequado.
 * 
 * Representa violação das regras de negócio de disponibilidade temporal.
 */
public class InvalidTimeSlotException extends DomainException {

    private static final String ERROR_CODE = "INVALID_TIME_SLOT";

    public InvalidTimeSlotException(String timeSlot, String reason) {
        super(String.format(
            "Horário %s é inválido para agendamento: %s", 
            timeSlot, 
            reason
        ));
    }

    public InvalidTimeSlotException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}