package com.clinicboard.business_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
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
            PatientName name = new PatientName("João Silva Santos");
            Email email = new Email("joao.silva@email.com");
            ContactDetails contact = new ContactDetails("11999999999");
            ProfessionalId professionalId = ProfessionalId.generate();
            PatientStatus status = PatientStatus.ACTIVE;
            java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
            java.time.LocalDateTime updatedAt = null;
            
            // When
            Patient patient = new Patient(patientId, name, email, contact, professionalId, status, createdAt, updatedAt);
            
            // Then
            assertNotNull(patient);
            assertEquals(patientId, patient.getId());
            assertEquals(name, patient.getName());
            assertEquals(email, patient.getEmail());
            assertEquals(contact, patient.getContact());
            assertEquals(professionalId, patient.getAssignedProfessionalId());
            assertEquals(PatientStatus.ACTIVE, patient.getStatus());
            assertNotNull(patient.getCreatedAt());
            assertNull(patient.getUpdatedAt());
        }

        @Test
        @DisplayName("Deve criar paciente usando construtor simplificado")
        void shouldCreatePatientUsingSimpleConstructor() {
            // Given
            PatientName name = new PatientName("Maria Oliveira");
            Email email = new Email("maria.oliveira@email.com");
            ContactDetails contact = new ContactDetails("11888888888");
            ProfessionalId professionalId = ProfessionalId.generate();
            
            // When
            Patient patient = new Patient(name, email, contact, professionalId);
            
            // Then
            assertNotNull(patient);
            assertNotNull(patient.getId());
            assertEquals(name, patient.getName());
            assertEquals(email, patient.getEmail());
            assertEquals(contact, patient.getContact());
            assertEquals(professionalId, patient.getAssignedProfessionalId());
            assertEquals(PatientStatus.ACTIVE, patient.getStatus());
        }
    }

    @Nested
    @DisplayName("Regras de Negócio - Agendamento")
    class AppointmentBusinessRules {

        @Test
        @DisplayName("Paciente ativo pode agendar consulta")
        void activePatientCanScheduleAppointment() {
            // Given
            Patient patient = createValidPatient();
            AppointmentTime futureTime = AppointmentTime.of(
                java.time.LocalDateTime.now().plusDays(1).withHour(10).withMinute(0)
            );
            
            // When & Then
            assertTrue(patient.canScheduleAppointment(futureTime));
        }

        @Test
        @DisplayName("Paciente inativo não pode agendar consulta")
        void inactivePatientCannotScheduleAppointment() {
            // Given
            Patient patient = createValidPatient().deactivate("Paciente solicitou inativação");
            AppointmentTime futureTime = AppointmentTime.of(
                java.time.LocalDateTime.now().plusDays(1).withHour(10).withMinute(0)
            );
            
            // When & Then
            assertFalse(patient.canScheduleAppointment(futureTime));
        }

        @Test
        @DisplayName("Não pode agendar consulta no passado")
        void cannotScheduleAppointmentInPast() {
            // Given
            Patient patient = createValidPatient();
            AppointmentTime pastTime = AppointmentTime.of(
                java.time.LocalDateTime.now().minusDays(1)
            );
            
            // When & Then
            assertFalse(patient.canScheduleAppointment(pastTime));
        }
    }

    @Nested
    @DisplayName("Gerenciamento de Status")
    class StatusManagement {

        @Test
        @DisplayName("Deve ativar paciente inativo")
        void shouldActivateInactivePatient() {
            // Given
            Patient patient = createValidPatient().deactivate("Teste");
            
            // When
            Patient activatedPatient = patient.activate();
            
            // Then
            assertEquals(PatientStatus.ACTIVE, activatedPatient.getStatus());
            assertNotNull(activatedPatient.getUpdatedAt());
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar ativar paciente já ativo")
        void shouldThrowExceptionWhenActivatingActivePatient() {
            // Given
            Patient patient = createValidPatient();
            
            // When & Then
            assertThrows(PatientBusinessRuleException.class, () -> patient.activate());
        }

        @Test
        @DisplayName("Deve desativar paciente ativo")
        void shouldDeactivateActivePatient() {
            // Given
            Patient patient = createValidPatient();
            String reason = "Paciente solicitou desativação";
            
            // When
            Patient deactivatedPatient = patient.deactivate(reason);
            
            // Then
            assertEquals(PatientStatus.INACTIVE, deactivatedPatient.getStatus());
            assertNotNull(deactivatedPatient.getUpdatedAt());
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar desativar paciente já inativo")
        void shouldThrowExceptionWhenDeactivatingInactivePatient() {
            // Given
            Patient patient = createValidPatient().deactivate("Teste");
            
            // When & Then
            assertThrows(PatientBusinessRuleException.class, 
                () -> patient.deactivate("Tentativa de desativar novamente"));
        }

        @ParameterizedTest
        @EnumSource(PatientStatus.class)
        @DisplayName("Deve verificar corretamente se paciente está ativo")
        void shouldCheckIfPatientIsActiveCorrectly(PatientStatus status) {
            // Given
            Patient patient = createPatientWithStatus(status);
            
            // When
            boolean isActive = patient.isActive();
            
            // Then
            assertEquals(status == PatientStatus.ACTIVE, isActive);
        }
    }

    @Nested
    @DisplayName("Atualização de Informações")
    class InformationUpdate {

        @Test
        @DisplayName("Deve atualizar informações de contato")
        void shouldUpdateContactInfo() {
            // Given
            Patient patient = createValidPatient();
            ContactDetails newContact = new ContactDetails("11777777777");
            
            // When
            Patient updatedPatient = patient.updateContactInfo(newContact);
            
            // Then
            assertEquals(newContact, updatedPatient.getContact());
            assertNotNull(updatedPatient.getUpdatedAt());
        }

        @Test
        @DisplayName("Deve reatribuir paciente a outro profissional")
        void shouldReassignPatientToProfessional() {
            // Given
            Patient patient = createValidPatient();
            ProfessionalId newProfessionalId = ProfessionalId.generate();
            
            // When
            Patient reassignedPatient = patient.reassignToProfessional(newProfessionalId);
            
            // Then
            assertEquals(newProfessionalId, reassignedPatient.getAssignedProfessionalId());
            assertNotNull(reassignedPatient.getUpdatedAt());
        }

        @Test
        @DisplayName("Deve criar nova instância com ID específico")
        void shouldCreateNewInstanceWithSpecificId() {
            // Given
            Patient patient = createValidPatient();
            PatientId newId = PatientId.generate();
            
            // When
            Patient patientWithNewId = patient.withId(newId);
            
            // Then
            assertEquals(newId, patientWithNewId.getId());
            assertEquals(patient.getName(), patientWithNewId.getName());
            assertEquals(patient.getEmail(), patientWithNewId.getEmail());
        }
    }

    @Nested
    @DisplayName("Validações e Regras de Domínio")
    class DomainValidations {

        @Test
        @DisplayName("Deve validar que paciente não tem consulta duplicada no dia")
        void shouldValidateNoDuplicateAppointmentOnDate() {
            // Given
            Patient patient = createValidPatient();
            java.time.LocalDate date = java.time.LocalDate.now().plusDays(1);
            
            // When & Then - Não deve lançar exceção para validação básica
            assertDoesNotThrow(() -> patient.validateNoDuplicateAppointmentOnDate(date));
        }
    }

    @Nested
    @DisplayName("Igualdade e Identidade")
    class EqualityAndIdentity {

        @Test
        @DisplayName("Pacientes com mesmo ID devem ser iguais")
        void patientsWithSameIdShouldBeEqual() {
            // Given
            PatientId patientId = PatientId.generate();
            PatientName name1 = new PatientName("João Silva");
            PatientName name2 = new PatientName("José Santos");
            Email email1 = new Email("joao@email.com");
            Email email2 = new Email("jose@email.com");
            ContactDetails contact1 = new ContactDetails("11999999999");
            ContactDetails contact2 = new ContactDetails("11888888888");
            ProfessionalId profId = ProfessionalId.generate();
            PatientStatus status = PatientStatus.ACTIVE;
            java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
            
            Patient patient1 = new Patient(patientId, name1, email1, contact1, profId, status, createdAt, null);
            Patient patient2 = new Patient(patientId, name2, email2, contact2, profId, status, createdAt, null);
            
            // When & Then
            assertEquals(patient1, patient2);
            assertEquals(patient1.hashCode(), patient2.hashCode());
        }

        @Test
        @DisplayName("Pacientes com IDs diferentes devem ser diferentes")
        void patientsWithDifferentIdsShouldBeDifferent() {
            // Given
            PatientName name = new PatientName("João Silva");
            Email email = new Email("joao@email.com");
            ContactDetails contact = new ContactDetails("11999999999");
            ProfessionalId profId = ProfessionalId.generate();
            
            Patient patient1 = new Patient(name, email, contact, profId);
            Patient patient2 = new Patient(name, email, contact, profId);
            
            // When & Then
            assertNotEquals(patient1, patient2);
        }

        @Test
        @DisplayName("Deve ter toString representativo")
        void shouldHaveRepresentativeToString() {
            // Given
            Patient patient = createValidPatient();
            
            // When
            String toString = patient.toString();
            
            // Then
            assertNotNull(toString);
            assertTrue(toString.contains("Patient"));
            assertTrue(toString.contains(patient.getId().value()));
        }
    }

    // Helper methods
    private Patient createValidPatient() {
        PatientName name = new PatientName("João Silva");
        Email email = new Email("joao.silva@email.com");
        ContactDetails contact = new ContactDetails("11999999999");
        ProfessionalId professionalId = ProfessionalId.generate();
        return new Patient(name, email, contact, professionalId);
    }

    private Patient createPatientWithStatus(PatientStatus status) {
        Patient patient = createValidPatient();
        return switch (status) {
            case ACTIVE -> patient;
            case INACTIVE -> patient.deactivate("Teste");
            default -> patient; // Para status não implementados, retorna ativo
        };
    }
}
