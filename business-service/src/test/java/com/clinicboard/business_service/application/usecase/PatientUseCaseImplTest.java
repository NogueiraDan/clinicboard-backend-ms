package com.clinicboard.business_service.application.usecase;

import com.clinicboard.business_service.application.port.inbound.PatientUseCase;
import com.clinicboard.business_service.domain.port.PatientRepositoryPort;
import com.clinicboard.business_service.application.mapper.DomainPatientMapper;
import com.clinicboard.business_service.common.error.CustomGenericException;
import com.clinicboard.business_service.application.dto.PatientRequestDto;
import com.clinicboard.business_service.application.dto.PatientResponseDto;
import com.clinicboard.business_service.domain.service.PatientRegistrationService;
import com.clinicboard.business_service.domain.model.Patient;
import com.clinicboard.business_service.domain.model.valueobjects.Contact;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para PatientUseCaseImpl
 * Substitui os testes do PatientService legacy
 */
@ExtendWith(MockitoExtension.class)
class PatientUseCaseImplTest {

    @Mock
    private PatientRepositoryPort patientRepository;

    @Mock
    private PatientRegistrationService registrationService;

    @Mock
    private DomainPatientMapper patientMapper;

    private PatientUseCase patientUseCase;

    @BeforeEach
    void setUp() {
        patientUseCase = new PatientUseCaseImpl(
            patientRepository,
            registrationService,
            patientMapper
        );
    }

