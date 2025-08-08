package com.clinicboard.business_service.domain.model.enums;

/**
 * Tipo de agendamento no domínio
 */
public enum AppointmentType {
    MARCACAO("Marcação"),
    REMARCACAO("Remarcação");

    private final String type;

    AppointmentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
