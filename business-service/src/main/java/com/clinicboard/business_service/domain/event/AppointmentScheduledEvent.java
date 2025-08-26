package com.clinicboard.business_service.domain.event;

import com.clinicboard.business_service.domain.model.AppointmentId;
import com.clinicboard.business_service.domain.model.PatientId;
import com.clinicboard.business_service.domain.model.ProfessionalId;
import com.clinicboard.business_service.domain.model.AppointmentTime;
import java.time.Instant;

/**
 * Evento de domínio disparado quando um novo agendamento é criado.
 * 
 * Este evento pode ser usado para:
 * - Enviar notificações para paciente e profissional
 * - Atualizar calendários externos
 * - Registrar logs de auditoria
 * - Integrar com sistemas de cobrança
 */
public record AppointmentScheduledEvent(
    AppointmentId appointmentId,
    PatientId patientId,
    ProfessionalId professionalId,
    AppointmentTime scheduledTime,
    Instant occurredOn
) implements DomainEvent {

    /**
     * Factory method para criar o evento a partir de um agendamento
     */
    public static AppointmentScheduledEvent from(AppointmentId appointmentId,
                                               PatientId patientId,
                                               ProfessionalId professionalId,
                                               AppointmentTime scheduledTime) {
        return new AppointmentScheduledEvent(
            appointmentId,
            patientId,
            professionalId,
            scheduledTime,
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

    public String getScheduledTimeFormatted() {
        return scheduledTime.getFormattedDateTime();
    }
}
