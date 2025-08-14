package com.clinicboard.business_service.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Evento de domínio disparado quando um agendamento é criado
 */
public class AppointmentScheduledEvent implements DomainEvent {
    
    public static final String ROUTING_KEY = "clinic.appointment.scheduled";
    
    private final String appointmentId;
    private final String patientId;
    private final String professionalId;
    private final LocalDateTime scheduledDateTime;
    private final String appointmentType;
    private final LocalDateTime occurredAt;
    
    public AppointmentScheduledEvent(String appointmentId, String patientId, String professionalId, 
                                   LocalDateTime scheduledDateTime, String appointmentType) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.professionalId = professionalId;
        this.scheduledDateTime = scheduledDateTime;
        this.appointmentType = appointmentType;
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
    
    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }
    
    public String getAppointmentType() {
        return appointmentType;
    }
    
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    /**
     * Retorna a routing key para este evento de domínio
     */
    public String getRoutingKey() {
        return ROUTING_KEY;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentScheduledEvent that = (AppointmentScheduledEvent) o;
        return Objects.equals(appointmentId, that.appointmentId) && 
               Objects.equals(occurredAt, that.occurredAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(appointmentId, occurredAt);
    }
    
    @Override
    public String toString() {
        return "AppointmentScheduledEvent{" +
                "appointmentId='" + appointmentId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", professionalId='" + professionalId + '\'' +
                ", scheduledDateTime=" + scheduledDateTime +
                ", appointmentType='" + appointmentType + '\'' +
                ", occurredAt=" + occurredAt +
                '}';
    }
}
