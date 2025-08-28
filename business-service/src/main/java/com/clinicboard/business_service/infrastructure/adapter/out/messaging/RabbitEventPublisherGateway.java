package com.clinicboard.business_service.infrastructure.adapter.out.messaging;

import com.clinicboard.business_service.application.port.out.EventPublisherGateway;
import com.clinicboard.business_service.domain.event.AppointmentScheduledEvent;
import com.clinicboard.business_service.domain.event.AppointmentCanceledEvent;
import com.clinicboard.business_service.domain.event.AppointmentStatusChangedEvent;
import com.clinicboard.business_service.domain.event.AppointmentRescheduledEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do gateway de eventos usando RabbitMQ com Circuit Breaker.
 * 
 * Esta classe é responsável por publicar eventos de domínio
 * relacionados a agendamentos em filas RabbitMQ, permitindo
 * que outros serviços sejam notificados sobre mudanças importantes.
 * 
 * Implementa Circuit Breaker pattern para resiliência:
 * - Detecta falhas no serviço de notificação
 * - Ativa fallback para DLQ quando necessário
 * - Protege contra cascading failures
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitEventPublisherGateway implements EventPublisherGateway {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.messaging.exchange.events:clinicboard.events}")
    private String eventsExchange;

    @Value("${app.messaging.routing-key.appointment-scheduled:appointment.scheduled}")
    private String appointmentScheduledRoutingKey;

    @Value("${app.messaging.routing-key.appointment-canceled:appointment.canceled}")
    private String appointmentCanceledRoutingKey;

    @Value("${app.messaging.routing-key.appointment-status-changed:appointment.status.changed}")
    private String appointmentStatusChangedRoutingKey;

    @Value("${app.messaging.routing-key.appointment-rescheduled:appointment.rescheduled}")
    private String appointmentRescheduledRoutingKey;

    @Value("${app.messaging.dlq.exchange:clinicboard.dlq}")
    private String dlqExchange;

    @Value("${app.messaging.dlq.routing-key:events.failed}")
    private String dlqRoutingKey;

    @Override
    @CircuitBreaker(name = "business-service", fallbackMethod = "publishAppointmentScheduledFallback")
    @Retry(name = "business-service")
    public void publishAppointmentScheduled(AppointmentScheduledEvent event) {
        try {
            log.debug("Publishing appointment scheduled event for appointment: {}", 
                event.getAggregateId());
            
            rabbitTemplate.convertAndSend(
                eventsExchange,
                appointmentScheduledRoutingKey,
                event
            );
            
            log.info("Successfully published AppointmentScheduledEvent for appointment: {}", 
                event.getAggregateId());
        } catch (Exception e) {
            log.error("Failed to publish AppointmentScheduledEvent for appointment: {}", 
                event.getAggregateId(), e);
            throw new RuntimeException("Failed to publish appointment scheduled event", e);
        }
    }

    @Override
    @CircuitBreaker(name = "business-service", fallbackMethod = "publishAppointmentCanceledFallback")
    @Retry(name = "business-service")
    public void publishAppointmentCanceled(AppointmentCanceledEvent event) {
        try {
            log.debug("Publishing appointment canceled event for appointment: {}", 
                event.appointmentId());
            
            rabbitTemplate.convertAndSend(
                eventsExchange,
                appointmentCanceledRoutingKey,
                event
            );
            
            log.info("Successfully published AppointmentCanceledEvent for appointment: {}", 
                event.appointmentId());
        } catch (Exception e) {
            log.error("Failed to publish AppointmentCanceledEvent for appointment: {}", 
                event.appointmentId(), e);
            throw new RuntimeException("Failed to publish appointment canceled event", e);
        }
    }

    @Override
    @CircuitBreaker(name = "business-service", fallbackMethod = "publishAppointmentStatusChangedFallback")
    @Retry(name = "business-service")
    public void publishAppointmentStatusChanged(AppointmentStatusChangedEvent event) {
        try {
            log.debug("Publishing appointment status changed event for appointment: {}", 
                event.getAggregateId());
            
            rabbitTemplate.convertAndSend(
                eventsExchange,
                appointmentStatusChangedRoutingKey,
                event
            );
            
            log.info("Successfully published AppointmentStatusChangedEvent for appointment: {} - {} -> {}", 
                event.getAggregateId(), 
                event.getPreviousStatusName(), 
                event.getNewStatusName());
        } catch (Exception e) {
            log.error("Failed to publish AppointmentStatusChangedEvent for appointment: {}", 
                event.getAggregateId(), e);
            throw new RuntimeException("Failed to publish appointment status changed event", e);
        }
    }

    @Override
    @CircuitBreaker(name = "business-service", fallbackMethod = "publishAppointmentRescheduledFallback")
    @Retry(name = "business-service")
    public void publishAppointmentRescheduled(AppointmentRescheduledEvent event) {
        try {
            log.debug("Publishing appointment rescheduled event for appointment: {}", 
                event.appointmentId());
            
            rabbitTemplate.convertAndSend(
                eventsExchange,
                appointmentRescheduledRoutingKey,
                event
            );
            
            log.info("Successfully published AppointmentRescheduledEvent for appointment: {} from {} to {}", 
                event.appointmentId(),
                event.getFormattedPreviousTime(),
                event.getFormattedNewTime());
        } catch (Exception e) {
            log.error("Failed to publish AppointmentRescheduledEvent for appointment: {}", 
                event.appointmentId(), e);
            throw new RuntimeException("Failed to publish appointment rescheduled event", e);
        }
    }

    // ========== MÉTODOS DE FALLBACK PARA CIRCUIT BREAKER ==========

    /**
     * Fallback para AppointmentScheduledEvent - envia para DLQ
     */
    public void publishAppointmentScheduledFallback(AppointmentScheduledEvent event, Exception ex) {
        log.warn("Circuit breaker activated for AppointmentScheduledEvent. Sending to DLQ. Appointment: {}", 
            event.getAggregateId(), ex);
        
        try {
            rabbitTemplate.convertAndSend(dlqExchange, dlqRoutingKey, event);
            log.info("AppointmentScheduledEvent sent to DLQ for appointment: {}", event.getAggregateId());
        } catch (Exception e) {
            log.error("Failed to send AppointmentScheduledEvent to DLQ for appointment: {}", 
                event.getAggregateId(), e);
        }
    }

    /**
     * Fallback para AppointmentCanceledEvent - envia para DLQ
     */
    public void publishAppointmentCanceledFallback(AppointmentCanceledEvent event, Exception ex) {
        log.warn("Circuit breaker activated for AppointmentCanceledEvent. Sending to DLQ. Appointment: {}", 
            event.appointmentId(), ex);
        
        try {
            rabbitTemplate.convertAndSend(dlqExchange, dlqRoutingKey, event);
            log.info("AppointmentCanceledEvent sent to DLQ for appointment: {}", event.appointmentId());
        } catch (Exception e) {
            log.error("Failed to send AppointmentCanceledEvent to DLQ for appointment: {}", 
                event.appointmentId(), e);
        }
    }

    /**
     * Fallback para AppointmentStatusChangedEvent - envia para DLQ
     */
    public void publishAppointmentStatusChangedFallback(AppointmentStatusChangedEvent event, Exception ex) {
        log.warn("Circuit breaker activated for AppointmentStatusChangedEvent. Sending to DLQ. Appointment: {}", 
            event.getAggregateId(), ex);
        
        try {
            rabbitTemplate.convertAndSend(dlqExchange, dlqRoutingKey, event);
            log.info("AppointmentStatusChangedEvent sent to DLQ for appointment: {}", event.getAggregateId());
        } catch (Exception e) {
            log.error("Failed to send AppointmentStatusChangedEvent to DLQ for appointment: {}", 
                event.getAggregateId(), e);
        }
    }

    /**
     * Fallback para AppointmentRescheduledEvent - envia para DLQ
     */
    public void publishAppointmentRescheduledFallback(AppointmentRescheduledEvent event, Exception ex) {
        log.warn("Circuit breaker activated for AppointmentRescheduledEvent. Sending to DLQ. Appointment: {}", 
            event.appointmentId(), ex);
        
        try {
            rabbitTemplate.convertAndSend(dlqExchange, dlqRoutingKey, event);
            log.info("AppointmentRescheduledEvent sent to DLQ for appointment: {}", event.appointmentId());
        } catch (Exception e) {
            log.error("Failed to send AppointmentRescheduledEvent to DLQ for appointment: {}", 
                event.appointmentId(), e);
        }
    }
}
