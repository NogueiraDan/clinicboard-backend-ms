package com.clinicboard.business_service.infrastructure.persistence.entity;

public enum AppointmentTypeEntity {
    MARCACAO("Marcação"),
    REMARCACAO("Remarcação");

    private final String type;

    AppointmentTypeEntity(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
