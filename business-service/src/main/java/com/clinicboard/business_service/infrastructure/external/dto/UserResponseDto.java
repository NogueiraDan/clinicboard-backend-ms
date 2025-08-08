package com.clinicboard.business_service.infrastructure.external.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para resposta do user-service
 * Classe de infraestrutura - representa o contrato com serviço externo
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String id;
    private String name;
    private String email;
    private String contact;
    private UserRole role;
}
