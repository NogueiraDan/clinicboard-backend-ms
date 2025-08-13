package com.clinicboard.user_service.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o Value Object Email
 */
class EmailTest {

    @Test
    @DisplayName("Deve criar email válido")
    void shouldCreateValidEmail() {
        // Given
        String validEmail = "user@example.com";

        // When
        Email email = new Email(validEmail);

        // Then
        assertEquals("user@example.com", email.getValue());
        assertEquals("example.com", email.getDomain());
        assertEquals("user", email.getLocalPart());
    }

    @Test
    @DisplayName("Deve converter email para lowercase")
    void shouldConvertEmailToLowercase() {
        // Given
        String upperCaseEmail = "USER@EXAMPLE.COM";

        // When
        Email email = new Email(upperCaseEmail);

        // Then
        assertEquals("user@example.com", email.getValue());
    }

    @Test
    @DisplayName("Deve falhar com email nulo")
    void shouldFailWithNullEmail() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
    }

    @Test
    @DisplayName("Deve falhar com email vazio")
    void shouldFailWithEmptyEmail() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new Email(""));
    }

    @Test
    @DisplayName("Deve falhar com email inválido")
    void shouldFailWithInvalidEmail() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new Email("invalid-email"));
        assertThrows(IllegalArgumentException.class, () -> new Email("user@"));
        assertThrows(IllegalArgumentException.class, () -> new Email("@example.com"));
        assertThrows(IllegalArgumentException.class, () -> new Email("user@.com"));
    }

    @Test
    @DisplayName("Deve verificar igualdade de emails")
    void shouldVerifyEmailEquality() {
        // Given
        Email email1 = new Email("user@example.com");
        Email email2 = new Email("USER@EXAMPLE.COM");
        Email email3 = new Email("other@example.com");

        // When & Then
        assertEquals(email1, email2); // Mesmo email, case insensitive
        assertNotEquals(email1, email3); // Emails diferentes
        assertEquals(email1.hashCode(), email2.hashCode());
    }
}
