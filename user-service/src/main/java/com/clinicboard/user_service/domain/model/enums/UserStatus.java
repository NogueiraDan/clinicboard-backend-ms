package com.clinicboard.user_service.domain.model.enums;

/**
 * Enum que representa o status do usuário no sistema
 */
public enum UserStatus {
    ACTIVE("Ativo"),
    INACTIVE("Inativo"),
    BLOCKED("Bloqueado");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isInactive() {
        return this == INACTIVE;
    }

    public boolean isBlocked() {
        return this == BLOCKED;
    }
}
