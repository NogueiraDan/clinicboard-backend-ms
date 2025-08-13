package com.clinicboard.user_service.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o Value Object Password
 */
class PasswordTest {

    @Test
    @DisplayName("Deve criar senha válida e fazer hash")
    void shouldCreateValidPasswordAndHash() {
        // Given
        String plainPassword = "MinhaSenh@123";

        // When
        Password password = new Password(plainPassword);

        // Then
        assertNotNull(password.getHashedValue());
        assertNotNull(password.getSalt());
        assertNotEquals(plainPassword, password.getHashedValue());
        assertTrue(password.matches(plainPassword));
        assertFalse(password.matches("outrasenha"));
    }

    @Test
    @DisplayName("Deve verificar correspondência de senha")
    void shouldVerifyPasswordMatch() {
        // Given
        String correctPassword = "MinhaSenh@123";
        String wrongPassword = "SenhaErrada@456";
        Password password = new Password(correctPassword);

        // When & Then
        assertTrue(password.matches(correctPassword));
        assertFalse(password.matches(wrongPassword));
        assertFalse(password.matches(null));
    }

    @Test
    @DisplayName("Deve criar senha a partir de hash e salt existentes")
    void shouldCreatePasswordFromExistingHashAndSalt() {
        // Given
        String plainPassword = "MinhaSenh@123";
        Password originalPassword = new Password(plainPassword);
        String hash = originalPassword.getHashedValue();
        String salt = originalPassword.getSalt();

        // When
        Password reconstructedPassword = new Password(hash, salt);

        // Then
        assertTrue(reconstructedPassword.matches(plainPassword));
        assertEquals(hash, reconstructedPassword.getHashedValue());
        assertEquals(salt, reconstructedPassword.getSalt());
    }

    @Test
    @DisplayName("Deve falhar com senha nula")
    void shouldFailWithNullPassword() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new Password(null));
    }

    @Test
    @DisplayName("Deve falhar com senha vazia")
    void shouldFailWithEmptyPassword() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new Password(""));
    }

    @Test
    @DisplayName("Deve falhar com senha muito curta")
    void shouldFailWithShortPassword() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new Password("12345"));
    }

    @Test
    @DisplayName("Deve falhar com senha sem caracteres especiais")
    void shouldFailWithPasswordWithoutSpecialCharacters() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new Password("MinhaSenh123"));
    }

    @Test
    @DisplayName("Deve falhar com senha sem números")
    void shouldFailWithPasswordWithoutNumbers() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new Password("MinhaSenh@"));
    }

    @Test
    @DisplayName("Deve falhar com senha sem letras maiúsculas")
    void shouldFailWithPasswordWithoutUppercase() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new Password("minhasenha@123"));
    }

    @Test
    @DisplayName("Deve falhar com senha sem letras minúsculas")
    void shouldFailWithPasswordWithoutLowercase() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new Password("MINHASENHA@123"));
    }

    @Test
    @DisplayName("Deve verificar igualdade de senhas")
    void shouldVerifyPasswordEquality() {
        // Given
        String plainPassword = "MinhaSenh@123";
        Password password1 = new Password(plainPassword);
        Password password2 = new Password(plainPassword);

        // When & Then
        // Senhas com mesma string geram hashes diferentes devido aos salts diferentes
        assertNotEquals(password1, password2);
        
        // Mas ambas devem fazer match com a senha original
        assertTrue(password1.matches(plainPassword));
        assertTrue(password2.matches(plainPassword));
    }

    @Test
    @DisplayName("ToString deve ocultar a senha")
    void toStringShouldHidePassword() {
        // Given
        Password password = new Password("MinhaSenh@123");

        // When
        String toString = password.toString();

        // Then
        assertEquals("***HIDDEN***", toString);
    }
}
