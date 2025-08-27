package com.clinicboard.business_service.infrastructure.adapter.out.integration.dto;

/**
 * DTO para representar o role do usuário na comunicação com o user-service.
 * Espelha a enum UserRole do user-service.
 */
public enum UserRoleDto {
    ADMIN,
    DOCTOR,
    NURSE,
    RECEPTIONIST,
    PATIENT;
    
    /**
     * Verifica se o role representa um profissional de saúde.
     */
    public boolean isProfessional() {
        return this == DOCTOR || this == NURSE || this == ADMIN || this == RECEPTIONIST;
    }
}
