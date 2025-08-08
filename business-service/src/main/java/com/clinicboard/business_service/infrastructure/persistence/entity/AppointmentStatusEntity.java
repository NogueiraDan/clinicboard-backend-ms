package com.clinicboard.business_service.infrastructure.persistence.entity;

public enum AppointmentStatusEntity {
    PENDING("Pendente"),
    SCHEDULED("Agendado"),
    CANCELED("Cancelado"),
    COMPLETED("Concluído"),
    NO_SHOW("Faltou"),
    RESCHEDULED("Remarcado");

    private final String status;

    AppointmentStatusEntity(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
