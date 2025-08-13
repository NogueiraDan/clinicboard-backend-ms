package com.clinicboard.user_service.domain.event;

import java.time.LocalDateTime;

/**
 * Evento de domínio disparado quando um usuário é registrado
 */
public class UserRegisteredEvent {
    
    private final String userId;
    private final String name;
    private final String email;
    private final String role;
    private final LocalDateTime occurredOn;
    
    public UserRegisteredEvent(String userId, String name, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.occurredOn = LocalDateTime.now();
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getRole() {
        return role;
    }
    
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
