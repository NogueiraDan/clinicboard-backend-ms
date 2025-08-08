package com.clinicboard.business_service.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * DTO para mensagem de cancelamento de agendamento via RabbitMQ
 */
public class AppointmentCancelledMessageDto {
    
    @JsonProperty("appointment_id")
    private String appointmentId;
    
    @JsonProperty("patient_id")
    private String patientId;
    
    @JsonProperty("professional_id")
    private String professionalId;
    
    @JsonProperty("original_date_time")
    private LocalDateTime originalDateTime;
    
    @JsonProperty("cancellation_reason")
    private String cancellationReason;
    
    @JsonProperty("event_type")
    private String eventType = "appointment.cancelled";
    
    @JsonProperty("occurred_at")
    private LocalDateTime occurredAt;
    
    // Constructors
    public AppointmentCancelledMessageDto() {}
    
    public AppointmentCancelledMessageDto(String appointmentId, String patientId, String professionalId,
                                        LocalDateTime originalDateTime, String cancellationReason) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.professionalId = professionalId;
        this.originalDateTime = originalDateTime;
        this.cancellationReason = cancellationReason;
        this.occurredAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getAppointmentId() {
        return appointmentId;
    }
    
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getProfessionalId() {
        return professionalId;
    }
    
    public void setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
    }
    
    public LocalDateTime getOriginalDateTime() {
        return originalDateTime;
    }
    
    public void setOriginalDateTime(LocalDateTime originalDateTime) {
        this.originalDateTime = originalDateTime;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }
}
