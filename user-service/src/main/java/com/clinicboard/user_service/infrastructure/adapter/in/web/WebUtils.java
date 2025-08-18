package com.clinicboard.user_service.infrastructure.adapter.in.web;

import org.springframework.security.core.userdetails.UserDetails;

import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.infrastructure.adapter.in.web.dto.UserResponseDto;

/**
 * Utilitários para conversão de tipos na camada web.
 * Classe legada mantida para compatibilidade.
 */
public class WebUtils {

    public static UserResponseDto convertToUserResponseDto(UserDetails userDetails) {
        if (userDetails instanceof User) {
            User user = (User) userDetails;
            return new UserResponseDto(
                    user.getId() != null ? user.getId().getValue() : null,
                    user.getName(),
                    user.getEmail().getValue(),
                    user.getContact().getValue(),
                    user.getRole());
        }
        return null;
    }
}
