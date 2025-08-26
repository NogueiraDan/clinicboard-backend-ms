package com.clinicboard.business_service.domain.event;

import com.clinicboard.business_service.domain.model.AppointmentId;
import com.clinicboard.business_service.domain.model.PatientId;
import com.clinicboard.business_service.domain.model.ProfessionalId;
import java.time.Instant;

/**
 * Evento de domínio disparado quando um agendamento é cancelado.
 * 
 * Este evento pode ser usado para:
 * - Notificar paciente e profissional sobre o cancelamento
 * - Liberar horário na agenda
 * - Registrar motivo do cancelamento
 * - Calcular métricas de cancelamento
 */
public record AppointmentCancelledEvent(
    AppointmentId appointmentId,
    PatientId patientId,
    ProfessionalId professionalId,
    String cancellationReason,
    Instant occurredOn
) implements DomainEvent {

    /**
     * Factory method para criar o evento
     */
    public static AppointmentCancelledEvent from(AppointmentId appointmentId,
                                               PatientId patientId,
                                               ProfessionalId professionalId,
                                               String reason) {
        return new AppointmentCancelledEvent(
            appointmentId,
            patientId,
            professionalId,
            reason,
            Instant.now()
        );
    }

    @Override
    public String getAggregateId() {
        return appointmentId != null ? appointmentId.value() : null;
    }

    /**
     * Retorna dados específicos do evento para processamento
     */
    public String getPatientIdValue() {
        return patientId.value();
    }

    public String getProfessionalIdValue() {
        return professionalId.value();
    }

    public boolean hasReason() {
        return cancellationReason != null && !cancellationReason.trim().isEmpty();
    }
}
