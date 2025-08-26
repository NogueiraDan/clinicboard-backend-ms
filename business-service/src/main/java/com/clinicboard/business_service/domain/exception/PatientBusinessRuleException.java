package com.clinicboard.business_service.domain.exception;

/**
 * Exceção lançada quando paciente tenta realizar ação não permitida por seu estado atual.
 * 
 * Exemplos: paciente inativo tentando agendar, múltiplos agendamentos no mesmo dia, etc.
 */
public class PatientBusinessRuleException extends DomainException {

    private static final String ERROR_CODE = "PATIENT_BUSINESS_RULE_VIOLATION";

    public PatientBusinessRuleException(String patientId, String rule) {
        super(String.format(
            "Paciente %s violou regra de negócio: %s", 
            patientId, 
            rule
        ));
    }

    public PatientBusinessRuleException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
