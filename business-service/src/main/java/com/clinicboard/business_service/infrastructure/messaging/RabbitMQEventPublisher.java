package com.clinicboard.business_service.infrastructure.messaging;

import com.clinicboard.business_service.application.dto.AppointmentRequestDto;
import com.clinicboard.business_service.application.port.outbound.EventPublisher;
import com.clinicboard.business_service.infrastructure.messaging.dto.AppointmentCancelledMessageDto;
import com.clinicboard.business_service.infrastructure.messaging.dto.AppointmentScheduledMessageDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador para publicação de eventos via RabbitMQ
 */
@Component
public class RabbitMQEventPublisher implements EventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    // Configurações das filas
    private static final String APPOINTMENT_SCHEDULED_QUEUE = "appointment.scheduled";
    private static final String APPOINTMENT_CANCELLED_QUEUE = "appointment.cancelled";
    private static final String PATIENT_REGISTERED_QUEUE = "patient.registered";
    
    public RabbitMQEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    @Override
    public void publishAppointmentScheduled(AppointmentRequestDto appointment) {
        // Publica evento estruturado
        AppointmentScheduledMessageDto messageDto = new AppointmentScheduledMessageDto(
            null, // appointmentId será preenchido pelo agregado
            appointment.getPatientId(),
            appointment.getProfessionalId(),
            appointment.getDate(),
            "SCHEDULED"
        );
        
        messageDto.setObservation(appointment.getObservation());
        
        rabbitTemplate.convertAndSend(APPOINTMENT_SCHEDULED_QUEUE, messageDto);
    }
    
    @Override
    public void publishAppointmentCancelled(String appointmentId, String reason) {
        AppointmentCancelledMessageDto messageDto = new AppointmentCancelledMessageDto(
            appointmentId,
            null, // Seria interessante recuperar os dados do agregado
            null,
            null,
            reason
        );
        
        rabbitTemplate.convertAndSend(APPOINTMENT_CANCELLED_QUEUE, messageDto);
    }
    
    @Override
    public void publishPatientRegistered(String patientId, String patientName, String professionalId) {
        // Para compatibilidade futura - implementação básica
        var message = String.format("Patient registered: ID=%s, Name=%s, Professional=%s", 
                                   patientId, patientName, professionalId);
        
        rabbitTemplate.convertAndSend(PATIENT_REGISTERED_QUEUE, message);
    }
}
