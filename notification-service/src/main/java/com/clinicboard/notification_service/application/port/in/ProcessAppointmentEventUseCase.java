package com.clinicboard.notification_service.application.port.in;

import com.clinicboard.notification_service.domain.event.AppointmentScheduledEvent;
import com.clinicboard.notification_service.domain.event.AppointmentCanceledEvent;
import com.clinicboard.notification_service.domain.event.AppointmentStatusChangedEvent;
import com.clinicboard.notification_service.domain.event.AppointmentRescheduledEvent;

/**
 * Porta de entrada para processamento de eventos de agendamento.
 * 
 * Define os contratos que os adaptadores de entrada devem implementar
 * para processar eventos relacionados a agendamentos.
 * 
 * Segue o padrão Hexagonal Architecture onde as portas definem
 * os contratos de entrada da aplicação.
 */
public interface ProcessAppointmentEventUseCase {
    
    /**
     * Processa evento de agendamento criado
     */
    void processAppointmentScheduled(AppointmentScheduledEvent event);
    
    /**
     * Processa evento de agendamento cancelado
     */
    void processAppointmentCanceled(AppointmentCanceledEvent event);
    
    /**
     * Processa evento de mudança de status do agendamento
     */
    void processAppointmentStatusChanged(AppointmentStatusChangedEvent event);
    
    /**
     * Processa evento de reagendamento
     */
    void processAppointmentRescheduled(AppointmentRescheduledEvent event);
}
