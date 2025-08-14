package com.clinicboard.user_service.integration;

import com.clinicboard.user_service.application.dto.UserRequestDto;
import com.clinicboard.user_service.application.port.inbound.UserUseCase;
import com.clinicboard.user_service.domain.model.valueobjects.Password;
import com.clinicboard.user_service.infrastructure.adapter.outbound.persistence.entity.UserJpaEntity;
import com.clinicboard.user_service.infrastructure.adapter.outbound.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de integração para verificar se senhas estão sendo hasheadas corretamente
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PasswordSecurityIntegrationTest {

    @Autowired
    private UserUseCase userUseCase;
    
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    void shouldHashPasswordWhenSavingUser() {
        // Given
        String plainPassword = "MinhaSenh@123";
        UserRequestDto request = new UserRequestDto(
            "João Silva",
            "joao.teste@example.com",
            plainPassword,
            "(11) 99999-9999", 
            "PROFESSIONAL"
        );

        // When
        var userResponse = userUseCase.registerUser(request);

        // Then - Verifica se foi salvo no banco
        Optional<UserJpaEntity> savedEntity = userJpaRepository.findById(userResponse.getId());
        assertThat(savedEntity).isPresent();

        UserJpaEntity entity = savedEntity.get();
        
        // CRÍTICO: Verifica se a senha NÃO está em texto plano
        assertThat(entity.getPasswordHash()).isNotEqualTo(plainPassword);
        assertThat(entity.getPasswordHash()).isNotNull();
        assertThat(entity.getPasswordHash()).isNotEmpty();
        
        // Verifica se tem salt
        assertThat(entity.getPasswordSalt()).isNotNull();
        assertThat(entity.getPasswordSalt()).isNotEmpty();
        
        // Verifica se a senha hasheada consegue ser verificada
        Password passwordVO = new Password(entity.getPasswordHash(), entity.getPasswordSalt());
        assertThat(passwordVO.matches(plainPassword)).isTrue();
        assertThat(passwordVO.matches("senhaErrada")).isFalse();
        
        System.out.println("=== VERIFICAÇÃO DE SEGURANÇA ===");
        System.out.println("Senha original: " + plainPassword);
        System.out.println("Hash salvo no banco: " + entity.getPasswordHash());
        System.out.println("Salt salvo no banco: " + entity.getPasswordSalt());
        System.out.println("Hash ≠ Senha original? " + !entity.getPasswordHash().equals(plainPassword));
    }
}
