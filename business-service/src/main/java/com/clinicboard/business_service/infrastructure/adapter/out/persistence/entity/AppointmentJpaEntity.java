package com.clinicboard.business_service.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidade JPA para persistência de dados de consultas.
 * Representa a estrutura de dados no banco, independente do modelo de domínio.
 */
@Entity
@Table(name = "agendamentos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AppointmentJpaEntity {

    @Id
    @Column(name = "appointment_id", length = 36)
    private String appointmentId;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "professional_id", nullable = false, length = 36)
    private String professionalId;

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", nullable = false, length = 50)
    private AppointmentTypeEnum appointmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private AppointmentStatusEnum status = AppointmentStatusEnum.SCHEDULED;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Enum para tipos de consulta na persistência.
     */
    public enum AppointmentTypeEnum {
        FIRST_CONSULTATION,
        FOLLOW_UP,
        EMERGENCY,
        PROCEDURE,
        EXAM,
        VACCINATION,
        TELEMEDICINE
    }

    /**
     * Enum para status da consulta na persistência.
     */
    public enum AppointmentStatusEnum {
        SCHEDULED,
        CONFIRMED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        NO_SHOW
    }

    // Métodos explícitos para garantir compatibilidade caso Lombok falhe
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    
    public String getProfessionalId() { return professionalId; }
    public void setProfessionalId(String professionalId) { this.professionalId = professionalId; }
    
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    
    public AppointmentTypeEnum getAppointmentType() { return appointmentType; }
    public void setAppointmentType(AppointmentTypeEnum appointmentType) { this.appointmentType = appointmentType; }
    
    public AppointmentStatusEnum getStatus() { return status; }
    public void setStatus(AppointmentStatusEnum status) { this.status = status; }
    
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