    @Test
    @DisplayName("Deve registrar paciente com sucesso")
    void shouldRegisterPatientSuccessfully() {
        // Given
        PatientRequestDto requestDto = createPatientRequest();
        Patient mockPatient = createMockPatient();
        PatientResponseDto expectedResponse = createPatientResponse("PATIENT-123");
        
        doNothing().when(registrationService).validatePatientRegistration(any(Contact.class), any(ProfessionalId.class));
        when(patientRepository.save(any(Patient.class))).thenReturn(mockPatient);
        when(patientMapper.toDto(any(Patient.class))).thenReturn(expectedResponse);
        
        // When
        PatientResponseDto result = patientUseCase.registerPatient(requestDto);

        // Then
        assertNotNull(result);
        assertEquals("PATIENT-123", result.getId());
        verify(registrationService).validatePatientRegistration(any(Contact.class), any(ProfessionalId.class));
        verify(patientRepository).save(any(Patient.class));
        verify(patientMapper).toDto(any(Patient.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando validação falha")
    void shouldThrowExceptionWhenValidationFails() {
        // Given
        PatientRequestDto requestDto = createPatientRequest();
        
        doThrow(new CustomGenericException("Email inválido"))
            .when(registrationService).validatePatientRegistration(any(Contact.class), any(ProfessionalId.class));

        // When/Then
        assertThrows(CustomGenericException.class, () ->
            patientUseCase.registerPatient(requestDto));
        
        verify(patientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar paciente por ID")
    void shouldFindPatientById() {
        // Given
        String patientId = "PATIENT-123";
        Patient mockPatient = createMockPatient();
        PatientResponseDto expectedResponse = createPatientResponse(patientId);
        
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(mockPatient));
        when(patientMapper.toDto(mockPatient)).thenReturn(expectedResponse);
        
        // When
        PatientResponseDto result = patientUseCase.findPatientById(patientId);

        // Then
        assertNotNull(result);
        assertEquals(patientId, result.getId());
        verify(patientRepository).findById(patientId);
        verify(patientMapper).toDto(mockPatient);
    }

    @Test
    @DisplayName("Deve buscar todos os pacientes")
    void shouldFindAllPatients() {
        // Given
        List<Patient> mockPatients = List.of(createMockPatient(), createMockPatient());
        List<PatientResponseDto> expectedResponse = List.of(
            createPatientResponse("PATIENT-1"), 
            createPatientResponse("PATIENT-2")
        );
        
        when(patientRepository.findAll()).thenReturn(mockPatients);
        when(patientMapper.toDto(any(Patient.class)))
            .thenReturn(expectedResponse.get(0))
            .thenReturn(expectedResponse.get(1));
        
        // When
        List<PatientResponseDto> result = patientUseCase.findAllPatients();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(patientRepository).findAll();
    }

    @Test
    @DisplayName("Deve atualizar paciente com sucesso")
    void shouldUpdatePatientSuccessfully() {
        // Given
        String patientId = "PATIENT-123";
        PatientRequestDto requestDto = createPatientRequest();
        Patient existingPatient = createMockPatient();
        PatientResponseDto expectedResponse = createPatientResponse(patientId);
        
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(existingPatient));
        doNothing().when(registrationService).validatePatientUpdate(eq(patientId), any(Contact.class));
        when(patientRepository.save(existingPatient)).thenReturn(existingPatient);
        when(patientMapper.toDto(existingPatient)).thenReturn(expectedResponse);
        
        // When
        PatientResponseDto result = patientUseCase.updatePatient(patientId, requestDto);

        // Then
        assertNotNull(result);
        assertEquals(patientId, result.getId());
        verify(registrationService).validatePatientUpdate(eq(patientId), any(Contact.class));
        verify(patientRepository).save(existingPatient);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar paciente inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentPatient() {
        // Given
        String patientId = "PATIENT-NONEXISTENT";
        PatientRequestDto requestDto = createPatientRequest();
        
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());
        
        // When/Then
        assertThrows(CustomGenericException.class, () ->
            patientUseCase.updatePatient(patientId, requestDto));
        
        verify(patientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve remover paciente com sucesso")
    void shouldRemovePatientSuccessfully() {
        // Given
        String patientId = "PATIENT-123";
        Patient mockPatient = createMockPatient();
        
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(mockPatient));
        doNothing().when(patientRepository).deleteById(patientId);
        
        // When
        patientUseCase.removePatient(patientId);

        // Then
        verify(patientRepository).findById(patientId);
        verify(patientRepository).deleteById(patientId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover paciente inexistente")
    void shouldThrowExceptionWhenRemovingNonExistentPatient() {
        // Given
        String patientId = "PATIENT-NONEXISTENT";
        
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());
        
        // When/Then
        assertThrows(CustomGenericException.class, () ->
            patientUseCase.removePatient(patientId));
        
        verify(patientRepository, never()).deleteById(patientId);
    }

    @Test
    @DisplayName("Deve buscar pacientes por filtro")
    void shouldFindPatientsByFilter() {
        // Given
        String param = "name";
        String value = "João";
        Patient mockPatient = mock(Patient.class);
        when(mockPatient.getName()).thenReturn("João Silva"); // Este stubbing é necessário para o filtro
        
        List<Patient> mockPatients = List.of(mockPatient);
        List<PatientResponseDto> expectedResponse = List.of(createPatientResponse("PATIENT-123"));
        
        when(patientRepository.findAll()).thenReturn(mockPatients);
        when(patientMapper.toDto(any(Patient.class))).thenReturn(expectedResponse.get(0));
        
        // When
        List<PatientResponseDto> result = patientUseCase.findPatientsByFilter(param, value);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(patientRepository).findAll();
    }

    @Test
    @DisplayName("Deve lançar exceção para parâmetro de filtro inválido")
    void shouldThrowExceptionForInvalidFilterParameter() {
        // Given
        String param = "invalid";
        String value = "test";
        
        // When/Then
        assertThrows(CustomGenericException.class, () ->
            patientUseCase.findPatientsByFilter(param, value));
    }

    @Test
    @DisplayName("Deve buscar pacientes por profissional")
    void shouldFindPatientsByProfessional() {
        // Given
        String professionalId = "PROF-456";
        List<Patient> mockPatients = List.of(createMockPatient());
        List<PatientResponseDto> expectedResponse = List.of(createPatientResponse("PATIENT-123"));
        
        when(patientRepository.findByProfessionalId(professionalId)).thenReturn(mockPatients);
        when(patientMapper.toDto(any(Patient.class))).thenReturn(expectedResponse.get(0));
        
        // When
        List<PatientResponseDto> result = patientUseCase.findPatientsByProfessional(professionalId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(patientRepository).findByProfessionalId(professionalId);
    }

    @Test
    @DisplayName("Deve validar contato inválido durante o registro")
    void shouldValidateInvalidContactDuringRegistration() {
        // Given
        PatientRequestDto requestDto = createPatientRequest();
        
        doThrow(new IllegalArgumentException("Contato inválido"))
            .when(registrationService).validatePatientRegistration(any(Contact.class), any(ProfessionalId.class));

        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
            patientUseCase.registerPatient(requestDto));
        
        verify(patientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve verificar se ProfessionalId é válido")
    void shouldValidateProfessionalId() {
        // Given
        PatientRequestDto requestDto = createPatientRequest();
        
        doThrow(new IllegalArgumentException("ID profissional inválido"))
            .when(registrationService).validatePatientRegistration(any(Contact.class), any(ProfessionalId.class));

        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
            patientUseCase.registerPatient(requestDto));
        
        verify(patientRepository, never()).save(any());
    }

    // Helper methods
    private PatientRequestDto createPatientRequest() {
        PatientRequestDto dto = new PatientRequestDto();
        dto.setName("João Silva");
        dto.setContact("11987654321");
        dto.setEmail("joao@test.com");
        dto.setProfessionalId("PROF-456");
        return dto;
    }

    private PatientResponseDto createPatientResponse(String id) {
        PatientResponseDto dto = new PatientResponseDto();
        dto.setId(id);
        dto.setName("João Silva");
        dto.setContact("11987654321");
        dto.setEmail("joao@test.com");
        return dto;
    }
    
    private Patient createMockPatient() {
        // Mock object - return simple mock without unnecessary stubbing
        return mock(Patient.class);
    }
}
