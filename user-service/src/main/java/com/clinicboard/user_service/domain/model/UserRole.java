package com.clinicboard.user_service.domain.model;

/**
 * Enum que representa os diferentes papéis (roles) de usuários no domínio.
 * Movido para o domínio pois representa conceitos fundamentais do negócio.
 */
public enum UserRole {
    ADMIN("admin"),
    PROFESSIONAL("professional");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
    
    /**
     * Verifica se o usuário possui privilégios administrativos
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    /**
     * Verifica se o usuário é um profissional
     */
    public boolean isProfessional() {
        return this == PROFESSIONAL;
    }
}
