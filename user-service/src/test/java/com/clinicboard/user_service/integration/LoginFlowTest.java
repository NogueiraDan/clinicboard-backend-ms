package com.clinicboard.user_service.integration;

import com.clinicboard.user_service.application.dto.LoginRequestDto;
import com.clinicboard.user_service.application.dto.LoginResponseDto;
import com.clinicboard.user_service.application.usecase.AuthenticationUseCaseImpl;
import com.clinicboard.user_service.application.mapper.DomainUserMapper;
import com.clinicboard.user_service.application.port.outbound.TokenServicePort;
import com.clinicboard.user_service.application.port.outbound.UserRepositoryPort;
import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.enums.UserRole;
import com.clinicboard.user_service.domain.model.valueobjects.Contact;
import com.clinicboard.user_service.domain.model.valueobjects.Email;
import com.clinicboard.user_service.domain.model.valueobjects.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Teste específico para verificar o fluxo completo de autenticação/login
 */
class LoginFlowTest {

    @Mock
    private UserRepositoryPort userRepository;
    
    @Mock
    private TokenServicePort tokenService;
    
    private AuthenticationUseCaseImpl authenticationUseCase;
    private DomainUserMapper userMapper = new DomainUserMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationUseCase = new AuthenticationUseCaseImpl(
            userRepository, tokenService, userMapper);
    }

    @Test
    void shouldAuthenticateUserWithCorrectPassword() {
        // Given - Usuário com senha hasheada no sistema
        String plainPassword = "MinhaSenh@123";
        String email = "joao@example.com";
        
        // Criamos um usuário que terá a senha hasheada automaticamente
        User storedUser = new User(
            "João Silva",
            new Email(email),
            new Password(plainPassword), // Isso vai hashear a senha
            new Contact("(11) 99999-9999"),
            UserRole.PROFESSIONAL
        );
        
        // Mock do repositório retornando o usuário com senha hasheada
        when(userRepository.findByEmail(any(Email.class)))
            .thenReturn(Optional.of(storedUser));
        
        // Mock do serviço de token
        when(tokenService.generateToken(any(User.class)))
            .thenReturn("fake-jwt-token");
        when(tokenService.getExpirationTime())
            .thenReturn(3600L);
        
        // When - Tentativa de login com senha em texto plano
        LoginRequestDto loginRequest = new LoginRequestDto(email, plainPassword);
        LoginResponseDto response = authenticationUseCase.login(loginRequest);
        
        // Then - Login deve ser bem-sucedido
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getUser().getEmail()).isEqualTo(email);
        
        System.out.println("✅ LOGIN BEM-SUCEDIDO: Senha em texto plano foi corretamente comparada com hash");
        System.out.println("Hash armazenado: " + storedUser.getPassword().getHashedValue());
        System.out.println("Salt armazenado: " + storedUser.getPassword().getSalt());
    }

    @Test
    void shouldRejectUserWithWrongPassword() {
        // Given - Usuário com senha hasheada no sistema
        String correctPassword = "MinhaSenh@123";
        String wrongPassword = "SenhaErrada456";
        String email = "joao@example.com";
        
        User storedUser = new User(
            "João Silva",
            new Email(email),
            new Password(correctPassword), // Senha correta hasheada
            new Contact("(11) 99999-9999"),
            UserRole.PROFESSIONAL
        );
        
        when(userRepository.findByEmail(any(Email.class)))
            .thenReturn(Optional.of(storedUser));
        
        // When/Then - Tentativa de login com senha incorreta deve falhar
        LoginRequestDto loginRequest = new LoginRequestDto(email, wrongPassword);
        
        assertThatThrownBy(() -> authenticationUseCase.login(loginRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Credenciais inválidas");
        
        System.out.println("✅ SENHA INCORRETA REJEITADA: Sistema não aceita senha errada");
    }

    @Test
    void shouldVerifyPasswordHashingInLoginFlow() {
        // Given
        String plainPassword = "TestPassword123@";
        String email = "test@example.com";
        
        User user = new User(
            "Test User",
            new Email(email),
            new Password(plainPassword),
            new Contact("11999999999"),
            UserRole.PROFESSIONAL
        );
        
        // When - Verificar se a senha foi hasheada
        Password hashedPassword = user.getPassword();
        
        // Then - Verificações de segurança no fluxo de login
        System.out.println("=== VERIFICAÇÃO DO FLUXO DE LOGIN ===");
        System.out.println("Senha original: " + plainPassword);
        System.out.println("Hash no domínio: " + hashedPassword.getHashedValue());
        System.out.println("Salt no domínio: " + hashedPassword.getSalt());
        
        // A senha hasheada não deve ser igual à senha original
        assertThat(hashedPassword.getHashedValue())
            .as("Hash não deve ser igual à senha em texto plano")
            .isNotEqualTo(plainPassword);
        
        // Deve conseguir autenticar com a senha correta
        assertThat(user.authenticate(plainPassword))
            .as("Deve autenticar com senha correta")
            .isTrue();
        
        // Não deve autenticar com senha incorreta
        assertThat(user.authenticate("senhaErrada"))
            .as("Não deve autenticar com senha incorreta")
            .isFalse();
        
        System.out.println("✅ FLUXO DE LOGIN SEGURO: Hash comparado corretamente");
    }
}
