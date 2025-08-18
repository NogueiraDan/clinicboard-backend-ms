package com.clinicboard.user_service.infrastructure.adapter.in.web.dto;

import com.clinicboard.user_service.domain.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de resposta para login.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    private String id;
    private String name;
    private String email;
    private String contact;
    private UserRole role;
    private String access_token;
}
