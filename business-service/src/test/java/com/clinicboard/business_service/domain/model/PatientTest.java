package com.clinicboard.business_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.EnumSource;

import com.clinicboard.business_service.domain.exception.PatientBusinessRuleException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para Patient Aggregate
 * 
 * Objetivo: Garantir 100% de cobertura e validação de todas as regras de negócio do agregado
 */
@DisplayName("Patient - Aggregate Tests")
class PatientTest {

    @Nested
    @DisplayName("Criação Válida de Paciente")
    class ValidPatientCreation {

        @Test
        @DisplayName("Deve criar paciente com todos os dados válidos")
        void shouldCreatePatientWithValidData() {
            // Given
            PatientId patientId = PatientId.generate();
            PatientName name = new PatientName("João", "Silva Santos");
            Email email = new Email("joao.silva@email.com");
            ContactDetails contact = new ContactDetails("(11) 99999-9999", "Rua das Flores, 123");
            
            // When
            Patient patient = new Patient(patientId, name, email, contact);
            
            // Then
            assertNotNull(patient);
            assertEquals(patientId, patient.getId());
            assertEquals(name, patient.getName());
            assertEquals(email, patient.getEmail());
            assertEquals(contact, patient.getContactDetails());
            assertEquals(PatientStatus.ACTIVE, patient.getStatus());
            assertNotNull(patient.getCreatedAt());
            assertNull(patient.getUpdatedAt());
        }

        @Test
        @DisplayName("Deve criar paciente usando factory method")
        void shouldCreatePatientUsingFactoryMethod() {
            // Given
            PatientName name = new PatientName("Maria", "Oliveira");
            Email email = new Email("maria.oliveira@email.com");
            ContactDetails contact = new ContactDetails("(11) 88888-8888", "Av. Paulista, 456");
            
            // When
            Patient patient = Patient.create(name, email, contact);
            
            // Then
            assertNotNull(patient);
            assertNotNull(patient.getId());
            assertEquals(name, patient.getName());
            assertEquals(email, patient.getEmail());
            assertEquals(contact, patient.getContactDetails());
            assertEquals(PatientStatus.ACTIVE, patient.getStatus());
        }
    }

    @Nested
    @DisplayName("Validações de Entrada")
    class InputValidations {

        @Test
        @DisplayName("Deve rejeitar criação com ID nulo")
        void shouldRejectNullId() {
            // Given
            PatientName name = new PatientName("João", "Silva");
            Email email = new Email("joao@email.com");
            ContactDetails contact = new ContactDetails("(11) 99999-9999", "Rua A, 123");
            
            // When & Then
            assertThrows(IllegalArgumentException.class, 
                () -> new Patient(null, name, email, contact));
        }

        @Test
        @DisplayName("Deve rejeitar criação com nome nulo")
        void shouldRejectNullName() {
            // Given
            PatientId patientId = PatientId.generate();
            Email email = new Email("joao@email.com");
            ContactDetails contact = new ContactDetails("(11) 99999-9999", "Rua A, 123");
            
            // When & Then
            assertThrows(IllegalArgumentException.class, 
                () -> new Patient(patientId, null, email, contact));
        }

        @Test
        @DisplayName("Deve rejeitar criação com email nulo")
        void shouldRejectNullEmail() {
            // Given
            PatientId patientId = PatientId.generate();
            PatientName name = new PatientName("João", "Silva");
            ContactDetails contact = new ContactDetails("(11) 99999-9999", "Rua A, 123");
            
            // When & Then
            assertThrows(IllegalArgumentException.class, 
                () -> new Patient(patientId, name, null, contact));
        }

        @Test
        @DisplayName("Deve rejeitar criação com contato nulo")
        void shouldRejectNullContact() {
            // Given
            PatientId patientId = PatientId.generate();
            PatientName name = new PatientName("João", "Silva");
            Email email = new Email("joao@email.com");
            
            // When & Then
            assertThrows(IllegalArgumentException.class, 
                () -> new Patient(patientId, name, email, null));
        }
    }

    @Nested
    @DisplayName("Mudanças de Status")
    class StatusChanges {

