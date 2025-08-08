package com.clinicboard.business_service.infrastructure.messaging;

import com.clinicboard.business_service.application.dto.AppointmentRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes para validar Circuit Breaker + DLQ na publicação de eventos
 */
@ExtendWith(MockitoExtension.class)
class RabbitMQEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RabbitMQEventPublisher eventPublisher;
    
    private AppointmentRequestDto appointmentRequestDto;

    @BeforeEach
    void setUp() {
        eventPublisher = new RabbitMQEventPublisher(rabbitTemplate);
        
        appointmentRequestDto = new AppointmentRequestDto();
        appointmentRequestDto.setPatientId("patient-123");
        appointmentRequestDto.setProfessionalId("prof-456");
        appointmentRequestDto.setDate(LocalDateTime.now().plusDays(1));
        appointmentRequestDto.setObservation("Consulta de rotina");
    }

    @Test
    void shouldPublishAppointmentScheduledSuccessfully() {
        // When
        eventPublisher.publishAppointmentScheduled(appointmentRequestDto);
        
        // Then
        verify(rabbitTemplate).convertAndSend(eq("appointment.scheduled"), any(Object.class));
    }

    @Test
    void shouldPublishAppointmentCancelledSuccessfully() {
        // When
        eventPublisher.publishAppointmentCancelled("appointment-123", "Cancelado pelo paciente");
        
        // Then
        verify(rabbitTemplate).convertAndSend(eq("appointment.cancelled"), any(Object.class));
    }

    @Test
    void shouldPublishPatientRegisteredSuccessfully() {
        // When
        eventPublisher.publishPatientRegistered("patient-123", "João Silva", "prof-456");
        
        // Then
        verify(rabbitTemplate).convertAndSend(eq("patient.registered"), any(Object.class));
    }

    @Test
    void shouldSendToDLQWhenRabbitMQFails() {
        // Given - Simula falha no RabbitMQ
        doThrow(new RuntimeException("RabbitMQ connection failed"))
            .when(rabbitTemplate).convertAndSend(eq("appointment.scheduled"), any(Object.class));
        
        // When - Tenta publicar o evento (irá para o fallback/DLQ)
        try {
            eventPublisher.publishAppointmentScheduled(appointmentRequestDto);
        } catch (Exception e) {
            // Chama o fallback manualmente (normalmente seria automático pelo Circuit Breaker)
            eventPublisher.publishAppointmentScheduledFallback(appointmentRequestDto, e);
        }
        
        // Then - Verifica que tentou publicar na fila principal e depois na DLQ
        verify(rabbitTemplate).convertAndSend(eq("appointment.scheduled"), any(Object.class));
        verify(rabbitTemplate).convertAndSend(eq("appointment.scheduled.dlq"), any(Object.class));
    }

    @Test
    void shouldLogErrorWhenBothQueuesFailDuringFallback() {
        // Given - Simula falha em ambas as filas
        doThrow(new RuntimeException("RabbitMQ totally down"))
            .when(rabbitTemplate).convertAndSend(anyString(), any(Object.class));
        
        // When/Then - Não deve lançar exceção no fallback, apenas logar
        eventPublisher.publishAppointmentScheduledFallback(
            appointmentRequestDto, 
            new RuntimeException("Original failure")
        );
        
        // Verifica que tentou publicar na DLQ
        verify(rabbitTemplate).convertAndSend(eq("appointment.scheduled.dlq"), any(Object.class));
    }
}
