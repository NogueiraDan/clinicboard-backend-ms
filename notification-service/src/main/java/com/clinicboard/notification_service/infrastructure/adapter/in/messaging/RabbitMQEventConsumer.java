package com.clinicboard.notification_service.infrastructure.adapter.in.messaging;

import com.clinicboard.notification_service.application.port.in.ProcessAppointmentEventUseCase;
import com.clinicboard.notification_service.domain.event.AppointmentScheduledEvent;
import com.clinicboard.notification_service.domain.event.AppointmentCanceledEvent;
import com.clinicboard.notification_service.domain.event.AppointmentStatusChangedEvent;
import com.clinicboard.notification_service.domain.event.AppointmentRescheduledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada para consumo de eventos via RabbitMQ.
 * 
 * Este adaptador recebe eventos publicados pelo business-service
 * e delega o processamento para os casos de uso apropriados.
 * 
 * Segue o padrão Hexagonal Architecture onde os adaptadores de entrada
 * são responsáveis por receber requisições externas e convertê-las
 * em chamadas para as portas de entrada da aplicação.
 * 
 * Implementa Circuit Breaker pattern através de Dead Letter Queue (DLQ)
 * para tratamento de falhas e reprocessamento de mensagens.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEventConsumer {
    
    private final ProcessAppointmentEventUseCase processAppointmentEventUseCase;
    
    /**
     * Consome eventos de agendamento criado
     */
    @RabbitListener(queues = "${app.messaging.queue.appointment-scheduled}")
    public void handleAppointmentScheduled(AppointmentScheduledEvent event) {
        log.info("Received AppointmentScheduledEvent for appointment: {}", event.getAggregateId());
        
        try {
            processAppointmentEventUseCase.processAppointmentScheduled(event);
            log.debug("Successfully processed AppointmentScheduledEvent for appointment: {}", event.getAggregateId());
        } catch (Exception e) {
            log.error("Failed to process AppointmentScheduledEvent for appointment: {}. Message will be sent to DLQ", 
                event.getAggregateId(), e);
            // O RabbitMQ automaticamente enviará para DLQ em caso de exceção
            throw e;
        }
    }
    
    /**
     * Consome eventos de agendamento cancelado
     */
    @RabbitListener(queues = "${app.messaging.queue.appointment-canceled}")
    public void handleAppointmentCanceled(AppointmentCanceledEvent event) {
        log.info("Received AppointmentCanceledEvent for appointment: {}", event.getAppointmentId());
        
        try {
            processAppointmentEventUseCase.processAppointmentCanceled(event);
            log.debug("Successfully processed AppointmentCanceledEvent for appointment: {}", event.getAppointmentId());
        } catch (Exception e) {
            log.error("Failed to process AppointmentCanceledEvent for appointment: {}. Message will be sent to DLQ", 
                event.getAppointmentId(), e);
            throw e;
        }
    }
    
    /**
     * Consome eventos de mudança de status
     */
    @RabbitListener(queues = "${app.messaging.queue.appointment-status-changed}")
    public void handleAppointmentStatusChanged(AppointmentStatusChangedEvent event) {
        log.info("Received AppointmentStatusChangedEvent for appointment: {} - {} -> {}", 
            event.getAggregateId(), event.getPreviousStatusName(), event.getNewStatusName());
        
        try {
            processAppointmentEventUseCase.processAppointmentStatusChanged(event);
            log.debug("Successfully processed AppointmentStatusChangedEvent for appointment: {}", event.getAggregateId());
        } catch (Exception e) {
            log.error("Failed to process AppointmentStatusChangedEvent for appointment: {}. Message will be sent to DLQ", 
                event.getAggregateId(), e);
            throw e;
        }
    }
    
    /**
     * Consome eventos de reagendamento
     */
    @RabbitListener(queues = "${app.messaging.queue.appointment-rescheduled}")
    public void handleAppointmentRescheduled(AppointmentRescheduledEvent event) {
        log.info("Received AppointmentRescheduledEvent for appointment: {} from {} to {}", 
            event.getAppointmentId(), event.getFormattedPreviousTime(), event.getFormattedNewTime());
        
        try {
            processAppointmentEventUseCase.processAppointmentRescheduled(event);
            log.debug("Successfully processed AppointmentRescheduledEvent for appointment: {}", event.getAppointmentId());
        } catch (Exception e) {
            log.error("Failed to process AppointmentRescheduledEvent for appointment: {}. Message will be sent to DLQ", 
                event.getAppointmentId(), e);
            throw e;
        }
    }
    
    /**
     * Consome mensagens da Dead Letter Queue para análise e possível reprocessamento
     */
    @RabbitListener(queues = "${app.messaging.queue.dlq}")
    public void handleFailedEvents(Object failedEvent) {
        log.warn("Received failed event in DLQ: {}", failedEvent.getClass().getSimpleName());
        log.warn("Failed event details: {}", failedEvent.toString());
        
        // TODO: Implementar lógica de reprocessamento ou alerta para administradores
        // Possíveis ações:
        // 1. Tentar reprocessar após um tempo
        // 2. Enviar alerta para administradores
        // 3. Armazenar em banco para análise posterior
        // 4. Aplicar estratégias de retry com backoff
    }
}
