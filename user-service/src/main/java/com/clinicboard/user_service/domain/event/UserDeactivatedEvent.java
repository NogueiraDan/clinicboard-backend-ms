package com.clinicboard.user_service.domain.event;

import java.time.LocalDateTime;

/**
 * Evento de domínio disparado quando um usuário é desativado
 */
public class UserDeactivatedEvent {
    
    private final String userId;
    private final String email;
    private final String name;
    private final LocalDateTime occurredOn;
    
    public UserDeactivatedEvent(String userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.occurredOn = LocalDateTime.now();
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getName() {
        return name;
    }
    
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
