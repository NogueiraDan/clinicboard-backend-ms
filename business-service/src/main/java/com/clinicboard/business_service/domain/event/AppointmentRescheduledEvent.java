package com.clinicboard.business_service.domain.event;

import com.clinicboard.business_service.domain.model.AppointmentId;
import com.clinicboard.business_service.domain.model.AppointmentTime;
import com.clinicboard.business_service.domain.model.PatientId;
import com.clinicboard.business_service.domain.model.ProfessionalId;

import java.time.Instant;

/**
 * Evento de domínio que representa o reagendamento de uma consulta.
 * 
 * Este evento é gerado quando um agendamento existente é remarcado
 * para uma nova data/horário, preservando os dados originais e novos
 * para permitir auditoria e notificações adequadas.
 */
public record AppointmentRescheduledEvent(
    AppointmentId appointmentId,
    PatientId patientId, 
    ProfessionalId professionalId,
    AppointmentTime previousTime,
    AppointmentTime newTime,
    String reason,
    Instant rescheduledAt
) implements DomainEvent {

    public AppointmentRescheduledEvent {
        if (appointmentId == null) {
            throw new IllegalArgumentException("Appointment ID cannot be null");
        }
        if (patientId == null) {
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        if (professionalId == null) {
            throw new IllegalArgumentException("Professional ID cannot be null");
        }
        if (previousTime == null) {
            throw new IllegalArgumentException("Previous time cannot be null");
        }
        if (newTime == null) {
            throw new IllegalArgumentException("New time cannot be null");
        }
        if (previousTime.equals(newTime)) {
            throw new IllegalArgumentException("New time must be different from previous time");
        }
        if (rescheduledAt == null) {
            rescheduledAt = Instant.now();
        }
    }

    @Override
    public String getAggregateId() {
        return appointmentId.value();
    }

    @Override
    public Instant occurredOn() {
        return rescheduledAt;
    }

    @Override
    public String getEventType() {
        return "AppointmentRescheduled";
    }

    /**
     * Factory method para criar o evento
     */
    public static AppointmentRescheduledEvent of(
            AppointmentId appointmentId,
            PatientId patientId,
            ProfessionalId professionalId,
            AppointmentTime previousTime,
            AppointmentTime newTime,
            String reason) {
        return new AppointmentRescheduledEvent(
            appointmentId,
            patientId,
            professionalId,
            previousTime,
            newTime,
            reason,
            Instant.now()
        );
    }

    // Getters utilitários para facilitar serialização/logging
    public String getFormattedPreviousTime() {
        return previousTime.getFormattedDateTime();
    }

    public String getFormattedNewTime() {
        return newTime.getFormattedDateTime();
    }

    public String getSummary() {
        return String.format("Appointment %s rescheduled from %s to %s. Reason: %s",
            appointmentId.value(),
            getFormattedPreviousTime(),
            getFormattedNewTime(),
            reason != null ? reason : "Not specified"
        );
    }
}
