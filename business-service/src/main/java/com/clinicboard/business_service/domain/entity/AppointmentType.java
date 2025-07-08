package com.clinicboard.business_service.domain.entity;

public enum AppointmentType {
    MARCACAO("Marcação"),
    REMARCACAO("Remarcação");

    private final String type;

    AppointmentType(String type) {
        this.type = type;
    }

    public String gettype() {
        return type;
    }
}
