package com.clinicboard.user_service.application.usecase;

import com.clinicboard.user_service.application.dto.LoginRequestDto;
import com.clinicboard.user_service.application.dto.LoginResponseDto;
import com.clinicboard.user_service.application.dto.UserResponseDto;
import com.clinicboard.user_service.application.mapper.DomainUserMapper;
import com.clinicboard.user_service.application.port.inbound.AuthenticationUseCase;
import com.clinicboard.user_service.application.port.outbound.TokenServicePort;
import com.clinicboard.user_service.application.port.outbound.UserRepositoryPort;
import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.valueobjects.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação dos casos de uso de autenticação
 */
@Service
@Transactional(readOnly = true)
public class AuthenticationUseCaseImpl implements AuthenticationUseCase {
    
    private final UserRepositoryPort userRepository;
    private final TokenServicePort tokenService;
    private final DomainUserMapper userMapper;
    
    public AuthenticationUseCaseImpl(UserRepositoryPort userRepository, 
                                   TokenServicePort tokenService,
                                   DomainUserMapper userMapper) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.userMapper = userMapper;
    }
    
    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        // Busca usuário por email
        Email email = new Email(request.getEmail());
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));
        
        // Valida senha usando comportamento do domínio
        if (!user.authenticate(request.getPassword())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }
        
        // Gera token
        String token = tokenService.generateToken(user);
        
        // Converte user para DTO
        UserResponseDto userDto = userMapper.toResponseDto(user);
        
        // Retorna resposta completa
        return new LoginResponseDto(
            token,
            "Bearer",
            tokenService.getExpirationTime(),
            userDto
        );
    }
    
    @Override
    public boolean validateToken(String token) {
        String userId = tokenService.validateToken(token);
        return !userId.isEmpty();
    }
    
    @Override
    public String extractUserIdFromToken(String token) {
        String userId = tokenService.validateToken(token);
        return userId.isEmpty() ? null : userId;
    }
}
