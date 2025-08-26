package com.clinicboard.business_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para AppointmentId Value Object
 * 
 * Objetivo: Garantir 100% de cobertura e validação de todas as regras de negócio
 */
@DisplayName("AppointmentId - Value Object Tests")
class AppointmentIdTest {

    @Nested
    @DisplayName("Criação Válida")
    class ValidCreation {

        @Test
        @DisplayName("Deve criar AppointmentId com UUID válido")
        void shouldCreateWithValidUUID() {
            // Given
            String validUUID = "123e4567-e89b-12d3-a456-426614174000";
            
            // When
            AppointmentId appointmentId = new AppointmentId(validUUID);
            
            // Then
            assertNotNull(appointmentId);
            assertEquals(validUUID, appointmentId.value());
        }

        @Test
        @DisplayName("Deve criar AppointmentId usando factory method")
        void shouldCreateUsingFactoryMethod() {
            // Given
            String validUUID = "550e8400-e29b-41d4-a716-446655440000";
            
            // When
            AppointmentId appointmentId = AppointmentId.of(validUUID);
            
            // Then
            assertNotNull(appointmentId);
            assertEquals(validUUID, appointmentId.value());
        }

        @Test
        @DisplayName("Deve gerar novo AppointmentId aleatório")
        void shouldGenerateRandomAppointmentId() {
            // When
            AppointmentId appointmentId1 = AppointmentId.generate();
            AppointmentId appointmentId2 = AppointmentId.generate();
            
            // Then
            assertNotNull(appointmentId1);
            assertNotNull(appointmentId2);
            assertNotEquals(appointmentId1.value(), appointmentId2.value());
            
            // Verifica formato UUID
            assertTrue(appointmentId1.value().matches(
                "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
            ));
        }

        @ParameterizedTest
        @DisplayName("Deve aceitar UUIDs em diferentes formatos de case")
        @ValueSource(strings = {
            "123e4567-e89b-12d3-a456-426614174000",
            "123E4567-E89B-12D3-A456-426614174000",
            "123e4567-E89B-12d3-A456-426614174000"
        })
        void shouldAcceptDifferentCaseFormats(String uuid) {
            // When & Then
            assertDoesNotThrow(() -> new AppointmentId(uuid));
        }
    }

    @Nested
    @DisplayName("Validações de Entrada")
    class InputValidations {

        @ParameterizedTest
        @DisplayName("Deve rejeitar valores nulos ou vazios")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        void shouldRejectNullOrEmptyValues(String invalidValue) {
            // When & Then
            AppointmentId.InvalidAppointmentIdException exception = 
                assertThrows(AppointmentId.InvalidAppointmentIdException.class, 
                           () -> new AppointmentId(invalidValue));
            
            assertTrue(exception.getMessage().contains("não pode ser"));
            assertEquals("INVALID_APPOINTMENT_ID", exception.getErrorCode());
        }

        @ParameterizedTest
        @DisplayName("Deve rejeitar formatos inválidos de UUID")
        @ValueSource(strings = {
            "123",
            "123e4567-e89b-12d3-a456",
            "123e4567-e89b-12d3-a456-426614174000-extra",
            "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
            "123e4567_e89b_12d3_a456_426614174000",
            "123e4567-e89b-12d3-a456-42661417400g"
        })
        void shouldRejectInvalidUUIDFormats(String invalidUUID) {
            // When & Then
            AppointmentId.InvalidAppointmentIdException exception = 
                assertThrows(AppointmentId.InvalidAppointmentIdException.class, 
                           () -> new AppointmentId(invalidUUID));
            
            assertTrue(exception.getMessage().contains("formato UUID válido"));
            assertEquals("INVALID_APPOINTMENT_ID", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Comportamento de Value Object")
    class ValueObjectBehavior {

        @Test
        @DisplayName("Deve ter igualdade por valor (equals)")
        void shouldHaveValueEquality() {
            // Given
            String uuid = "123e4567-e89b-12d3-a456-426614174000";
            AppointmentId id1 = new AppointmentId(uuid);
            AppointmentId id2 = new AppointmentId(uuid);
            
            // When & Then
            assertEquals(id1, id2);
            assertEquals(id1.hashCode(), id2.hashCode());
        }

        @Test
        @DisplayName("Deve ter desigualdade para valores diferentes")
        void shouldHaveValueInequality() {
            // Given
            AppointmentId id1 = new AppointmentId("123e4567-e89b-12d3-a456-426614174000");
            AppointmentId id2 = new AppointmentId("550e8400-e29b-41d4-a716-446655440000");
            
            // When & Then
            assertNotEquals(id1, id2);
            assertNotEquals(id1.hashCode(), id2.hashCode());
        }

        @Test
        @DisplayName("Deve ser imutável")
        void shouldBeImmutable() {
            // Given
            String originalUUID = "123e4567-e89b-12d3-a456-426614174000";
            AppointmentId appointmentId = new AppointmentId(originalUUID);
            
            // When
            String retrievedValue = appointmentId.value();
            
            // Then
            assertEquals(originalUUID, retrievedValue);
            
            // Não deve ter métodos setters (verificado pela compilação)
            // O record garante imutabilidade
        }

        @Test
        @DisplayName("Deve ter toString() meaningful")
        void shouldHaveMeaningfulToString() {
            // Given
            String uuid = "123e4567-e89b-12d3-a456-426614174000";
            AppointmentId appointmentId = new AppointmentId(uuid);
            
            // When
            String toString = appointmentId.toString();
            
            // Then
            assertTrue(toString.contains(uuid));
        }
    }

    @Nested
    @DisplayName("Factory Methods")
    class FactoryMethods {

        @Test
        @DisplayName("Factory method of() deve ter mesmo comportamento do construtor")
        void factoryMethodShouldBehaveLikeConstructor() {
            // Given
            String uuid = "123e4567-e89b-12d3-a456-426614174000";
            
            // When
            AppointmentId fromConstructor = new AppointmentId(uuid);
            AppointmentId fromFactory = AppointmentId.of(uuid);
            
            // Then
            assertEquals(fromConstructor, fromFactory);
        }

        @Test
        @DisplayName("Factory method generate() deve criar IDs únicos")
        void generateShouldCreateUniqueIds() {
            // When
            AppointmentId[] ids = new AppointmentId[100];
            for (int i = 0; i < 100; i++) {
                ids[i] = AppointmentId.generate();
            }
            
            // Then
            // Verifica que todos são únicos
            for (int i = 0; i < 100; i++) {
                for (int j = i + 1; j < 100; j++) {
                    assertNotEquals(ids[i], ids[j], 
                        "IDs gerados devem ser únicos: " + ids[i] + " vs " + ids[j]);
                }
            }
        }
    }

    @Nested
    @DisplayName("Exception Behavior")
    class ExceptionBehavior {

        @Test
        @DisplayName("InvalidAppointmentIdException deve estender DomainException")
        void exceptionShouldExtendDomainException() {
            // When
            AppointmentId.InvalidAppointmentIdException exception = 
                new AppointmentId.InvalidAppointmentIdException("test message");
            
            // Then
            assertTrue(exception instanceof com.clinicboard.business_service.domain.exception.DomainException);
            assertEquals("INVALID_APPOINTMENT_ID", exception.getErrorCode());
            assertEquals("test message", exception.getMessage());
        }
    }
}