        @Test
        @DisplayName("Deve permitir desativar paciente ativo")
        void shouldAllowDeactivatingActivePatient() {
            // Given
            Patient patient = createValidPatient();
            
            // When
            patient.deactivate();
            
            // Then
            assertEquals(PatientStatus.INACTIVE, patient.getStatus());
            assertNotNull(patient.getUpdatedAt());
        }

        @Test
        @DisplayName("Deve permitir reativar paciente inativo")
        void shouldAllowReactivatingInactivePatient() {
            // Given
            Patient patient = createValidPatient();
            patient.deactivate();
            
            // When
            patient.reactivate();
            
            // Then
            assertEquals(PatientStatus.ACTIVE, patient.getStatus());
            assertNotNull(patient.getUpdatedAt());
        }

        @Test
        @DisplayName("Deve permitir desativar definitivamente")
        void shouldAllowPermanentDeactivation() {
            // Given
            Patient patient = createValidPatient();
            
            // When
            patient.deactivatePermanently();
            
            // Then
            assertEquals(PatientStatus.PERMANENTLY_INACTIVE, patient.getStatus());
            assertNotNull(patient.getUpdatedAt());
        }

        @Test
        @DisplayName("Não deve permitir reativar paciente permanentemente inativo")
        void shouldNotAllowReactivatingPermanentlyInactivePatient() {
            // Given
            Patient patient = createValidPatient();
            patient.deactivatePermanently();
            
            // When & Then
            PatientBusinessRuleException exception = assertThrows(
                PatientBusinessRuleException.class, 
                () -> patient.reactivate()
            );
            
            assertTrue(exception.getMessage().contains("permanentemente inativo"));
            assertEquals(PatientStatus.PERMANENTLY_INACTIVE, patient.getStatus());
        }

        @Test
        @DisplayName("Deve ignorar desativação de paciente já inativo")
        void shouldIgnoreDeactivationOfInactivePatient() {
            // Given
            Patient patient = createValidPatient();
            patient.deactivate();
            
            // When
            patient.deactivate();
            
            // Then
            assertEquals(PatientStatus.INACTIVE, patient.getStatus());
        }

        @Test
        @DisplayName("Deve ignorar reativação de paciente já ativo")
        void shouldIgnoreReactivationOfActivePatient() {
            // Given
            Patient patient = createValidPatient();
            
            // When
            patient.reactivate();
            
            // Then
            assertEquals(PatientStatus.ACTIVE, patient.getStatus());
        }
    }

    @Nested
    @DisplayName("Atualização de Dados")
    class DataUpdates {

        @Test
        @DisplayName("Deve permitir atualizar email")
        void shouldAllowEmailUpdate() {
            // Given
            Patient patient = createValidPatient();
            Email newEmail = new Email("novo.email@email.com");
            
            // When
            patient.updateEmail(newEmail);
            
            // Then
            assertEquals(newEmail, patient.getEmail());
            assertNotNull(patient.getUpdatedAt());
        }

        @Test
        @DisplayName("Deve permitir atualizar contato")
        void shouldAllowContactUpdate() {
            // Given
            Patient patient = createValidPatient();
            ContactDetails newContact = new ContactDetails("(11) 77777-7777", "Nova Rua, 789");
            
            // When
            patient.updateContactDetails(newContact);
            
            // Then
            assertEquals(newContact, patient.getContactDetails());
            assertNotNull(patient.getUpdatedAt());
        }

        @Test
        @DisplayName("Não deve permitir atualizar email para nulo")
        void shouldNotAllowNullEmailUpdate() {
            // Given
            Patient patient = createValidPatient();
            
            // When & Then
            assertThrows(IllegalArgumentException.class, 
                () -> patient.updateEmail(null));
        }

        @Test
        @DisplayName("Não deve permitir atualizar contato para nulo")
        void shouldNotAllowNullContactUpdate() {
            // Given
            Patient patient = createValidPatient();
            
            // When & Then
            assertThrows(IllegalArgumentException.class, 
                () -> patient.updateContactDetails(null));
        }

        @Test
        @DisplayName("Não deve permitir atualizações em paciente permanentemente inativo")
        void shouldNotAllowUpdatesOnPermanentlyInactivePatient() {
            // Given
            Patient patient = createValidPatient();
            patient.deactivatePermanently();
            Email newEmail = new Email("novo@email.com");
            
            // When & Then
            PatientBusinessRuleException exception = assertThrows(
                PatientBusinessRuleException.class, 
                () -> patient.updateEmail(newEmail)
            );
            
            assertTrue(exception.getMessage().contains("permanentemente inativo"));
        }
    }

