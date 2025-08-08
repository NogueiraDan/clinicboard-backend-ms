package com.clinicboard.business_service.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * DTO para mensagem de agendamento via RabbitMQ
 */
public class AppointmentScheduledMessageDto {
    
    @JsonProperty("appointment_id")
    private String appointmentId;
    
    @JsonProperty("patient_id")
    private String patientId;
    
    @JsonProperty("professional_id")
    private String professionalId;
    
    @JsonProperty("scheduled_date_time")
    private LocalDateTime scheduledDateTime;
    
    @JsonProperty("appointment_type")
    private String appointmentType;
    
    @JsonProperty("patient_name")
    private String patientName;
    
    @JsonProperty("professional_name")
    private String professionalName;
    
    @JsonProperty("observation")
    private String observation;
    
    @JsonProperty("event_type")
    private String eventType = "appointment.scheduled";
    
    @JsonProperty("occurred_at")
    private LocalDateTime occurredAt;
    
    // Constructors
    public AppointmentScheduledMessageDto() {}
    
    public AppointmentScheduledMessageDto(String appointmentId, String patientId, String professionalId, 
                                        LocalDateTime scheduledDateTime, String appointmentType) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.professionalId = professionalId;
        this.scheduledDateTime = scheduledDateTime;
        this.appointmentType = appointmentType;
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
    
    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }
    
    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }
    
    public String getAppointmentType() {
        return appointmentType;
    }
    
    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public String getProfessionalName() {
        return professionalName;
    }
    
    public void setProfessionalName(String professionalName) {
        this.professionalName = professionalName;
    }
    
    public String getObservation() {
        return observation;
    }
    
    public void setObservation(String observation) {
        this.observation = observation;
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
