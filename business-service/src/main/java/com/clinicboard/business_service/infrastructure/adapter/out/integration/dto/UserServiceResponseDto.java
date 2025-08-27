package com.clinicboard.business_service.infrastructure.adapter.out.integration.dto;

/**
 * DTO para comunicação com o user-service.
 * Representa a resposta do endpoint GET /users/{id}.
 * 
 * Este DTO espelha a estrutura do UserResponseDto do user-service,
 * mas pertence ao contexto do business-service.
 */
public record UserServiceResponseDto(
    String id,
    String name,
    String email,
    String contact,
    UserRoleDto role
) {
    
    /**
     * Verifica se o usuário é um profissional.
     */
    public boolean isProfessional() {
        return role != null && role.isProfessional();
    }
    
    /**
     * Verifica se o usuário está ativo (critério: tem role válido).
     */
    public boolean isActive() {
        return role != null;
    }
}
