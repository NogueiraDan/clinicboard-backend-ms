package com.clinicboard.user_service.domain.model;

import java.util.Arrays;
import java.util.Set;

/**
 * Value Object que representa os diferentes papéis (roles) de usuários no domínio.
 * Encapsula as regras de negócio relacionadas a permissões e comportamentos por role.
 */
public record UserRole(RoleType type, String description) {
    
    public enum RoleType {
        ADMIN("admin", "Administrador do sistema"),
        PROFESSIONAL("professional", "Profissional da saúde");
        
        private final String code;
        private final String defaultDescription;
        
        RoleType(String code, String defaultDescription) {
            this.code = code;
            this.defaultDescription = defaultDescription;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDefaultDescription() {
            return defaultDescription;
        }
    }
    
    public UserRole {
        if (type == null) {
            throw new IllegalArgumentException("RoleType não pode ser nulo");
        }
        if (description == null || description.trim().isEmpty()) {
            // Usa descrição padrão se não fornecida
            description = type.getDefaultDescription();
        }
    }
    
    /**
     * Cria um UserRole com a descrição padrão.
     */
    public static UserRole of(RoleType type) {
        return new UserRole(type, type.getDefaultDescription());
    }
    
    /**
     * Cria um UserRole a partir do código string.
     */
    public static UserRole fromCode(String code) {
        return Arrays.stream(RoleType.values())
                .filter(role -> role.getCode().equals(code))
                .map(UserRole::of)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Role inválido: " + code));
    }
    
    /**
     * Verifica se o usuário possui privilégios administrativos
     */
    public boolean isAdmin() {
        return type == RoleType.ADMIN;
    }
    
    /**
     * Verifica se o usuário é um profissional
     */
    public boolean isProfessional() {
        return type == RoleType.PROFESSIONAL;
    }
    
    /**
     * Verifica se pode gerenciar outros usuários
     */
    public boolean canManageUsers() {
        return isAdmin();
    }
    
    /**
     * Verifica se pode acessar dados sensíveis
     */
    public boolean canAccessSensitiveData() {
        return isAdmin() || isProfessional();
    }
    
    /**
     * Retorna as permissões associadas ao role
     */
    public Set<String> getPermissions() {
        return switch (type) {
            case ADMIN -> Set.of("MANAGE_USERS", "MANAGE_SYSTEM", "ACCESS_ALL_DATA");
            case PROFESSIONAL -> Set.of("MANAGE_PATIENTS", "ACCESS_PATIENT_DATA");
        };
    }
    
    /**
     * Retorna o código do role para compatibilidade
     */
    public String getRole() {
        return type.getCode();
    }
}
