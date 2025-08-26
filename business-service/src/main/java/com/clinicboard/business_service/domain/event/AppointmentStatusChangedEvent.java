package com.clinicboard.business_service.domain.event;

import com.clinicboard.business_service.domain.model.AppointmentId;
import com.clinicboard.business_service.domain.model.AppointmentStatus;
import java.time.Instant;

/**
 * Evento de domínio disparado quando o status de um agendamento muda.
 * 
 * Este evento pode ser usado para:
 * - Rastrear histórico de mudanças de status
 * - Notificar sobre mudanças importantes
 * - Atualizar sistemas externos
 * - Calcular métricas de workflow
 */
public record AppointmentStatusChangedEvent(
    AppointmentId appointmentId,
    AppointmentStatus previousStatus,
    AppointmentStatus newStatus,
    Instant occurredOn
) implements DomainEvent {

    /**
     * Factory method para criar o evento
     */
    public static AppointmentStatusChangedEvent from(AppointmentId appointmentId,
                                                   AppointmentStatus previousStatus,
                                                   AppointmentStatus newStatus) {
        return new AppointmentStatusChangedEvent(
            appointmentId,
            previousStatus,
            newStatus,
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
    public String getPreviousStatusName() {
        return previousStatus.getDisplayName();
    }

    public String getNewStatusName() {
        return newStatus.getDisplayName();
    }

    public boolean isImportantTransition() {
        // Considera transições importantes aquelas que afetam o workflow
        return newStatus == AppointmentStatus.CONFIRMED ||
               newStatus == AppointmentStatus.CANCELLED ||
               newStatus == AppointmentStatus.COMPLETED ||
               newStatus == AppointmentStatus.NO_SHOW;
    }

    public boolean isFinalStatus() {
        return newStatus.isFinal();
    }
}
