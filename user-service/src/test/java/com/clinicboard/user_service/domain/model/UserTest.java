package com.clinicboard.user_service.domain.model;

import com.clinicboard.user_service.domain.model.enums.UserRole;
import com.clinicboard.user_service.domain.model.enums.UserStatus;
import com.clinicboard.user_service.domain.model.valueobjects.Contact;
import com.clinicboard.user_service.domain.model.valueobjects.Email;
import com.clinicboard.user_service.domain.model.valueobjects.Password;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o agregado User
 */
class UserTest {

    @Test
    @DisplayName("Deve criar um usuário válido")
    void shouldCreateValidUser() {
        // Given
        String name = "João Silva";
        Email email = new Email("joao@email.com");
        Password password = new Password("MinhaSenh@123");
        Contact contact = new Contact("(11) 99999-9999");
        UserRole role = UserRole.PROFESSIONAL;

        // When
        User user = new User(name, email, password, contact, role);

        // Then
        assertNotNull(user);
        assertEquals(name, user.getName());
        assertEquals(email.getValue(), user.getEmailValue());
        assertEquals(contact.getValue(), user.getContactValue());
        assertEquals(role, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertTrue(user.isActive());
        assertTrue(user.isProfessional());
        assertFalse(user.isAdmin());
    }

    @Test
    @DisplayName("Deve criar um usuário admin")
    void shouldCreateAdminUser() {
        // Given
        String name = "Admin User";
        Email email = new Email("admin@email.com");
        Password password = new Password("AdminPass@123");
        Contact contact = new Contact("(11) 88888-8888");
        UserRole role = UserRole.ADMIN;

        // When
        User user = new User(name, email, password, contact, role);

        // Then
        assertTrue(user.isAdmin());
        assertFalse(user.isProfessional());
    }

    @Test
    @DisplayName("Deve autenticar usuário com senha correta")
    void shouldAuthenticateWithCorrectPassword() {
        // Given
        String plainPassword = "MinhaSenh@123";
        User user = new User(
            "João Silva",
            new Email("joao@email.com"),
            new Password(plainPassword),
            new Contact("(11) 99999-9999"),
            UserRole.PROFESSIONAL
        );

        // When & Then
        assertTrue(user.authenticate(plainPassword));
        assertFalse(user.authenticate("SenhaErrada@123"));
    }

    @Test
    @DisplayName("Deve alterar senha quando fornecida senha atual correta")
    void shouldChangePasswordWhenCurrentPasswordIsCorrect() {
        // Given
        String currentPassword = "SenhaAtual@123";
        String newPassword = "NovaSenha@456";
        
        User user = new User(
            "João Silva",
            new Email("joao@email.com"),
            new Password(currentPassword),
            new Contact("(11) 99999-9999"),
            UserRole.PROFESSIONAL
        );

        // When
        user.changePassword(currentPassword, newPassword);

        // Then
        assertFalse(user.authenticate(currentPassword));
        assertTrue(user.authenticate(newPassword));
    }

    @Test
    @DisplayName("Deve ativar usuário inativo")
    void shouldActivateInactiveUser() {
        // Given
        User user = new User(
            "João Silva",
            new Email("joao@email.com"),
            new Password("MinhaSenh@123"),
            new Contact("(11) 99999-9999"),
            UserRole.PROFESSIONAL
        );
        user.deactivate(); // Primeiro desativa

        // When
        user.activate();

        // Then
        assertTrue(user.isActive());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("Deve desativar usuário ativo")
    void shouldDeactivateActiveUser() {
        // Given
        User user = new User(
            "João Silva",
            new Email("joao@email.com"),
            new Password("MinhaSenh@123"),
            new Contact("(11) 99999-9999"),
            UserRole.PROFESSIONAL
        );

        // When
        user.deactivate();

        // Then
        assertFalse(user.isActive());
        assertEquals(UserStatus.INACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("Deve atualizar perfil do usuário")
    void shouldUpdateUserProfile() {
        // Given
        User user = new User(
            "Nome Original",
            new Email("original@email.com"),
            new Password("MinhaSenh@123"),
            new Contact("(11) 11111-1111"),
            UserRole.PROFESSIONAL
        );

        String newName = "Nome Atualizado";
        Contact newContact = new Contact("(11) 22222-2222");

        // When
        user.updateProfile(newName, newContact);

        // Then
        assertEquals(newName, user.getName());
        assertEquals(newContact.getValue(), user.getContactValue());
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com nome vazio")
    void shouldFailWhenCreatingUserWithEmptyName() {
        // Given
        String emptyName = "";
        Email email = new Email("joao@email.com");
        Password password = new Password("MinhaSenh@123");
        Contact contact = new Contact("(11) 99999-9999");
        UserRole role = UserRole.PROFESSIONAL;

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new User(emptyName, email, password, contact, role)
        );
    }

    @Test
    @DisplayName("Deve falhar ao alterar para a mesma senha")
    void shouldFailWhenChangingToSamePassword() {
        // Given
        String password = "MinhaSenh@123";
        User user = new User(
            "João Silva",
            new Email("joao@email.com"),
            new Password(password),
            new Contact("(11) 99999-9999"),
            UserRole.PROFESSIONAL
        );

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            user.changePassword(password, password)
        );
    }

    @Test
    @DisplayName("Deve falhar ao ativar usuário já ativo")
    void shouldFailWhenActivatingActiveUser() {
        // Given
        User user = new User(
            "João Silva",
            new Email("joao@email.com"),
            new Password("MinhaSenh@123"),
            new Contact("(11) 99999-9999"),
            UserRole.PROFESSIONAL
        );

        // When & Then
        assertThrows(IllegalStateException.class, user::activate);
    }

    @Test
    @DisplayName("Deve falhar ao desativar usuário já inativo")
    void shouldFailWhenDeactivatingInactiveUser() {
        // Given
        User user = new User(
            "João Silva",
            new Email("joao@email.com"),
            new Password("MinhaSenh@123"),
            new Contact("(11) 99999-9999"),
            UserRole.PROFESSIONAL
        );
        user.deactivate(); // Primeiro desativa

        // When & Then
        assertThrows(IllegalStateException.class, user::deactivate);
    }

    @Test
    @DisplayName("Usuário inativo não deve conseguir se autenticar")
    void inactiveUserShouldNotAuthenticate() {
        // Given
        String password = "MinhaSenh@123";
        User user = new User(
            "João Silva",
            new Email("joao@email.com"),
            new Password(password),
            new Contact("(11) 99999-9999"),
            UserRole.PROFESSIONAL
        );
        user.deactivate();

        // When & Then
        assertThrows(IllegalStateException.class, () ->
            user.authenticate(password)
        );
    }

    @Test
    @DisplayName("Deve trocar senha com sucesso")
    void shouldChangePasswordSuccessfully() {
        // Given
        User user = new User(
            "João Silva",
            new Email("joao@email.com"),
            new Password("MinhaSenh@123"),
            new Contact("(11) 99999-9999"),
            UserRole.PROFESSIONAL
        );
        String currentPassword = "MinhaSenh@123";
        String newPassword = "NovaSenh@456";

        // When
        user.changePassword(currentPassword, newPassword);

        // Then
        assertTrue(user.authenticate(newPassword));
        assertFalse(user.authenticate(currentPassword));
    }

    @Test
    @DisplayName("Deve falhar ao trocar senha com senha atual incorreta")
    void shouldFailWhenChangingPasswordWithWrongCurrentPassword() {
        // Given
        User user = new User(
            "João Silva",
            new Email("joao@email.com"),
            new Password("MinhaSenh@123"),
            new Contact("(11) 99999-9999"),
            UserRole.PROFESSIONAL
        );
        String wrongCurrentPassword = "SenhaErrada@123";
        String newPassword = "NovaSenh@456";

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            user.changePassword(wrongCurrentPassword, newPassword)
        );
    }
}
