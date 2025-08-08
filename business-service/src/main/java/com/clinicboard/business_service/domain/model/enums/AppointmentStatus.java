package com.clinicboard.business_service.domain.model.enums;

/**
 * Status de um agendamento no domínio
 */
public enum AppointmentStatus {
    PENDING("Pendente"),
    SCHEDULED("Agendado"),
    CANCELED("Cancelado"),
    COMPLETED("Concluído"),
    NO_SHOW("Faltou"),
    RESCHEDULED("Remarcado");

    private final String status;

    AppointmentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
