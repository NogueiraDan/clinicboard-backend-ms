package com.clinicboard.user_service.infrastructure.adapter.in.web;

import com.clinicboard.user_service.application.port.in.AuthenticateUserUseCase;
import com.clinicboard.user_service.infrastructure.adapter.in.web.dto.LoginRequestDto;
import com.clinicboard.user_service.infrastructure.adapter.in.web.dto.LoginResponseDto;

import org.springframework.stereotype.Component;

/**
 * Mapper específico para conversão entre DTOs de autenticação e comandos de domínio.
 * Responsável exclusivamente por operações relacionadas à autenticação.
 */
@Component
public class AuthWebMapper {
    
    /**
     * Converte LoginRequestDto para AuthenticationCommand
     */
    public AuthenticateUserUseCase.AuthenticationCommand toAuthCommand(LoginRequestDto dto) {
        return new AuthenticateUserUseCase.AuthenticationCommand(
                dto.getEmail(),
                dto.getPassword()
        );
    }
    
    /**
     * Converte AuthenticationResult para LoginResponseDto
     */
    public LoginResponseDto toLoginResponseDto(AuthenticateUserUseCase.AuthenticationResult result) {
        return new LoginResponseDto(
                result.user().getId().getValue(),
                result.user().getName(),
                result.user().getEmail().getValue(),
                result.user().getContact().getValue(),
                result.user().getRole(),
                result.token()
        );
    }
}
