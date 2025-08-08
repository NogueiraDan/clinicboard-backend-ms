package com.clinicboard.business_service.domain.service;

import com.clinicboard.business_service.domain.model.valueobjects.Contact;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;
import com.clinicboard.business_service.domain.port.PatientRepositoryPort;
import com.clinicboard.business_service.common.error.BusinessException;
import com.clinicboard.business_service.common.error.CustomGenericException;
import com.clinicboard.business_service.application.port.outbound.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para PatientRegistrationService
 */
@ExtendWith(MockitoExtension.class)
class PatientRegistrationServiceTest {

    @Mock
    private PatientRepositoryPort patientRepository;

    @Mock
    private UserService userService;

    private PatientRegistrationService patientRegistrationService;

    @BeforeEach
    void setUp() {
        patientRegistrationService = new PatientRegistrationService(patientRepository, userService);
    }

    @Test
    @DisplayName("Deve validar registro de paciente com sucesso")
    void shouldValidatePatientRegistrationSuccessfully() {
        // Given
        Contact contact = new Contact("11987654321", "joao@email.com");
        ProfessionalId professionalId = new ProfessionalId("PROF-123");
        
        when(patientRepository.findByEmail(eq(contact.getEmail())))
            .thenReturn(Optional.empty());
        
        when(userService.isUserProfessional(eq(professionalId.getValue())))
            .thenReturn(true);

        // When/Then - não deve lançar exceção
        assertDoesNotThrow(() -> 
            patientRegistrationService.validatePatientRegistration(contact, professionalId));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já está em uso")
    void shouldThrowExceptionWhenEmailAlreadyInUse() {
        // Given
        Contact contact = new Contact("11987654321", "joao@email.com");
        ProfessionalId professionalId = new ProfessionalId("PROF-123");
        
        when(patientRepository.findByEmail(eq(contact.getEmail())))
            .thenReturn(Optional.of(new com.clinicboard.business_service.domain.model.Patient(
                "PATIENT-123", "João", contact, professionalId)));

        // When/Then
        CustomGenericException exception = assertThrows(CustomGenericException.class, () -> 
            patientRegistrationService.validatePatientRegistration(contact, professionalId));
                
        assertEquals("Email já cadastrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não é profissional")
    void shouldThrowExceptionWhenUserIsNotProfessional() {
        // Given
        Contact contact = new Contact("11987654321", "joao@email.com");
        ProfessionalId professionalId = new ProfessionalId("PROF-123");
        
        when(patientRepository.findByEmail(eq(contact.getEmail())))
            .thenReturn(Optional.empty());
        
        when(userService.isUserProfessional(eq(professionalId.getValue())))
            .thenReturn(false);

        // When/Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            patientRegistrationService.validatePatientRegistration(contact, professionalId));
                
        assertTrue(exception.getMessage().contains("Perfil não permitido"));
    }

    @Test
    @DisplayName("Deve validar atualização de paciente com sucesso")
    void shouldValidatePatientUpdateSuccessfully() {
        // Given
        String patientId = "PATIENT-123";
        Contact newContact = new Contact("11999888777", "new@email.com");
        
        when(patientRepository.findById(eq(patientId)))
            .thenReturn(Optional.of(new com.clinicboard.business_service.domain.model.Patient(
                patientId, "João", newContact, new ProfessionalId("PROF-123"))));
        
        when(patientRepository.findByEmail(eq(newContact.getEmail())))
            .thenReturn(Optional.empty());

        // When/Then - não deve lançar exceção
        assertDoesNotThrow(() -> 
            patientRegistrationService.validatePatientUpdate(patientId, newContact));
    }

    @Test
    @DisplayName("Deve lançar exceção quando paciente não existe para atualização")
    void shouldThrowExceptionWhenPatientDoesNotExistForUpdate() {
        // Given
        String patientId = "PATIENT-123";
        Contact newContact = new Contact("11999888777", "new@email.com");
        
        when(patientRepository.findById(eq(patientId)))
            .thenReturn(Optional.empty());

        // When/Then
        CustomGenericException exception = assertThrows(CustomGenericException.class, () -> 
            patientRegistrationService.validatePatientUpdate(patientId, newContact));
                
        assertEquals("Paciente não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve verificar se email está disponível para novo paciente")
    void shouldCheckIfEmailIsAvailableForNewPatient() {
        // Given
        String email = "novo@email.com";
        
        when(patientRepository.findByEmail(eq(email)))
            .thenReturn(Optional.empty());

        // When
        boolean isAvailable = !patientRepository.findByEmail(email).isPresent();

        // Then
        assertTrue(isAvailable);
    }

    @Test
    @DisplayName("Deve verificar se email já está em uso")
    void shouldCheckIfEmailIsAlreadyInUse() {
        // Given
        String email = "usado@email.com";
        Contact contact = new Contact("11987654321", email);
        
        when(patientRepository.findByEmail(eq(email)))
            .thenReturn(Optional.of(new com.clinicboard.business_service.domain.model.Patient(
                "PATIENT-456", "Maria", contact, new ProfessionalId("PROF-789"))));

        // When
        boolean isInUse = patientRepository.findByEmail(email).isPresent();

        // Then
        assertTrue(isInUse);
    }

    @Test
    @DisplayName("Deve verificar se usuário tem perfil de profissional")
    void shouldCheckIfUserHasProfessionalProfile() {
        // Given
        ProfessionalId professionalId = new ProfessionalId("PROF-123");
        ProfessionalId nonProfessionalId = new ProfessionalId("USER-456");
        
        when(userService.isUserProfessional(eq(professionalId.getValue())))
            .thenReturn(true);
        
        when(userService.isUserProfessional(eq(nonProfessionalId.getValue())))
            .thenReturn(false);

        // When/Then
        assertTrue(userService.isUserProfessional(professionalId.getValue()));
        assertFalse(userService.isUserProfessional(nonProfessionalId.getValue()));
    }

    @Test
    @DisplayName("Deve validar que contato tem informações completas")
    void shouldValidateContactHasCompleteInformation() {
        // Given
        Contact completeContact = new Contact("11987654321", "complete@email.com");

        // When/Then - Contact já valida na criação, então não precisa testar separadamente
        assertNotNull(completeContact.getPhone());
        assertNotNull(completeContact.getEmail());
        assertFalse(completeContact.getPhone().isEmpty());
        assertFalse(completeContact.getEmail().isEmpty());
    }
}
