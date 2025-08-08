package com.clinicboard.business_service.domain.model;

import com.clinicboard.business_service.domain.model.enums.AppointmentStatus;
import com.clinicboard.business_service.domain.model.enums.AppointmentType;
import com.clinicboard.business_service.domain.event.AppointmentCancelledEvent;
import com.clinicboard.business_service.domain.event.AppointmentScheduledEvent;
import com.clinicboard.business_service.domain.model.valueobjects.AppointmentTime;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDateTime;

/**
 * Agregado Appointment - representa um agendamento no contexto médico
 * PURO - sem dependências de infraestrutura
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Appointment extends AbstractAggregateRoot<Appointment> {

    private String id;
    private AppointmentTime appointmentTime;
    private AppointmentStatus status = AppointmentStatus.PENDING;
    private AppointmentType type = AppointmentType.MARCACAO;
    private ProfessionalId professionalId;
    private String patientId;
    private String observation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Construtor para criação do agregado
    public Appointment(AppointmentTime appointmentTime, ProfessionalId professionalId, 
                      String patientId, String observation, AppointmentType type) {
        validateAppointmentTime(appointmentTime);
        validateProfessionalId(professionalId);
        validatePatientId(patientId);
        
        this.appointmentTime = appointmentTime;
        this.professionalId = professionalId;
        this.patientId = patientId;
        this.observation = observation;
        this.type = type != null ? type : AppointmentType.MARCACAO;
        this.status = AppointmentStatus.PENDING;
    }

    // Construtor para reconstrução a partir da infraestrutura (usado pelos mappers)
    public Appointment(String id, AppointmentTime appointmentTime, AppointmentStatus status, 
                      AppointmentType type, ProfessionalId professionalId, String patientId, 
                      String observation, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.type = type;
        this.professionalId = professionalId;
        this.patientId = patientId;
        this.observation = observation;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Comportamentos de domínio
    public void schedule() {
        if (status != AppointmentStatus.PENDING) {
            throw new IllegalStateException("Apenas agendamentos pendentes podem ser confirmados");
        }
        
        this.status = AppointmentStatus.SCHEDULED;
        
        // Registra evento de domínio
        registerEvent(new AppointmentScheduledEvent(
            this.id, 
            this.patientId, 
            this.professionalId.getValue(), 
            this.appointmentTime.getDateTime(),
            this.type.name()
        ));
    }

    public void cancel(String reason) {
        if (!canBeCancelled()) {
            throw new IllegalStateException("Este agendamento não pode ser cancelado");
        }
        
        LocalDateTime originalDateTime = this.appointmentTime.getDateTime();
        this.status = AppointmentStatus.CANCELED;
        
        // Registra evento de domínio
        registerEvent(new AppointmentCancelledEvent(
            this.id, 
            this.patientId, 
            this.professionalId.getValue(), 
            originalDateTime,
            reason != null ? reason : "Não informado"
        ));
    }

    public void reschedule(AppointmentTime newAppointmentTime, String reason) {
        if (!canBeRescheduled()) {
            throw new IllegalStateException("Este agendamento não pode ser reagendado");
        }
        
        validateAppointmentTime(newAppointmentTime);
        
        // Primeiro cancela o agendamento atual
        cancel(reason);
        
        // Cria novo agendamento com status remarcado
        this.appointmentTime = newAppointmentTime;
        this.status = AppointmentStatus.RESCHEDULED;
        this.type = AppointmentType.REMARCACAO;
    }

    public void complete() {
        if (status != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException("Apenas agendamentos confirmados podem ser concluídos");
        }
        
        this.status = AppointmentStatus.COMPLETED;
    }

    public void markAsNoShow() {
        if (status != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException("Apenas agendamentos confirmados podem ser marcados como falta");
        }
        
        this.status = AppointmentStatus.NO_SHOW;
    }

    public void updateObservation(String newObservation) {
        this.observation = newObservation;
    }

    // Consultas de negócio
    public boolean canBeCancelled() {
        return status == AppointmentStatus.PENDING || status == AppointmentStatus.SCHEDULED;
    }

    public boolean canBeRescheduled() {
        return status == AppointmentStatus.PENDING || status == AppointmentStatus.SCHEDULED;
    }

    public boolean isScheduledFor(LocalDateTime dateTime) {
        return appointmentTime.getDateTime().equals(dateTime);
    }

    public boolean isInTimeRange(LocalDateTime startRange, LocalDateTime endRange) {
        return appointmentTime.isWithinRange(startRange, endRange);
    }

    public boolean isManagedBy(ProfessionalId professionalId) {
        return this.professionalId.equals(professionalId);
    }

    public boolean isForPatient(String patientId) {
        return this.patientId.equals(patientId);
    }

    public boolean isSameDay(LocalDateTime dateTime) {
        return appointmentTime.isSameDay(dateTime);
    }

    // Getters para compatibilidade com código existente
    public LocalDateTime getDate() {
        return appointmentTime.getDateTime();
    }

    public String getProfessionalIdValue() {
        return professionalId.getValue();
    }

    // Validações de negócio
    private void validateAppointmentTime(AppointmentTime appointmentTime) {
        if (appointmentTime == null) {
            throw new IllegalArgumentException("Horário do agendamento é obrigatório");
        }
    }

    private void validateProfessionalId(ProfessionalId professionalId) {
        if (professionalId == null) {
            throw new IllegalArgumentException("ID do profissional é obrigatório");
        }
    }

    private void validatePatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do paciente é obrigatório");
        }
    }
}
