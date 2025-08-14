package com.clinicboard.user_service.application.usecase;

import com.clinicboard.user_service.application.dto.LoginRequestDto;
import com.clinicboard.user_service.application.dto.LoginResponseDto;
import com.clinicboard.user_service.application.dto.UserResponseDto;
import com.clinicboard.user_service.application.mapper.DomainUserMapper;
import com.clinicboard.user_service.application.port.outbound.TokenServicePort;
import com.clinicboard.user_service.application.port.outbound.UserRepositoryPort;
import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.enums.UserRole;
import com.clinicboard.user_service.domain.model.enums.UserStatus;
import com.clinicboard.user_service.domain.model.valueobjects.Contact;
import com.clinicboard.user_service.domain.model.valueobjects.Email;
import com.clinicboard.user_service.domain.model.valueobjects.Password;
import com.clinicboard.user_service.domain.model.valueobjects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;
    
    @Mock
    private TokenServicePort tokenService;
    
    @Mock
    private DomainUserMapper userMapper;
    
    @InjectMocks
    private AuthenticationUseCaseImpl authenticationUseCase;
    
    private User validUser;
    private LoginRequestDto validLoginRequest;
    private UserResponseDto userResponseDto;
    
    @BeforeEach
    void setUp() {
        validUser = new User(
            new UserId("user-123"),
            "João Silva",
            new Email("joao@example.com"),
            new Password("MinhaSenh@123"),
            new Contact("(11) 99999-9999"),
            UserRole.PROFESSIONAL,
            UserStatus.ACTIVE,
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now()
        );
        
        validLoginRequest = new LoginRequestDto("joao@example.com", "MinhaSenh@123");
        
        userResponseDto = new UserResponseDto(
            "user-123",
            "João Silva", 
            "joao@example.com",
            "(11) 99999-9999",
            "PROFESSIONAL",
            "ACTIVE",
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now()
        );
    }
    
    @Test
    void shouldLoginSuccessfully() {
        // Given
        String expectedToken = "jwt-token-123";
        Long expirationTime = 7200L;
        
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(validUser));
        when(tokenService.generateToken(validUser)).thenReturn(expectedToken);
        when(tokenService.getExpirationTime()).thenReturn(expirationTime);
        when(userMapper.toResponseDto(validUser)).thenReturn(userResponseDto);
        
        // When
        LoginResponseDto response = authenticationUseCase.login(validLoginRequest);
        
        // Then
        assertThat(response.getToken()).isEqualTo(expectedToken);
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(expirationTime);
        assertThat(response.getUser()).isEqualTo(userResponseDto);
        
        verify(userRepository).findByEmail(any(Email.class));
        verify(tokenService).generateToken(validUser);
        verify(userMapper).toResponseDto(validUser);
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> authenticationUseCase.login(validLoginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Credenciais inválidas");
        
        verify(userRepository).findByEmail(any(Email.class));
        verify(tokenService, never()).generateToken(any());
        verify(userMapper, never()).toResponseDto(any());
    }
    
    @Test
    void shouldThrowExceptionWhenPasswordInvalid() {
        // Given
        LoginRequestDto invalidPasswordRequest = new LoginRequestDto("joao@example.com", "WrongPassword");
        
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(validUser));
        
        // When & Then
        assertThatThrownBy(() -> authenticationUseCase.login(invalidPasswordRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Credenciais inválidas");
        
        verify(userRepository).findByEmail(any(Email.class));
        verify(tokenService, never()).generateToken(any());
        verify(userMapper, never()).toResponseDto(any());
    }
    
    @Test
    void shouldValidateTokenSuccessfully() {
        // Given
        String validToken = "valid-jwt-token";
        when(tokenService.validateToken(validToken)).thenReturn("user-123");
        
        // When
        boolean isValid = authenticationUseCase.validateToken(validToken);
        
        // Then
        assertThat(isValid).isTrue();
        verify(tokenService).validateToken(validToken);
    }
    
    @Test
    void shouldReturnFalseForInvalidToken() {
        // Given
        String invalidToken = "invalid-jwt-token";
        when(tokenService.validateToken(invalidToken)).thenReturn("");
        
        // When
        boolean isValid = authenticationUseCase.validateToken(invalidToken);
        
        // Then
        assertThat(isValid).isFalse();
        verify(tokenService).validateToken(invalidToken);
    }
    
    @Test
    void shouldExtractUserIdFromValidToken() {
        // Given
        String validToken = "valid-jwt-token";
        String expectedUserId = "user-123";
        when(tokenService.validateToken(validToken)).thenReturn(expectedUserId);
        
        // When
        String userId = authenticationUseCase.extractUserIdFromToken(validToken);
        
        // Then
        assertThat(userId).isEqualTo(expectedUserId);
        verify(tokenService).validateToken(validToken);
    }
    
    @Test
    void shouldReturnNullForInvalidTokenWhenExtractingUserId() {
        // Given
        String invalidToken = "invalid-jwt-token";
        when(tokenService.validateToken(invalidToken)).thenReturn("");
        
        // When
        String userId = authenticationUseCase.extractUserIdFromToken(invalidToken);
        
        // Then
        assertThat(userId).isNull();
        verify(tokenService).validateToken(invalidToken);
    }
}
