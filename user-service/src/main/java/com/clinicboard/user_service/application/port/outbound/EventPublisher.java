package com.clinicboard.user_service.application.port.outbound;

/**
 * Porta de saída para publicação de eventos (Event Publisher)
 * Define o contrato para envio de eventos de domínio
 */
public interface EventPublisher {
    
    void publishUserRegistered(String userId, String name, String email, String role);
    
    void publishUserActivated(String userId, String email, String name);
    
    void publishUserDeactivated(String userId, String email, String name);
    
    void publishUserPasswordChanged(String userId, String email);
}
