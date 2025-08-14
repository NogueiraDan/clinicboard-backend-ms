package com.clinicboard.user_service.application.usecase;

import com.clinicboard.user_service.application.dto.UserRequestDto;
import com.clinicboard.user_service.application.dto.UserResponseDto;
import com.clinicboard.user_service.application.mapper.DomainUserMapper;
import com.clinicboard.user_service.application.port.outbound.EventPublisher;
import com.clinicboard.user_service.application.port.outbound.UserRepositoryPort;
import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.enums.UserRole;
import com.clinicboard.user_service.domain.model.valueobjects.Email;
import com.clinicboard.user_service.domain.model.valueobjects.UserId;
import com.clinicboard.user_service.domain.service.UserDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para UserUseCaseImpl
 */
@ExtendWith(MockitoExtension.class)
class UserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private UserDomainService userDomainService;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private DomainUserMapper userMapper;

    private UserUseCaseImpl userUseCase;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCaseImpl(userRepository, userDomainService, eventPublisher, userMapper);
    }

    @Test
    @DisplayName("Deve registrar usuário com sucesso")
    void shouldRegisterUserSuccessfully() {
        // Given
        UserRequestDto request = new UserRequestDto(
            "João Silva",
            "joao@email.com",
            "MinhaSenh@123",
            "(11) 99999-9999",
            "PROFESSIONAL"
        );

        User savedUser = createMockUser();
        UserResponseDto expectedResponse = createMockUserResponse();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(expectedResponse);

        // When
        UserResponseDto result = userUseCase.registerUser(request);

        // Then
        assertNotNull(result);
        assertEquals(expectedResponse.getName(), result.getName());
        assertEquals(expectedResponse.getEmail(), result.getEmail());

        // Verifica se as validações foram chamadas
        verify(userDomainService).validateUserCreation(any(User.class));
        verify(userRepository).save(any(User.class));
        verify(eventPublisher).publishUserRegistered(
            anyString(),
            anyString(),
            anyString(),
            anyString()
        );
    }

    @Test
    @DisplayName("Deve autenticar usuário com credenciais válidas")
    void shouldAuthenticateUserWithValidCredentials() {
        // Given
        String email = "joao@email.com";
        String password = "MinhaSenh@123";
        
        User mockUser = createMockUser();
        UserResponseDto expectedResponse = createMockUserResponse();

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(mockUser));
        when(mockUser.authenticate(password)).thenReturn(true);
        when(userMapper.toResponseDto(mockUser)).thenReturn(expectedResponse);

        // When
        UserResponseDto result = userUseCase.authenticateUser(email, password);

        // Then
        assertNotNull(result);
        verify(userRepository).findByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Deve falhar na autenticação com credenciais inválidas")
    void shouldFailAuthenticationWithInvalidCredentials() {
        // Given
        String email = "joao@email.com";
        String wrongPassword = "SenhaErrada@123";
        
        User mockUser = createMockUser();

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(mockUser));
        when(mockUser.authenticate(wrongPassword)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            userUseCase.authenticateUser(email, wrongPassword)
        );
    }

    @Test
    @DisplayName("Deve falhar na autenticação com usuário não encontrado")
    void shouldFailAuthenticationWithUserNotFound() {
        // Given
        String email = "inexistente@email.com";
        String password = "MinhaSenh@123";

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            userUseCase.authenticateUser(email, password)
        );
    }

    @Test
    @DisplayName("Deve encontrar usuário por ID")
    void shouldFindUserById() {
        // Given
        String userId = "user-123";
        User mockUser = createMockUser();
        UserResponseDto expectedResponse = createMockUserResponse();

        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(mockUser));
        when(userMapper.toResponseDto(mockUser)).thenReturn(expectedResponse);

        // When
        UserResponseDto result = userUseCase.findUserById(userId);

        // Then
        assertNotNull(result);
        assertEquals(expectedResponse.getName(), result.getName());
        verify(userRepository).findById(any(UserId.class));
    }

    @Test
    @DisplayName("Deve falhar ao buscar usuário inexistente por ID")
    void shouldFailWhenUserNotFoundById() {
        // Given
        String userId = "inexistente-123";

        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            userUseCase.findUserById(userId)
        );
    }

    @Test
    @DisplayName("Deve ativar usuário com sucesso")
    void shouldActivateUserSuccessfully() {
        // Given
        String userId = "user-123";
        User mockUser = createMockUser();

        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(mockUser));
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        // When
        userUseCase.activateUser(userId);

        // Then
        verify(mockUser).activate();
        verify(userRepository).save(mockUser);
        verify(eventPublisher).publishUserActivated(
            anyString(),
            anyString(),
            anyString()
        );
    }

    @Test
    @DisplayName("Deve alterar senha com sucesso")
    void shouldChangePasswordSuccessfully() {
        // Given
        String userId = "user-123";
        String currentPassword = "SenhaAtual@123";
        String newPassword = "NovaSenha@456";
        
        User mockUser = createMockUser();

        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(mockUser));
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        // When
        userUseCase.changePassword(userId, currentPassword, newPassword);

        // Then
        verify(mockUser).changePassword(anyString(), anyString());
        verify(userRepository).save(mockUser);
        verify(eventPublisher).publishUserPasswordChanged(
            anyString(),
            anyString()
        );
    }

    @Test
    @DisplayName("Deve falhar ao alterar senha com senha atual incorreta")
    void shouldFailChangePasswordWithWrongCurrentPassword() {
        // Given
        String userId = "user-123";
        String wrongCurrentPassword = "SenhaErrada@123";
        String newPassword = "NovaSenha@456";
        
        User mockUser = createMockUser();
        doThrow(new IllegalArgumentException("Senha atual incorreta"))
            .when(mockUser).changePassword(anyString(), anyString());

        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(mockUser));

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            userUseCase.changePassword(userId, wrongCurrentPassword, newPassword)
        );

        verify(mockUser).changePassword(anyString(), anyString());
        verify(userRepository, never()).save(mockUser);
    }

    // Métodos auxiliares para criar mocks
    private User createMockUser() {
        User mockUser = mock(User.class);
        lenient().when(mockUser.getIdValue()).thenReturn("user-123");
        lenient().when(mockUser.getName()).thenReturn("João Silva");
        lenient().when(mockUser.getEmailValue()).thenReturn("joao@email.com");
        lenient().when(mockUser.getContactValue()).thenReturn("(11) 99999-9999");
        lenient().when(mockUser.getRole()).thenReturn(UserRole.PROFESSIONAL);
        return mockUser;
    }

    private UserResponseDto createMockUserResponse() {
        return new UserResponseDto(
            "user-123",
            "João Silva",
            "joao@email.com",
            "(11) 99999-9999",
            "PROFESSIONAL",
            "ACTIVE",
            null,
            null
        );
    }
}
