package com.clinicboard.business_service.domain.model;

import com.clinicboard.business_service.domain.model.valueobjects.Contact;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o agregado Patient
 */
class PatientTest {

    @Test
    @DisplayName("Deve criar um paciente válido")
    void shouldCreateValidPatient() {
        // Given
        String name = "João Silva";
        Contact contact = new Contact("11987654321", "joao.silva@email.com");
        ProfessionalId professionalId = new ProfessionalId("prof-123");

        // When
        Patient patient = new Patient(name, contact, professionalId);

        // Then
        assertNotNull(patient);
        assertEquals(name, patient.getName());
        assertEquals("joao.silva@email.com", patient.getEmail());
        assertEquals("11987654321", patient.getContactPhone());
        assertEquals("prof-123", patient.getProfessionalIdValue());
    }

    @Test
    @DisplayName("Deve atualizar informações do paciente")
    void shouldUpdatePatientInformation() {
        // Given
        String originalName = "João Silva";
        Contact originalContact = new Contact("11987654321", "joao.silva@email.com");
        ProfessionalId professionalId = new ProfessionalId("prof-123");
        
        Patient patient = new Patient(originalName, originalContact, professionalId);

        String newName = "João Santos Silva";
        Contact newContact = new Contact("11999887766", "joao.santos@email.com");

        // When
        patient.updateInformation(newName, newContact);

        // Then
        assertEquals(newName, patient.getName());
        assertEquals("joao.santos@email.com", patient.getEmail());
        assertEquals("11999887766", patient.getContactPhone());
    }

    @Test
    @DisplayName("Deve alterar profissional responsável")
    void shouldChangeProfessional() {
        // Given
        Contact contact = new Contact("11987654321", "joao.silva@email.com");
        ProfessionalId originalProfessional = new ProfessionalId("prof-123");
        ProfessionalId newProfessional = new ProfessionalId("prof-456");
        
        Patient patient = new Patient("João Silva", contact, originalProfessional);

        // When
        patient.changeProfessional(newProfessional);

        // Then
        assertEquals("prof-456", patient.getProfessionalIdValue());
        assertTrue(patient.isManagedBy(newProfessional));
        assertFalse(patient.isManagedBy(originalProfessional));
    }

    @Test
    @DisplayName("Deve verificar se é gerenciado por profissional")
    void shouldCheckIfManagedByProfessional() {
        // Given
        Contact contact = new Contact("11987654321", "joao.silva@email.com");
        ProfessionalId professionalId = new ProfessionalId("prof-123");
        ProfessionalId anotherProfessional = new ProfessionalId("prof-456");
        
        Patient patient = new Patient("João Silva", contact, professionalId);

        // When/Then
        assertTrue(patient.isManagedBy(professionalId));
        assertFalse(patient.isManagedBy(anotherProfessional));
    }

    @Test
    @DisplayName("Deve lançar exceção com nome inválido")
    void shouldThrowExceptionWithInvalidName() {
        // Given
        Contact contact = new Contact("11987654321", "joao.silva@email.com");
        ProfessionalId professionalId = new ProfessionalId("prof-123");

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new Patient(null, contact, professionalId));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Patient("", contact, professionalId));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Patient("   ", contact, professionalId));
        
        // Nome muito longo
        String longName = "a".repeat(101);
        assertThrows(IllegalArgumentException.class, () -> 
            new Patient(longName, contact, professionalId));
    }

    @Test
    @DisplayName("Deve lançar exceção com contato inválido")
    void shouldThrowExceptionWithInvalidContact() {
        // Given
        ProfessionalId professionalId = new ProfessionalId("prof-123");

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new Patient("João Silva", null, professionalId));
    }

    @Test
    @DisplayName("Deve lançar exceção com professional ID inválido")
    void shouldThrowExceptionWithInvalidProfessionalId() {
        // Given
        Contact contact = new Contact("11987654321", "joao.silva@email.com");

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new Patient("João Silva", contact, null));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar com nome inválido")
    void shouldThrowExceptionWhenUpdatingWithInvalidName() {
        // Given
        Contact contact = new Contact("11987654321", "joao.silva@email.com");
        ProfessionalId professionalId = new ProfessionalId("prof-123");
        Patient patient = new Patient("João Silva", contact, professionalId);

        Contact newContact = new Contact("11999887766", "joao.novo@email.com");

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            patient.updateInformation(null, newContact));
        
        assertThrows(IllegalArgumentException.class, () -> 
            patient.updateInformation("", newContact));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar com contato inválido")
    void shouldThrowExceptionWhenUpdatingWithInvalidContact() {
        // Given
        Contact contact = new Contact("11987654321", "joao.silva@email.com");
        ProfessionalId professionalId = new ProfessionalId("prof-123");
        Patient patient = new Patient("João Silva", contact, professionalId);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            patient.updateInformation("João Santos", null));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar para professional ID inválido")
    void shouldThrowExceptionWhenChangingToInvalidProfessionalId() {
        // Given
        Contact contact = new Contact("11987654321", "joao.silva@email.com");
        ProfessionalId professionalId = new ProfessionalId("prof-123");
        Patient patient = new Patient("João Silva", contact, professionalId);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            patient.changeProfessional(null));
    }
}
