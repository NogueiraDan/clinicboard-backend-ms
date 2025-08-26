package com.clinicboard.business_service.application.port.out;

import com.clinicboard.business_service.domain.event.AppointmentScheduledEvent;
import com.clinicboard.business_service.domain.event.AppointmentCanceledEvent;
import com.clinicboard.business_service.domain.event.AppointmentStatusChangedEvent;

/**
 * Gateway para publicação de eventos de negócio.
 * Esta interface define o contrato para publicação de eventos de domínio
 * que notificam outras partes do sistema sobre mudanças importantes
 * no contexto de agendamentos.
 * 
 * <p>Implementações desta interface devem garantir:
 * - Entrega confiável dos eventos
 * - Serialização adequada dos dados
 * - Tratamento de falhas na publicação
 * 
 * @since 1.0
 */
public interface EventPublisherGateway {
    
    /**
     * Publica um evento de agendamento criado.
     * 
     * @param event o evento de agendamento criado
     */
    void publishAppointmentScheduled(AppointmentScheduledEvent event);
    
    /**
     * Publica um evento de agendamento cancelado.
     * 
     * @param event o evento de agendamento cancelado
     */
    void publishAppointmentCanceled(AppointmentCanceledEvent event);
    
    /**
     * Publica um evento de mudança de status do agendamento.
     * 
     * @param event o evento de mudança de status
     */
    void publishAppointmentStatusChanged(AppointmentStatusChangedEvent event);
}
