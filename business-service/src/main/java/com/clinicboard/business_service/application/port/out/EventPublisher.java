package com.clinicboard.business_service.application.port.out;

import com.clinicboard.business_service.domain.event.AppointmentScheduledEvent;
import com.clinicboard.business_service.domain.event.AppointmentCancelledEvent;
import com.clinicboard.business_service.domain.event.AppointmentStatusChangedEvent;

/**
 * Porta de saída para publicação de eventos de domínio.
 * 
 * Define o contrato que a infraestrutura deve implementar
 * para publicação de eventos entre bounded contexts.
 * 
 * Princípios DDD aplicados:
 * - Interface na camada de aplicação (Dependency Inversion)
 * - Contrato agnóstico à tecnologia de mensageria
 * - Separação clara entre eventos de domínio e infraestrutura
 */
public interface EventPublisher {

    /**
     * Publica evento de agendamento criado.
     * 
     * @param event evento de agendamento criado
     */
    void publishAppointmentScheduled(AppointmentScheduledEvent event);

    /**
     * Publica evento de agendamento cancelado.
     * 
     * @param event evento de agendamento cancelado
     */
    void publishAppointmentCancelled(AppointmentCancelledEvent event);

    /**
     * Publica evento de mudança de status do agendamento.
     * 
     * @param event evento de mudança de status
     */
    void publishAppointmentStatusChanged(AppointmentStatusChangedEvent event);
}
