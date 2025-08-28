package com.clinicboard.notification_service.infrastructure.adapter.out.notification;

import com.clinicboard.notification_service.application.port.out.NotificationGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementação mock do gateway de notificações.
 * 
 * Esta implementação serve como placeholder inicial e pode ser
 * substituída por implementações reais (email, SMS, push notifications, etc).
 * 
 * Segue o padrão Hexagonal Architecture onde os adaptadores de saída
 * implementam as portas definidas pela camada de aplicação.
 */
@Slf4j
@Component
public class MockNotificationGateway implements NotificationGateway {
    
    @Override
    public void sendAppointmentScheduledNotification(String userId, String userName, String message, String appointmentDetails) {
        log.info("📅 [NOTIFICATION] Agendamento Criado");
        log.info("   Usuário: {} (ID: {})", userName, userId);
        log.info("   Mensagem: {}", message);
        log.info("   Detalhes: {}", appointmentDetails);
        log.info("   ===============================================");
        
        // TODO: Implementar envio real de notificação (email, SMS, push, etc)
        // Aqui seria integrado com serviços como SendGrid, Twilio, Firebase, etc.
    }
    
    @Override
    public void sendAppointmentCanceledNotification(String userId, String userName, String message, String reason) {
        log.info("❌ [NOTIFICATION] Agendamento Cancelado");
        log.info("   Usuário: {} (ID: {})", userName, userId);
        log.info("   Mensagem: {}", message);
        log.info("   Motivo: {}", reason);
        log.info("   ===============================================");
        
        // TODO: Implementar envio real de notificação
    }
    
    @Override
    public void sendAppointmentStatusChangedNotification(String userId, String userName, String message, String statusDetails) {
        log.info("🔄 [NOTIFICATION] Status Alterado");
        log.info("   Usuário: {} (ID: {})", userName, userId);
        log.info("   Mensagem: {}", message);
        log.info("   Status: {}", statusDetails);
        log.info("   ===============================================");
        
        // TODO: Implementar envio real de notificação
    }
    
    @Override
    public void sendAppointmentRescheduledNotification(String userId, String userName, String message, String rescheduleDetails) {
        log.info("📅 [NOTIFICATION] Agendamento Reagendado");
        log.info("   Usuário: {} (ID: {})", userName, userId);
        log.info("   Mensagem: {}", message);
        log.info("   Reagendamento: {}", rescheduleDetails);
        log.info("   ===============================================");
        
        // TODO: Implementar envio real de notificação
    }
}
