package com.clinicboard.user_service.infrastructure.adapter.in.web.dto;

import com.clinicboard.user_service.domain.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de resposta para operações relacionadas a usuários.
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
