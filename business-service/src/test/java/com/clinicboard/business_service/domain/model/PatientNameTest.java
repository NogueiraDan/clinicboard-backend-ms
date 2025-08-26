package com.clinicboard.business_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para PatientName Value Object
 */
@DisplayName("PatientName - Value Object Tests")
class PatientNameTest {

    @Nested
    @DisplayName("Criação Válida")
    class ValidCreation {

        @Test
        @DisplayName("Deve criar nome válido simples")
        void shouldCreateValidSimpleName() {
            // Given
            String validName = "João Silva";
            
            // When
            PatientName patientName = new PatientName(validName);
            
            // Then
            assertNotNull(patientName);
            assertEquals(validName, patientName.value());
        }

        @ParameterizedTest
        @DisplayName("Deve aceitar nomes válidos com caracteres especiais")
        @ValueSource(strings = {
            "Maria da Silva",
            "José Carlos",
            "Ana-Carolina",
            "O'Connor",
            "José María",
            "François",
            "Dr. João Silva"
        })
        void shouldAcceptValidNamesWithSpecialCharacters(String name) {
            // When & Then
            assertDoesNotThrow(() -> new PatientName(name));
        }

        @Test
        @DisplayName("Deve aceitar nome no limite mínimo de caracteres")
        void shouldAcceptMinimumLengthName() {
            // Given
            String minimumName = "Ab";
            
            // When & Then
            assertDoesNotThrow(() -> new PatientName(minimumName));
        }

        @Test
        @DisplayName("Deve aceitar nome no limite máximo de caracteres")
        void shouldAcceptMaximumLengthName() {
            // Given
            String maxName = "A".repeat(100);
            
            // When & Then
            assertDoesNotThrow(() -> new PatientName(maxName));
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
            PatientName.InvalidPatientNameException exception = 
                assertThrows(PatientName.InvalidPatientNameException.class, 
                           () -> new PatientName(invalidValue));
            
            assertNotNull(exception.getMessage());
        }

        @Test
        @DisplayName("Deve rejeitar nome muito curto")
        void shouldRejectTooShortName() {
            // Given
            String shortName = "A";
            
            // When & Then
            PatientName.InvalidPatientNameException exception = 
                assertThrows(PatientName.InvalidPatientNameException.class, 
                           () -> new PatientName(shortName));
            
            assertTrue(exception.getMessage().contains("pelo menos"));
        }

        @Test
        @DisplayName("Deve rejeitar nome muito longo")
        void shouldRejectTooLongName() {
            // Given
            String longName = "A".repeat(101);
            
            // When & Then
            PatientName.InvalidPatientNameException exception = 
                assertThrows(PatientName.InvalidPatientNameException.class, 
                           () -> new PatientName(longName));
            
            assertTrue(exception.getMessage().contains("no máximo"));
        }

        @ParameterizedTest
        @DisplayName("Deve rejeitar nomes com caracteres inválidos")
        @ValueSource(strings = {
            "João123",
            "Maria@Silva",
            "José#Carlos",
            "Ana$Carolina",
            "João&Silva",
            "Maria*Silva",
            "José+Carlos"
        })
        void shouldRejectNamesWithInvalidCharacters(String invalidName) {
            // When & Then
            PatientName.InvalidPatientNameException exception = 
                assertThrows(PatientName.InvalidPatientNameException.class, 
                           () -> new PatientName(invalidName));
            
            assertTrue(exception.getMessage().contains("caracteres inválidos"));
        }
    }

    @Nested
    @DisplayName("Comportamento de Value Object")
    class ValueObjectBehavior {

        @Test
        @DisplayName("Deve ter igualdade por valor")
        void shouldHaveValueEquality() {
            // Given
            String name = "João Silva";
            PatientName name1 = new PatientName(name);
            PatientName name2 = new PatientName(name);
            
            // When & Then
            assertEquals(name1, name2);
            assertEquals(name1.hashCode(), name2.hashCode());
        }

        @Test
        @DisplayName("Deve ter desigualdade para valores diferentes")
        void shouldHaveValueInequality() {
            // Given
            PatientName name1 = new PatientName("João Silva");
            PatientName name2 = new PatientName("Maria Santos");
            
            // When & Then
            assertNotEquals(name1, name2);
        }

        @Test
        @DisplayName("Deve ser imutável")
        void shouldBeImmutable() {
            // Given
            String originalName = "João Silva";
            PatientName patientName = new PatientName(originalName);
            
            // When
            String retrievedValue = patientName.value();
            
            // Then
            assertEquals(originalName, retrievedValue);
            
            // O record garante imutabilidade
        }

        @Test
        @DisplayName("Deve ter toString() meaningful")
        void shouldHaveMeaningfulToString() {
            // Given
            String name = "João Silva";
            PatientName patientName = new PatientName(name);
            
            // When
            String toString = patientName.toString();
            
            // Then
            assertTrue(toString.contains(name));
        }
    }

    @Nested
    @DisplayName("Métodos Utilitários")
    class UtilityMethods {

        @Test
        @DisplayName("Deve extrair primeiro nome corretamente")
        void shouldExtractFirstNameCorrectly() {
            // Given
            PatientName patientName = new PatientName("João Silva Santos");
            
            // When
            String firstName = patientName.getFirstName();
            
            // Then
            assertEquals("João", firstName);
        }

        @Test
        @DisplayName("Deve extrair último nome corretamente")
        void shouldExtractLastNameCorrectly() {
            // Given
            PatientName patientName = new PatientName("João Silva Santos");
            
            // When
            String lastName = patientName.getLastName();
            
            // Then
            assertEquals("Santos", lastName);
        }

        @Test
        @DisplayName("Deve retornar nome completo quando há apenas um nome")
        void shouldReturnFullNameWhenOnlyOneName() {
            // Given
            PatientName patientName = new PatientName("João");
            
            // When
            String firstName = patientName.getFirstName();
            String lastName = patientName.getLastName();
            
            // Then
            assertEquals("João", firstName);
            assertEquals("João", lastName);
        }

        @Test
        @DisplayName("Deve obter iniciais corretamente")
        void shouldGetInitialsCorrectly() {
            // Given
            PatientName patientName = new PatientName("João Silva Santos");
            
            // When
            String initials = patientName.getInitials();
            
            // Then
            assertEquals("J.S.S.", initials);
        }

        @Test
        @DisplayName("Deve formatar nome corretamente")
        void shouldFormatNameCorrectly() {
            // Given
            PatientName patientName = new PatientName("joão silva santos");
            
            // When
            String formatted = patientName.getFormattedName();
            
            // Then
            assertEquals("João Silva Santos", formatted);
        }
    }

    @Nested
    @DisplayName("Exception Behavior")
    class ExceptionBehavior {

        @Test
        @DisplayName("InvalidPatientNameException deve estender DomainException")
        void exceptionShouldExtendDomainException() {
            // When
            PatientName.InvalidPatientNameException exception = 
                new PatientName.InvalidPatientNameException("test message");
            
            // Then
            assertTrue(exception instanceof com.clinicboard.business_service.domain.exception.DomainException);
            assertEquals("INVALID_PATIENT_NAME", exception.getErrorCode());
            assertEquals("test message", exception.getMessage());
        }
    }
}
