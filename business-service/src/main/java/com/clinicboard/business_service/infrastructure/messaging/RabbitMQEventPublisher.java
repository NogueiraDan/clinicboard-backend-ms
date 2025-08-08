package com.clinicboard.business_service.infrastructure.messaging;

import com.clinicboard.business_service.application.dto.AppointmentRequestDto;
import com.clinicboard.business_service.application.port.outbound.EventPublisher;
import com.clinicboard.business_service.infrastructure.messaging.dto.AppointmentCancelledMessageDto;
import com.clinicboard.business_service.infrastructure.messaging.dto.AppointmentScheduledMessageDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adaptador para publicação de eventos via RabbitMQ
 * Com Circuit Breaker e DLQ para garantir entrega dos eventos
 */
@Component
public class RabbitMQEventPublisher implements EventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQEventPublisher.class);
    
    private final RabbitTemplate rabbitTemplate;
    
    // Configurações das filas principais
    private static final String APPOINTMENT_SCHEDULED_QUEUE = "appointment.scheduled";
    private static final String APPOINTMENT_CANCELLED_QUEUE = "appointment.cancelled";
    private static final String PATIENT_REGISTERED_QUEUE = "patient.registered";
    
    // Configurações das DLQs (Dead Letter Queues)
    private static final String APPOINTMENT_SCHEDULED_DLQ = "appointment.scheduled.dlq";
    private static final String APPOINTMENT_CANCELLED_DLQ = "appointment.cancelled.dlq";
    private static final String PATIENT_REGISTERED_DLQ = "patient.registered.dlq";
    
    public RabbitMQEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    @Override
    @CircuitBreaker(name = "notification-service", fallbackMethod = "publishAppointmentScheduledFallback")
    public void publishAppointmentScheduled(AppointmentRequestDto appointment) {
        try {
            // Publica evento estruturado
            AppointmentScheduledMessageDto messageDto = new AppointmentScheduledMessageDto(
                null, // appointmentId será preenchido pelo agregado
                appointment.getPatientId(),
                appointment.getProfessionalId(),
                appointment.getDate(),
                "SCHEDULED"
            );
            
            messageDto.setObservation(appointment.getObservation());
            
            logger.info("Publicando evento de agendamento para paciente: {} e profissional: {}", 
                       appointment.getPatientId(), appointment.getProfessionalId());
            
            rabbitTemplate.convertAndSend(APPOINTMENT_SCHEDULED_QUEUE, messageDto);
            
            logger.info("Evento de agendamento publicado com sucesso");
            
        } catch (Exception e) {
            logger.error("Erro ao publicar evento de agendamento: {}", e.getMessage(), e);
            throw e; // Relança para ativar o Circuit Breaker
        }
    }
    
    /**
     * Fallback method - publica na DLQ quando Notification Service está indisponível
     */
    public void publishAppointmentScheduledFallback(AppointmentRequestDto appointment, Throwable throwable) {
        logger.warn("Circuit Breaker ativo - Notification Service indisponível. Enviando para DLQ. Erro: {}", 
                   throwable.getMessage());
        
        try {
            AppointmentScheduledMessageDto messageDto = new AppointmentScheduledMessageDto(
                null,
                appointment.getPatientId(),
                appointment.getProfessionalId(),
                appointment.getDate(),
                "SCHEDULED"
            );
            
            messageDto.setObservation(appointment.getObservation());
            
            // Publica na Dead Letter Queue para retry posterior
            rabbitTemplate.convertAndSend(APPOINTMENT_SCHEDULED_DLQ, messageDto);
            
            logger.info("Evento de agendamento enviado para DLQ com sucesso - será reprocessado automaticamente");
            
        } catch (Exception e) {
            logger.error("CRÍTICO: Falha ao enviar evento para DLQ. Evento pode estar perdido: {}", e.getMessage(), e);
            // Aqui poderíamos implementar persistência em banco como último recurso
        }
    }
    
    @Override
    @CircuitBreaker(name = "notification-service", fallbackMethod = "publishAppointmentCancelledFallback")
    public void publishAppointmentCancelled(String appointmentId, String reason) {
        try {
            AppointmentCancelledMessageDto messageDto = new AppointmentCancelledMessageDto(
                appointmentId,
                null, // Seria interessante recuperar os dados do agregado
                null,
                null,
                reason
            );
            
            logger.info("Publicando evento de cancelamento para agendamento: {}", appointmentId);
            
            rabbitTemplate.convertAndSend(APPOINTMENT_CANCELLED_QUEUE, messageDto);
            
            logger.info("Evento de cancelamento publicado com sucesso");
            
        } catch (Exception e) {
            logger.error("Erro ao publicar evento de cancelamento: {}", e.getMessage(), e);
            throw e; // Relança para ativar o Circuit Breaker
        }
    }
    
    /**
     * Fallback method para cancelamento - publica na DLQ quando Notification Service está indisponível
     */
    public void publishAppointmentCancelledFallback(String appointmentId, String reason, Throwable throwable) {
        logger.warn("Circuit Breaker ativo - Notification Service indisponível. Enviando cancelamento para DLQ. Erro: {}", 
                   throwable.getMessage());
        
        try {
            AppointmentCancelledMessageDto messageDto = new AppointmentCancelledMessageDto(
                appointmentId,
                null,
                null,
                null,
                reason
            );
            
            // Publica na Dead Letter Queue para retry posterior
            rabbitTemplate.convertAndSend(APPOINTMENT_CANCELLED_DLQ, messageDto);
            
            logger.info("Evento de cancelamento enviado para DLQ com sucesso - será reprocessado automaticamente");
            
        } catch (Exception e) {
            logger.error("CRÍTICO: Falha ao enviar evento de cancelamento para DLQ. Evento pode estar perdido: {}", e.getMessage(), e);
        }
    }
    
    @Override
    @CircuitBreaker(name = "notification-service", fallbackMethod = "publishPatientRegisteredFallback")
    public void publishPatientRegistered(String patientId, String patientName, String professionalId) {
        try {
            var message = String.format("Patient registered: ID=%s, Name=%s, Professional=%s", 
                                       patientId, patientName, professionalId);
            
            logger.info("Publicando evento de cadastro de paciente: {}", patientId);
            
            rabbitTemplate.convertAndSend(PATIENT_REGISTERED_QUEUE, message);
            
            logger.info("Evento de cadastro de paciente publicado com sucesso");
            
        } catch (Exception e) {
            logger.error("Erro ao publicar evento de cadastro de paciente: {}", e.getMessage(), e);
            throw e; // Relança para ativar o Circuit Breaker
        }
    }
    
    /**
     * Fallback method para cadastro de paciente - publica na DLQ quando Notification Service está indisponível
     */
    public void publishPatientRegisteredFallback(String patientId, String patientName, String professionalId, Throwable throwable) {
        logger.warn("Circuit Breaker ativo - Notification Service indisponível. Enviando cadastro de paciente para DLQ. Erro: {}", 
                   throwable.getMessage());
        
        try {
            var message = String.format("Patient registered: ID=%s, Name=%s, Professional=%s", 
                                       patientId, patientName, professionalId);
            
            // Publica na Dead Letter Queue para retry posterior
            rabbitTemplate.convertAndSend(PATIENT_REGISTERED_DLQ, message);
            
            logger.info("Evento de cadastro de paciente enviado para DLQ com sucesso - será reprocessado automaticamente");
            
        } catch (Exception e) {
            logger.error("CRÍTICO: Falha ao enviar evento de cadastro de paciente para DLQ. Evento pode estar perdido: {}", e.getMessage(), e);
        }
    }
}
