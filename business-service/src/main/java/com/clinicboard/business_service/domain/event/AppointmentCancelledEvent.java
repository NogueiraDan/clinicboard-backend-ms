package com.clinicboard.business_service.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Evento de domínio disparado quando um agendamento é cancelado
 */
public class AppointmentCancelledEvent {
    
    private final String appointmentId;
    private final String patientId;
    private final String professionalId;
    private final LocalDateTime originalDateTime;
    private final String reason;
    private final LocalDateTime occurredAt;
    
    public AppointmentCancelledEvent(String appointmentId, String patientId, String professionalId, 
                                   LocalDateTime originalDateTime, String reason) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.professionalId = professionalId;
        this.originalDateTime = originalDateTime;
        this.reason = reason;
        this.occurredAt = LocalDateTime.now();
    }
    
    public String getAppointmentId() {
        return appointmentId;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public String getProfessionalId() {
        return professionalId;
    }
    
    public LocalDateTime getOriginalDateTime() {
        return originalDateTime;
    }
    
    public String getReason() {
        return reason;
    }
    
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentCancelledEvent that = (AppointmentCancelledEvent) o;
        return Objects.equals(appointmentId, that.appointmentId) && 
               Objects.equals(occurredAt, that.occurredAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(appointmentId, occurredAt);
    }
    
    @Override
    public String toString() {
        return "AppointmentCancelledEvent{" +
                "appointmentId='" + appointmentId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", professionalId='" + professionalId + '\'' +
                ", originalDateTime=" + originalDateTime +
                ", reason='" + reason + '\'' +
                ", occurredAt=" + occurredAt +
                '}';
    }
}
