package com.clinicboard.business_service.domain.exception;

/**
 * Exceção lançada quando há tentativa de agendar consulta em horário já ocupado.
 * Representa violação da regra de negócio: "Não pode haver sobreposição de agendamentos"
 */
public class AppointmentConflictException extends DomainException {

    private static final String ERROR_CODE = "APPOINTMENT_CONFLICT";

    public AppointmentConflictException(String professionalId, String dateTime) {
        super(String.format(
            "Profissional %s já possui agendamento conflitante no horário %s", 
            professionalId, 
            dateTime
        ));
    }

    public AppointmentConflictException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
