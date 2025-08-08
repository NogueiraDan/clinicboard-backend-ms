package com.clinicboard.business_service.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o Value Object ProfessionalId
 */
class ProfessionalIdTest {

    @Test
    @DisplayName("Deve criar um ID de profissional válido")
    void shouldCreateValidProfessionalId() {
        // Given
        String validId = "PROF-123";

        // When
        ProfessionalId professionalId = new ProfessionalId(validId);

        // Then
        assertEquals(validId, professionalId.getValue());
    }

    @Test
    @DisplayName("Deve criar ID removendo espaços extras")
    void shouldCreateIdTrimmingSpaces() {
        // Given
        String idWithSpaces = "  PROF-123  ";

        // When
        ProfessionalId professionalId = new ProfessionalId(idWithSpaces);

        // Then
        assertEquals("PROF-123", professionalId.getValue());
    }

    @Test
    @DisplayName("Deve lançar exceção com ID nulo")
    void shouldThrowExceptionWithNullId() {
        // Given/When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new ProfessionalId(null));
    }

    @Test
    @DisplayName("Deve lançar exceção com ID vazio ou apenas espaços")
    void shouldThrowExceptionWithEmptyId() {
        // Given/When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new ProfessionalId(""));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new ProfessionalId("   "));
    }

    @Test
    @DisplayName("Deve verificar igualdade de IDs")
    void shouldVerifyIdEquality() {
        // Given
        ProfessionalId id1 = new ProfessionalId("PROF-123");
        ProfessionalId id2 = new ProfessionalId("PROF-123");
        ProfessionalId id3 = new ProfessionalId("PROF-456");

        // When/Then
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        
        assertNotEquals(id1, id3);
        assertNotEquals(id1, null);
        assertNotEquals(id1, "not a professional id");
    }

    @Test
    @DisplayName("Deve ter representação string adequada")
    void shouldHaveProperStringRepresentation() {
        // Given
        ProfessionalId professionalId = new ProfessionalId("PROF-123");

        // When
        String stringRepresentation = professionalId.toString();

        // Then
        assertTrue(stringRepresentation.contains("ProfessionalId"));
        assertTrue(stringRepresentation.contains("PROF-123"));
    }

    @Test
    @DisplayName("Deve comparar IDs por conteúdo")
    void shouldCompareIdsByContent() {
        // Given
        ProfessionalId id1 = new ProfessionalId("PROF-100");
        ProfessionalId id2 = new ProfessionalId("PROF-200");
        ProfessionalId id3 = new ProfessionalId("PROF-100");

        // When/Then
        assertEquals(id1.getValue(), id3.getValue());
        assertNotEquals(id1.getValue(), id2.getValue());
    }

    @Test
    @DisplayName("Deve aceitar diferentes formatos de ID")
    void shouldAcceptDifferentIdFormats() {
        // Given/When
        ProfessionalId id1 = new ProfessionalId("123");
        ProfessionalId id2 = new ProfessionalId("PROF-456");
        ProfessionalId id3 = new ProfessionalId("USER_789");
        ProfessionalId id4 = new ProfessionalId("abc-def-123");

        // Then
        assertEquals("123", id1.getValue());
        assertEquals("PROF-456", id2.getValue());
        assertEquals("USER_789", id3.getValue());
        assertEquals("abc-def-123", id4.getValue());
    }
}
