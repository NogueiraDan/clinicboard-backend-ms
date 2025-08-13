package com.clinicboard.user_service.domain.model.enums;

/**
 * Enum que representa os papéis/perfis de usuário no sistema
 */
public enum UserRole {
    ADMIN("Administrador"),
    PROFESSIONAL("Profissional");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isProfessional() {
        return this == PROFESSIONAL;
    }
}
