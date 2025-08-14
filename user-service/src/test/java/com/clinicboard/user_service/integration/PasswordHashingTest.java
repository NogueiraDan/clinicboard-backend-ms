package com.clinicboard.user_service.integration;

import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.enums.UserRole;
import com.clinicboard.user_service.domain.model.valueobjects.Contact;
import com.clinicboard.user_service.domain.model.valueobjects.Email;
import com.clinicboard.user_service.domain.model.valueobjects.Password;
import com.clinicboard.user_service.infrastructure.adapter.outbound.persistence.entity.UserJpaEntity;
import com.clinicboard.user_service.infrastructure.adapter.outbound.persistence.mapper.UserPersistenceMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste unitário para verificar se as senhas são hasheadas corretamente
 */
class PasswordHashingTest {

    private final UserPersistenceMapper persistenceMapper = new UserPersistenceMapper();

    @Test
    void shouldHashPasswordWhenPersisting() {
        // Given
        String plainPassword = "MinhaSenh@123";
        User user = new User(
            "João Silva",
            new Email("joao@example.com"),
            new Password(plainPassword),
            new Contact("(11) 99999-9999"),
            UserRole.PROFESSIONAL
        );

        // When - Converter domínio para entidade (deve hashear a senha)
        UserJpaEntity entity = persistenceMapper.toEntity(user);

        // Then - Verificações de segurança
        System.out.println("=== VERIFICAÇÃO DE SEGURANÇA DA SENHA ===");
        System.out.println("Senha original: " + plainPassword);
        System.out.println("Hash salvo: " + entity.getPasswordHash());
        System.out.println("Salt salvo: " + entity.getPasswordSalt());
        
        // A senha NÃO deve estar em texto plano
        assertThat(entity.getPasswordHash())
            .as("Hash não deve ser igual à senha em texto plano")
            .isNotEqualTo(plainPassword);
        
        // Hash e Salt devem existir
        assertThat(entity.getPasswordHash())
            .as("Hash deve existir")
            .isNotNull()
            .isNotEmpty();
        
        assertThat(entity.getPasswordSalt())
            .as("Salt deve existir")
            .isNotNull()
            .isNotEmpty();
        
        // Hash deve ter tamanho apropriado para SHA-256 em Base64
        assertThat(entity.getPasswordHash().length())
            .as("Hash deve ter tamanho apropriado")
            .isGreaterThan(20);
        
        // Salt deve ter tamanho apropriado
        assertThat(entity.getPasswordSalt().length())
            .as("Salt deve ter tamanho apropriado")
            .isGreaterThan(10);
        
        System.out.println("✅ TESTE PASSADO: Senha foi hasheada corretamente");
    }

    @Test
    void shouldVerifyPasswordAfterRoundTrip() {
        // Given
        String plainPassword = "MinhaSenh@123";
        User originalUser = new User(
            "João Silva",
            new Email("joao@example.com"),
            new Password(plainPassword),
            new Contact("(11) 99999-9999"),
            UserRole.PROFESSIONAL
        );

        // When - Round-trip: domínio -> entidade -> domínio
        UserJpaEntity entity = persistenceMapper.toEntity(originalUser);
        User reconstructedUser = persistenceMapper.toDomain(entity);

        // Then - Deve conseguir verificar a senha original
        assertThat(reconstructedUser.authenticate(plainPassword))
            .as("Deve ser possível autenticar com a senha original")
            .isTrue();
        
        // Não deve aceitar senha incorreta
        assertThat(reconstructedUser.authenticate("senhaErrada"))
            .as("Não deve aceitar senha incorreta")
            .isFalse();
        
        System.out.println("✅ TESTE PASSADO: Round-trip funciona corretamente");
    }

    @Test
    void shouldHashDifferentPasswordsDifferently() {
        // Given
        String password1 = "MinhaSenh@123";
        String password2 = "OutraSenh@456";
        
        User user1 = new User("User1", new Email("user1@example.com"), 
                             new Password(password1), new Contact("11999999999"), UserRole.PROFESSIONAL);
        User user2 = new User("User2", new Email("user2@example.com"), 
                             new Password(password2), new Contact("11999999998"), UserRole.PROFESSIONAL);

        // When
        UserJpaEntity entity1 = persistenceMapper.toEntity(user1);
        UserJpaEntity entity2 = persistenceMapper.toEntity(user2);

        // Then - Hashes devem ser diferentes
        assertThat(entity1.getPasswordHash())
            .as("Senhas diferentes devem gerar hashes diferentes")
            .isNotEqualTo(entity2.getPasswordHash());
        
        assertThat(entity1.getPasswordSalt())
            .as("Salts devem ser diferentes")
            .isNotEqualTo(entity2.getPasswordSalt());
        
        System.out.println("✅ TESTE PASSADO: Senhas diferentes geram hashes diferentes");
    }
}
