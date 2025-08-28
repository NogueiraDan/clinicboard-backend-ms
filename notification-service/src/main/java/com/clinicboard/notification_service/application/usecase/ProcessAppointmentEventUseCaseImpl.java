package com.clinicboard.notification_service.application.usecase;

import com.clinicboard.notification_service.application.port.in.ProcessAppointmentEventUseCase;
import com.clinicboard.notification_service.application.port.out.NotificationGateway;
import com.clinicboard.notification_service.domain.event.AppointmentScheduledEvent;
import com.clinicboard.notification_service.domain.event.AppointmentCanceledEvent;
import com.clinicboard.notification_service.domain.event.AppointmentStatusChangedEvent;
import com.clinicboard.notification_service.domain.event.AppointmentRescheduledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Implementação do caso de uso para processamento de eventos de agendamento.
 * 
 * Esta classe orquestra a lógica de negócio para processar eventos relacionados
 * a agendamentos e coordenar o envio de notificações apropriadas.
 * 
 * Segue os princípios de DDD onde o caso de uso coordena as operações
 * mas delega responsabilidades específicas para portas de saída.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessAppointmentEventUseCaseImpl implements ProcessAppointmentEventUseCase {
    
    private final NotificationGateway notificationGateway;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
    
    @Override
    public void processAppointmentScheduled(AppointmentScheduledEvent event) {
        log.info("Processing appointment scheduled event for appointment: {}", event.getAggregateId());
        
        try {
            String appointmentDetails = String.format("Data: %s", 
                event.getAppointmentDateTime().format(FORMATTER));
            
            // Notifica o paciente
            String patientMessage = String.format(
                "Olá %s! Seu agendamento foi confirmado com %s.",
                event.getPatientName(),
                event.getProfessionalName()
            );
            
            notificationGateway.sendAppointmentScheduledNotification(
                event.getPatientId(),
                event.getPatientName(),
                patientMessage,
                appointmentDetails
            );
            
            // Notifica o profissional
            String professionalMessage = String.format(
                "Olá %s! Você tem um novo agendamento com %s.",
                event.getProfessionalName(),
                event.getPatientName()
            );
            
            notificationGateway.sendAppointmentScheduledNotification(
                event.getProfessionalId(),
                event.getProfessionalName(),
                professionalMessage,
                appointmentDetails
            );
            
            log.info("Successfully processed appointment scheduled event for appointment: {}", event.getAggregateId());
            
        } catch (Exception e) {
            log.error("Failed to process appointment scheduled event for appointment: {}", event.getAggregateId(), e);
            throw new RuntimeException("Failed to process appointment scheduled event", e);
        }
    }
    
    @Override
    public void processAppointmentCanceled(AppointmentCanceledEvent event) {
        log.info("Processing appointment canceled event for appointment: {}", event.getAppointmentId());
        
        try {
            // Notifica o paciente
            String patientMessage = String.format(
                "Olá %s! Seu agendamento com %s foi cancelado.",
                event.getPatientName(),
                event.getProfessionalName()
            );
            
            notificationGateway.sendAppointmentCanceledNotification(
                event.getPatientId(),
                event.getPatientName(),
                patientMessage,
                event.getReason()
            );
            
            // Notifica o profissional
            String professionalMessage = String.format(
                "Olá %s! O agendamento com %s foi cancelado.",
                event.getProfessionalName(),
                event.getPatientName()
            );
            
            notificationGateway.sendAppointmentCanceledNotification(
                event.getProfessionalId(),
                event.getProfessionalName(),
                professionalMessage,
                event.getReason()
            );
            
            log.info("Successfully processed appointment canceled event for appointment: {}", event.getAppointmentId());
            
        } catch (Exception e) {
            log.error("Failed to process appointment canceled event for appointment: {}", event.getAppointmentId(), e);
            throw new RuntimeException("Failed to process appointment canceled event", e);
        }
    }
    
    @Override
    public void processAppointmentStatusChanged(AppointmentStatusChangedEvent event) {
        log.info("Processing appointment status changed event for appointment: {} - {} -> {}", 
            event.getAggregateId(), event.getPreviousStatusName(), event.getNewStatusName());
        
        try {
            String statusDetails = String.format("Status alterado de '%s' para '%s'", 
                event.getPreviousStatusName(), event.getNewStatusName());
            
            // Notifica o paciente
            String patientMessage = String.format(
                "Olá %s! O status do seu agendamento com %s foi atualizado.",
                event.getPatientName(),
                event.getProfessionalName()
            );
            
            notificationGateway.sendAppointmentStatusChangedNotification(
                event.getPatientId(),
                event.getPatientName(),
                patientMessage,
                statusDetails
            );
            
            // Notifica o profissional
            String professionalMessage = String.format(
                "Olá %s! O status do agendamento com %s foi atualizado.",
                event.getProfessionalName(),
                event.getPatientName()
            );
            
            notificationGateway.sendAppointmentStatusChangedNotification(
                event.getProfessionalId(),
                event.getProfessionalName(),
                professionalMessage,
                statusDetails
            );
            
            log.info("Successfully processed appointment status changed event for appointment: {}", event.getAggregateId());
            
        } catch (Exception e) {
            log.error("Failed to process appointment status changed event for appointment: {}", event.getAggregateId(), e);
            throw new RuntimeException("Failed to process appointment status changed event", e);
        }
    }
    
    @Override
    public void processAppointmentRescheduled(AppointmentRescheduledEvent event) {
        log.info("Processing appointment rescheduled event for appointment: {} from {} to {}", 
            event.getAppointmentId(), event.getFormattedPreviousTime(), event.getFormattedNewTime());
        
        try {
            String rescheduleDetails = String.format("Nova data: %s (anterior: %s)", 
                event.getFormattedNewTime(), event.getFormattedPreviousTime());
            
            // Notifica o paciente
            String patientMessage = String.format(
                "Olá %s! Seu agendamento com %s foi reagendado.",
                event.getPatientName(),
                event.getProfessionalName()
            );
            
            notificationGateway.sendAppointmentRescheduledNotification(
                event.getPatientId(),
                event.getPatientName(),
                patientMessage,
                rescheduleDetails
            );
            
            // Notifica o profissional
            String professionalMessage = String.format(
                "Olá %s! O agendamento com %s foi reagendado.",
                event.getProfessionalName(),
                event.getPatientName()
            );
            
            notificationGateway.sendAppointmentRescheduledNotification(
                event.getProfessionalId(),
                event.getProfessionalName(),
                professionalMessage,
                rescheduleDetails
            );
            
            log.info("Successfully processed appointment rescheduled event for appointment: {}", event.getAppointmentId());
            
        } catch (Exception e) {
            log.error("Failed to process appointment rescheduled event for appointment: {}", event.getAppointmentId(), e);
            throw new RuntimeException("Failed to process appointment rescheduled event", e);
        }
    }
}
