package com.clinicboard.business_service.domain.event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Evento que representa o cancelamento de um agendamento.
 * Este evento é disparado quando um agendamento é cancelado,
 * permitindo que outros contextos ou serviços sejam notificados
 * sobre essa mudança de estado.
 * 
 * @param appointmentId O identificador único do agendamento cancelado
 * @param patientId O identificador do paciente
 * @param professionalId O identificador do profissional
 * @param scheduledTime O horário que estava agendado
 * @param canceledAt O momento do cancelamento
 * @param eventId O identificador único do evento
 * @param occurredAt O momento em que o evento ocorreu
 * 
 * @since 1.0
 */
public record AppointmentCanceledEvent(
    String appointmentId,
    String patientId,
    String professionalId,
    LocalDateTime scheduledTime,
    LocalDateTime canceledAt,
    String eventId,
    Instant occurredAt
) implements DomainEvent {
    
    public static AppointmentCanceledEvent of(
            String appointmentId,
            String patientId,
            String professionalId,
            LocalDateTime scheduledTime,
            LocalDateTime canceledAt) {
        return new AppointmentCanceledEvent(
            appointmentId,
            patientId,
            professionalId,
            scheduledTime,
            canceledAt,
            java.util.UUID.randomUUID().toString(),
            Instant.now()
        );
    }

    @Override
    public String getAggregateId() {
        return appointmentId;
    }

    @Override
    public Instant occurredOn() {
        return occurredAt;
    }
}
