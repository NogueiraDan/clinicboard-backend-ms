package com.clinicboard.user_service.domain.event;

import java.time.LocalDateTime;

/**
 * Evento de domínio disparado quando a senha de um usuário é alterada
 */
public class UserPasswordChangedEvent {
    
    private final String userId;
    private final String email;
    private final LocalDateTime occurredOn;
    
    public UserPasswordChangedEvent(String userId, String email) {
        this.userId = userId;
        this.email = email;
        this.occurredOn = LocalDateTime.now();
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
