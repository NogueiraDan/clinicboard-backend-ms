package com.clinicboard.business_service.infrastructure.adapter.out.messaging;

import com.clinicboard.business_service.application.port.out.EventPublisherGateway;
import com.clinicboard.business_service.domain.event.AppointmentScheduledEvent;
import com.clinicboard.business_service.domain.event.AppointmentCanceledEvent;
import com.clinicboard.business_service.domain.event.AppointmentStatusChangedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do gateway de eventos usando RabbitMQ.
 * 
 * Esta classe é responsável por publicar eventos de domínio
 * relacionados a agendamentos em filas RabbitMQ, permitindo
 * que outros serviços sejam notificados sobre mudanças importantes.
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

    @Override
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
}
