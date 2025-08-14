package com.clinicboard.user_service.application.port.inbound;

import com.clinicboard.user_service.application.dto.UserRequestDto;
import com.clinicboard.user_service.application.dto.UserResponseDto;

import java.util.List;

/**
 * Porta de entrada para operações de usuário (Use Cases)
 * Define os casos de uso disponíveis
 */
public interface UserUseCase {
    
    UserResponseDto registerUser(UserRequestDto request);
    
    UserResponseDto findUserById(String userId);
    
    UserResponseDto findUserByEmail(String email);
    
    List<UserResponseDto> findAllUsers();
    
    List<UserResponseDto> findUsersByRole(String role);
    
    UserResponseDto updateUser(String userId, UserRequestDto request);
    
    void changePassword(String userId, String currentPassword, String newPassword);
    
    void activateUser(String userId);
    
    void deactivateUser(String userId);
    
    void deleteUser(String userId);
}
