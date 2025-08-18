package com.clinicboard.user_service.infrastructure.adapter.in.web;

import com.clinicboard.user_service.application.port.in.AuthenticateUserUseCase;
import com.clinicboard.user_service.application.port.in.CreateUserUseCase;
import com.clinicboard.user_service.application.port.in.UpdateUserUseCase;
import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.UserId;
import com.clinicboard.user_service.infrastructure.adapter.in.web.dto.*;

import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre DTOs da camada web e comandos/objetos de domínio.
 */
@Component
public class UserWebMapper {
    
    /**
     * Converte CreateUserRequestDto para CreateUserCommand
     */
    public CreateUserUseCase.CreateUserCommand toCreateCommand(CreateUserRequestDto dto) {
        return new CreateUserUseCase.CreateUserCommand(
                dto.getName(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getContact(),
                dto.getRole()
        );
    }
    
    /**
     * Converte UpdateUserRequestDto para UpdateUserCommand
     */
    public UpdateUserUseCase.UpdateUserCommand toUpdateCommand(String id, UpdateUserRequestDto dto) {
        return new UpdateUserUseCase.UpdateUserCommand(
                new UserId(id),
                dto.getName(),
                dto.getContact()
        );
    }
    
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
     * Converte User (domínio) para UserResponseDto
     */
    public UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId() != null ? user.getId().getValue() : null,
                user.getName(),
                user.getEmail().getValue(),
                user.getContact().getValue(),
                user.getRole()
        );
    }
    
    /**
     * Converte AuthenticationResult para LoginResponseDto
     */
    public LoginResponseDto toLoginResponseDto(AuthenticateUserUseCase.AuthenticationResult result) {
        User user = result.user();
        return new LoginResponseDto(
                user.getId().getValue(),
                user.getName(),
                user.getEmail().getValue(),
                user.getContact().getValue(),
                user.getRole(),
                result.token()
        );
    }
}
