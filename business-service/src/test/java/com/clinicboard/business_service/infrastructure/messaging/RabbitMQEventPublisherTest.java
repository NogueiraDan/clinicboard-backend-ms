package com.clinicboard.business_service.infrastructure.messaging;

import com.clinicboard.business_service.domain.event.AppointmentScheduledEvent;
import com.clinicboard.business_service.domain.event.AppointmentCancelledEvent;
import com.clinicboard.business_service.domain.event.PatientRegisteredEvent;
import com.clinicboard.business_service.application.port.outbound.EventPublishingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes para validar publicação de Domain Events via RabbitMQ
 * com Circuit Breaker + DLQ
 */
@ExtendWith(MockitoExtension.class)
class RabbitMQEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RabbitMQEventPublisher eventPublisher;
    
    private final String exchangeName = "notification.ex";

    @BeforeEach
    void setUp() {
        eventPublisher = new RabbitMQEventPublisher(rabbitTemplate);
        ReflectionTestUtils.setField(eventPublisher, "exchangeName", exchangeName);
    }

    @Test
    void shouldPublishAppointmentScheduledEventSuccessfully() {
        // Given
        AppointmentScheduledEvent event = new AppointmentScheduledEvent(
            "appointment-123",
            "patient-456", 
            "prof-789",
            LocalDateTime.now().plusDays(1),
            "MARCACAO"
        );
        
        // When
        eventPublisher.publishEvent(event);
        
        // Then
        verify(rabbitTemplate).convertAndSend(
            eq(exchangeName),
            eq("clinic.appointment.scheduled"),
            eq(event)
        );
    }

    @Test
    void shouldPublishAppointmentCancelledEventSuccessfully() {
        // Given
        AppointmentCancelledEvent event = new AppointmentCancelledEvent(
            "appointment-123",
            "patient-456",
            "prof-789", 
            LocalDateTime.now().plusDays(1),
            "Cancelado pelo paciente"
        );
        
        // When
        eventPublisher.publishEvent(event);
        
        // Then
        verify(rabbitTemplate).convertAndSend(
            eq(exchangeName),
            eq("clinic.appointment.cancelled"),
            eq(event)
        );
    }

    @Test
    void shouldPublishPatientRegisteredEventSuccessfully() {
        // Given
        PatientRegisteredEvent event = new PatientRegisteredEvent(
            "patient-123", 
            "João Silva", 
            "joao@email.com",
            "prof-456"
        );
        
        // When
        eventPublisher.publishEvent(event);
        
        // Then
        verify(rabbitTemplate).convertAndSend(
            eq(exchangeName),
            eq("clinic.patient.registered"),
            eq(event)
        );
    }

    @Test
    void shouldThrowExceptionWhenRabbitTemplateFailsAndCircuitBreakerIsNotActive() {
        // Given
        AppointmentScheduledEvent event = new AppointmentScheduledEvent(
            "appointment-123",
            "patient-456", 
            "prof-789",
            LocalDateTime.now().plusDays(1),
            "MARCACAO"
        );
        
        doThrow(new RuntimeException("RabbitMQ connection failed"))
            .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));
        
        // When & Then
        assertThrows(EventPublishingException.class, () -> 
            eventPublisher.publishEvent(event)
        );
    }

    @Test
    void shouldSendEventToDLQWhenCircuitBreakerActivates() {
        // Given
        AppointmentScheduledEvent event = new AppointmentScheduledEvent(
            "appointment-123",
            "patient-456", 
            "prof-789",
            LocalDateTime.now().plusDays(1),
            "MARCACAO"
        );
        
        // When - simula ativação do circuit breaker via método fallback
        eventPublisher.publishEventFallback(event, new RuntimeException("Service unavailable"));
        
        // Then
        verify(rabbitTemplate).convertAndSend(
            eq(exchangeName),
            eq("clinic.appointment.scheduled.failed"),
            eq(event)
        );
    }
}