    @Nested
    @DisplayName("Comportamento de Aggregate")
    class AggregateBehavior {

        @Test
        @DisplayName("Deve ter igualdade baseada no ID")
        void shouldHaveIdBasedEquality() {
            // Given
            PatientId patientId = PatientId.generate();
            PatientName name1 = new PatientName("João", "Silva");
            PatientName name2 = new PatientName("José", "Santos");
            Email email1 = new Email("joao@email.com");
            Email email2 = new Email("jose@email.com");
            ContactDetails contact1 = new ContactDetails("(11) 99999-9999", "Rua A, 123");
            ContactDetails contact2 = new ContactDetails("(11) 88888-8888", "Rua B, 456");
            
            Patient patient1 = new Patient(patientId, name1, email1, contact1);
            Patient patient2 = new Patient(patientId, name2, email2, contact2);
            
            // When & Then
            assertEquals(patient1, patient2);
            assertEquals(patient1.hashCode(), patient2.hashCode());
        }

        @Test
        @DisplayName("Deve ter desigualdade para IDs diferentes")
        void shouldHaveInequalityForDifferentIds() {
            // Given
            PatientId patientId1 = PatientId.generate();
            PatientId patientId2 = PatientId.generate();
            PatientName name = new PatientName("João", "Silva");
            Email email = new Email("joao@email.com");
            ContactDetails contact = new ContactDetails("(11) 99999-9999", "Rua A, 123");
            
            Patient patient1 = new Patient(patientId1, name, email, contact);
            Patient patient2 = new Patient(patientId2, name, email, contact);
            
            // When & Then
            assertNotEquals(patient1, patient2);
        }

        @Test
        @DisplayName("Deve verificar se está ativo corretamente")
        void shouldCheckActiveStatusCorrectly() {
            // Given
            Patient patient = createValidPatient();
            
            // When & Then
            assertTrue(patient.isActive());
            
            // When
            patient.deactivate();
            
            // Then
            assertFalse(patient.isActive());
        }

        @Test
        @DisplayName("Deve verificar se pode ser agendado")
        void shouldCheckIfCanBeScheduled() {
            // Given
            Patient patient = createValidPatient();
            
            // When & Then
            assertTrue(patient.canBeScheduled());
            
            // When
            patient.deactivate();
            
            // Then
            assertFalse(patient.canBeScheduled());
            
            // When
            patient.reactivate();
            
            // Then
            assertTrue(patient.canBeScheduled());
            
            // When
            patient.deactivatePermanently();
            
            // Then
            assertFalse(patient.canBeScheduled());
        }
    }

    @Nested
    @DisplayName("Invariantes do Domínio")
    class DomainInvariants {

        @ParameterizedTest
        @DisplayName("Deve manter status válido em todas as transições")
        @EnumSource(PatientStatus.class)
        void shouldMaintainValidStatusInAllTransitions(PatientStatus status) {
            // Given
            Patient patient = createValidPatient();
            
            // When
            switch (status) {
                case ACTIVE -> patient.reactivate();
                case INACTIVE -> patient.deactivate();
                case PERMANENTLY_INACTIVE -> patient.deactivatePermanently();
            }
            
            // Then
            assertEquals(status, patient.getStatus());
        }

        @Test
        @DisplayName("Deve manter timestamps consistentes")
        void shouldMaintainConsistentTimestamps() {
            // Given
            Patient patient = createValidPatient();
            var createdAt = patient.getCreatedAt();
            
            // When
            patient.updateEmail(new Email("new@email.com"));
            
            // Then
            assertEquals(createdAt, patient.getCreatedAt()); // CreatedAt não deve mudar
            assertNotNull(patient.getUpdatedAt());
            assertTrue(patient.getUpdatedAt().isAfter(createdAt) || 
                      patient.getUpdatedAt().equals(createdAt));
        }
    }

    // Helper method
    private Patient createValidPatient() {
        PatientName name = new PatientName("João", "Silva");
        Email email = new Email("joao@email.com");
        ContactDetails contact = new ContactDetails("(11) 99999-9999", "Rua A, 123");
        return Patient.create(name, email, contact);
    }
}
