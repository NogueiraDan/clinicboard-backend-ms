package com.clinicboard.business_service.infrastructure.adapter.out.messaging;

import com.clinicboard.business_service.application.port.out.EventPublisher;
import com.clinicboard.business_service.domain.event.AppointmentScheduledEvent;
import com.clinicboard.business_service.domain.event.AppointmentCancelledEvent;
import com.clinicboard.business_service.domain.event.AppointmentStatusChangedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adaptador para publicação de eventos de domínio via RabbitMQ.
 * 
 * Implementa a porta de saída EventPublisher, convertendo eventos
 * de domínio em mensagens para o sistema de mensageria.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${app.messaging.exchanges.business-events:business.events}")
    private String businessEventsExchange;
    
    @Value("${app.messaging.routing-keys.appointment-scheduled:appointment.scheduled}")
    private String appointmentScheduledRoutingKey;
    
    @Value("${app.messaging.routing-keys.appointment-cancelled:appointment.cancelled}")
    private String appointmentCancelledRoutingKey;
    
    @Value("${app.messaging.routing-keys.appointment-status-changed:appointment.status.changed}")
    private String appointmentStatusChangedRoutingKey;

    @Override
    public void publishAppointmentScheduled(AppointmentScheduledEvent event) {
        log.debug("Publicando evento AppointmentScheduled para agendamento: {}", event.getAggregateId());
        
        try {
            AppointmentScheduledEventPayload payload = new AppointmentScheduledEventPayload(
                event.getAggregateId(),
                event.patientId().value(),
                event.professionalId().value(),
                event.scheduledTime().value(),
                event.occurredOn()
            );
            
            String eventPayload = objectMapper.writeValueAsString(payload);
            
            rabbitTemplate.convertAndSend(businessEventsExchange, appointmentScheduledRoutingKey, eventPayload, message -> {
                message.getMessageProperties().setHeader("eventType", "AppointmentScheduled");
                message.getMessageProperties().setHeader("aggregateId", event.getAggregateId());
                message.getMessageProperties().setHeader("occurredOn", event.occurredOn().toString());
                message.getMessageProperties().setContentType("application/json");
                return message;
            });
            
            log.info("Evento AppointmentScheduled publicado com sucesso para agendamento: {}", event.getAggregateId());
            
        } catch (Exception e) {
            log.error("Erro ao publicar evento AppointmentScheduled: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao publicar evento AppointmentScheduled", e);
        }
    }

    @Override
    public void publishAppointmentCancelled(AppointmentCancelledEvent event) {
        log.debug("Publicando evento AppointmentCancelled para agendamento: {}", event.getAggregateId());
        
        try {
            AppointmentCancelledEventPayload payload = new AppointmentCancelledEventPayload(
                event.getAggregateId(),
                event.patientId().value(),
                event.cancellationReason(),
                event.occurredOn()
            );
            
            String eventPayload = objectMapper.writeValueAsString(payload);
            
            rabbitTemplate.convertAndSend(businessEventsExchange, appointmentCancelledRoutingKey, eventPayload, message -> {
                message.getMessageProperties().setHeader("eventType", "AppointmentCancelled");
                message.getMessageProperties().setHeader("aggregateId", event.getAggregateId());
                message.getMessageProperties().setHeader("occurredOn", event.occurredOn().toString());
                message.getMessageProperties().setContentType("application/json");
                return message;
            });
            
            log.info("Evento AppointmentCancelled publicado com sucesso para agendamento: {}", event.getAggregateId());
            
        } catch (Exception e) {
            log.error("Erro ao publicar evento AppointmentCancelled: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao publicar evento AppointmentCancelled", e);
        }
    }

    @Override
    public void publishAppointmentStatusChanged(AppointmentStatusChangedEvent event) {
        log.debug("Publicando evento AppointmentStatusChanged para agendamento: {}", event.getAggregateId());
        
        try {
            AppointmentStatusChangedEventPayload payload = new AppointmentStatusChangedEventPayload(
                event.getAggregateId(),
                event.previousStatus().name(),
                event.newStatus().name(),
                event.occurredOn()
            );
            
            String eventPayload = objectMapper.writeValueAsString(payload);
            
            rabbitTemplate.convertAndSend(businessEventsExchange, appointmentStatusChangedRoutingKey, eventPayload, message -> {
                message.getMessageProperties().setHeader("eventType", "AppointmentStatusChanged");
                message.getMessageProperties().setHeader("aggregateId", event.getAggregateId());
                message.getMessageProperties().setHeader("occurredOn", event.occurredOn().toString());
                message.getMessageProperties().setContentType("application/json");
                return message;
            });
            
            log.info("Evento AppointmentStatusChanged publicado com sucesso para agendamento: {}", event.getAggregateId());
            
        } catch (Exception e) {
            log.error("Erro ao publicar evento AppointmentStatusChanged: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao publicar evento AppointmentStatusChanged", e);
        }
    }

    // DTOs para serialização dos eventos
    
    private record AppointmentScheduledEventPayload(
        String aggregateId,
        String patientId,
        String professionalId,
        java.time.LocalDateTime scheduledTime,
        java.time.Instant occurredOn
    ) {}
    
    private record AppointmentCancelledEventPayload(
        String aggregateId,
        String patientId,
        String reason,
        java.time.Instant occurredOn
    ) {}
    
    private record AppointmentStatusChangedEventPayload(
        String aggregateId,
        String oldStatus,
        String newStatus,
        java.time.Instant occurredOn
    ) {}
}
