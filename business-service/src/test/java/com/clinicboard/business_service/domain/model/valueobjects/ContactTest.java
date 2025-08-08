package com.clinicboard.business_service.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o Value Object Contact
 */
class ContactTest {

    @Test
    @DisplayName("Deve criar um contato válido")
    void shouldCreateValidContact() {
        // Given/When
        Contact contact = new Contact("11987654321", "test@email.com");

        // Then
        assertEquals("11987654321", contact.getPhone());
        assertEquals("test@email.com", contact.getEmail());
    }

    @Test
    @DisplayName("Deve aceitar telefone com formatação")
    void shouldAcceptFormattedPhone() {
        // Given/When
        Contact contact = new Contact("(11) 98765-4321", "test@email.com");

        // Then
        assertEquals("(11) 98765-4321", contact.getPhone());
    }

    @Test
    @DisplayName("Deve lançar exceção com telefone inválido")
    void shouldThrowExceptionWithInvalidPhone() {
        // Given/When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact(null, "test@email.com"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("", "test@email.com"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("   ", "test@email.com"));
        
        // Muito poucos dígitos
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("123456789", "test@email.com"));
        
        // Muitos dígitos
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("123456789012", "test@email.com"));
    }

    @Test
    @DisplayName("Deve lançar exceção com email inválido")
    void shouldThrowExceptionWithInvalidEmail() {
        // Given/When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("11987654321", null));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("11987654321", ""));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("11987654321", "   "));
        
        // Email sem @
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("11987654321", "invalid-email"));
        
        // Email sem .
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("11987654321", "invalid@email"));
    }

    @Test
    @DisplayName("Deve verificar igualdade de contatos")
    void shouldVerifyContactEquality() {
        // Given
        Contact contact1 = new Contact("11987654321", "test@email.com");
        Contact contact2 = new Contact("11987654321", "test@email.com");
        Contact contact3 = new Contact("11999887766", "test@email.com");
        Contact contact4 = new Contact("11987654321", "other@email.com");

        // When/Then
        assertEquals(contact1, contact2);
        assertEquals(contact1.hashCode(), contact2.hashCode());
        
        assertNotEquals(contact1, contact3);
        assertNotEquals(contact1, contact4);
        assertNotEquals(contact1, null);
        assertNotEquals(contact1, "not a contact");
    }

    @Test
    @DisplayName("Deve ter representação string adequada")
    void shouldHaveProperStringRepresentation() {
        // Given
        Contact contact = new Contact("11987654321", "test@email.com");

        // When
        String stringRepresentation = contact.toString();

        // Then
        assertTrue(stringRepresentation.contains("11987654321"));
        assertTrue(stringRepresentation.contains("test@email.com"));
        assertTrue(stringRepresentation.contains("Contact"));
    }
}
