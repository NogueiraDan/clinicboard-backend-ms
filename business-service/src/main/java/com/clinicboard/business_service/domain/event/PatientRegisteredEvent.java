package com.clinicboard.business_service.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Evento de domínio disparado quando um paciente é registrado
 */
public class PatientRegisteredEvent {
    
    private final String patientId;
    private final String patientName;
    private final String patientEmail;
    private final String professionalId;
    private final LocalDateTime occurredAt;
    
    public PatientRegisteredEvent(String patientId, String patientName, String patientEmail, String professionalId) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.professionalId = professionalId;
        this.occurredAt = LocalDateTime.now();
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public String getPatientEmail() {
        return patientEmail;
    }
    
    public String getProfessionalId() {
        return professionalId;
    }
    
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientRegisteredEvent that = (PatientRegisteredEvent) o;
        return Objects.equals(patientId, that.patientId) && 
               Objects.equals(occurredAt, that.occurredAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(patientId, occurredAt);
    }
    
    @Override
    public String toString() {
        return "PatientRegisteredEvent{" +
                "patientId='" + patientId + '\'' +
                ", patientName='" + patientName + '\'' +
                ", patientEmail='" + patientEmail + '\'' +
                ", professionalId='" + professionalId + '\'' +
                ", occurredAt=" + occurredAt +
                '}';
    }
}
