package com.clinicboard.notification_service.application.port.out;

/**
 * Porta de saída para envio de notificações.
 * 
 * Define o contrato que os adaptadores de saída devem implementar
 * para enviar notificações através de diferentes canais (email, SMS, push, etc).
 * 
 * Segue o padrão Hexagonal Architecture onde as portas definem
 * os contratos de saída da aplicação.
 */
public interface NotificationGateway {
    
    /**
     * Envia notificação de agendamento criado
     */
    void sendAppointmentScheduledNotification(String userId, String userName, String message, String appointmentDetails);
    
    /**
     * Envia notificação de agendamento cancelado
     */
    void sendAppointmentCanceledNotification(String userId, String userName, String message, String reason);
    
    /**
     * Envia notificação de mudança de status
     */
    void sendAppointmentStatusChangedNotification(String userId, String userName, String message, String statusDetails);
    
    /**
     * Envia notificação de reagendamento
     */
    void sendAppointmentRescheduledNotification(String userId, String userName, String message, String rescheduleDetails);
}
