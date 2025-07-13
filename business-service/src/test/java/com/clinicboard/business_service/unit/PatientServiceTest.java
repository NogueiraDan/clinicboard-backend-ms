package com.clinicboard.business_service.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import com.clinicboard.business_service.api.dto.UserResponseDto;
import com.clinicboard.business_service.api.dto.UserRole;
import com.clinicboard.business_service.api.events.UserFeignClient;
import com.clinicboard.business_service.application.dto.PatientRequestDto;
import com.clinicboard.business_service.application.dto.PatientResponseDto;
import com.clinicboard.business_service.application.mapper.PatientMapper;
import com.clinicboard.business_service.application.service.PatientService;
import com.clinicboard.business_service.common.error.BusinessException;
import com.clinicboard.business_service.common.error.CustomGenericException;
import com.clinicboard.business_service.domain.entity.Patient;
import com.clinicboard.business_service.domain.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
    @InjectMocks
    private PatientService patientService;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserFeignClient userFeignClient;

    private Patient testPatient;
    private PatientRequestDto testPatientRequestDto;
    private PatientResponseDto testPatientResponseDto;
    private UserResponseDto testUserResponseDto;
    private String testPatientId;
    private String testProfessionalId;
    private String testEmail;

    @BeforeEach
    void setUp() {
        testPatientId = "patient-123";
        testProfessionalId = "professional-456";
        testEmail = "patient@example.com";

        testPatient = new Patient();
        testPatient.setId(testPatientId);
        testPatient.setEmail(testEmail);
        testPatient.setName("Test Patient");
        testPatient.setProfessionalId(testProfessionalId);

        testPatientRequestDto = new PatientRequestDto();
        testPatientRequestDto.setEmail(testEmail);
        testPatientRequestDto.setName("Test Patient");
        testPatientRequestDto.setProfessionalId(testProfessionalId);

        testPatientResponseDto = new PatientResponseDto();
        testPatientResponseDto.setId(testPatientId);
        testPatientResponseDto.setEmail(testEmail);
        testPatientResponseDto.setName("Test Patient");

        testUserResponseDto = new UserResponseDto();
        testUserResponseDto.setId(testProfessionalId);
        testUserResponseDto.setRole(UserRole.PROFESSIONAL);
    }

    @Test
    @DisplayName("Should find all patients successfully")
    void findAll_WhenPatientsExist_ShouldReturnListOfPatientResponseDto() {
        // Arrange
        List<Patient> patients = List.of(testPatient);
        when(patientRepository.findAll()).thenReturn(patients);
        when(patientMapper.toDto(testPatient)).thenReturn(testPatientResponseDto);

        // Act
        List<PatientResponseDto> result = patientService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPatientResponseDto, result.get(0));
        verify(patientRepository).findAll();
        verify(patientMapper).toDto(testPatient);
    }

    @Test
    @DisplayName("Should find patient by id successfully")
    void findById_WhenPatientExists_ShouldReturnPatientResponseDto() {
        // Arrange
        when(patientRepository.findById(testPatientId)).thenReturn(Optional.of(testPatient));
        when(patientMapper.toDto(testPatient)).thenReturn(testPatientResponseDto);

        // Act
        PatientResponseDto result = patientService.findById(testPatientId);

        // Assert
        assertNotNull(result);
        assertEquals(testPatientResponseDto, result);
        verify(patientRepository).findById(testPatientId);
        verify(patientMapper).toDto(testPatient);
    }

    @Test
    @DisplayName("Should throw CustomGenericException when patient not found by id")
    void findById_WhenPatientNotExists_ShouldThrowCustomGenericException() {
        // Arrange
        when(patientRepository.findById(testPatientId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomGenericException exception = assertThrows(
                CustomGenericException.class,
                () -> patientService.findById(testPatientId));

        assertEquals("Paciente não encontrado", exception.getMessage());
        verify(patientRepository).findById(testPatientId);
        verify(patientMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should find patients by name filter successfully")
    void findByFilter_WhenFilterByName_ShouldReturnFilteredPatients() {
        // Arrange
        String param = "nome";
        String value = "Test";
        String expectedValue = "%" + value + "%";
        List<Patient> patients = List.of(testPatient);

        when(patientRepository.findByName(expectedValue)).thenReturn(patients);
        when(patientMapper.toDto(testPatient)).thenReturn(testPatientResponseDto);

        // Act
        List<PatientResponseDto> result = patientService.findByFilter(param, value);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPatientResponseDto, result.get(0));
        verify(patientRepository).findByName(expectedValue);
        verify(patientMapper).toDto(testPatient);
    }

    @Test
    @DisplayName("Should find patients by contact filter successfully")
    void findByFilter_WhenFilterByContact_ShouldReturnFilteredPatients() {
        // Arrange
        String param = "contato";
        String value = "123456789";
        String expectedValue = "%" + value + "%";
        List<Patient> patients = List.of(testPatient);

        when(patientRepository.findByContact(expectedValue)).thenReturn(patients);
        when(patientMapper.toDto(testPatient)).thenReturn(testPatientResponseDto);

        // Act
        List<PatientResponseDto> result = patientService.findByFilter(param, value);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPatientResponseDto, result.get(0));
        verify(patientRepository).findByContact(expectedValue);
        verify(patientMapper).toDto(testPatient);
    }

    @Test
    @DisplayName("Should throw CustomGenericException for invalid filter parameter")
    void findByFilter_WhenInvalidParam_ShouldThrowCustomGenericException() {
        // Arrange
        String param = "invalid";
        String value = "test";

        // Act & Assert
        CustomGenericException exception = assertThrows(
                CustomGenericException.class,
                () -> patientService.findByFilter(param, value));

        assertEquals("Parâmetro de busca inválido. Use 'nome' ou 'contato'.", exception.getMessage());
        verify(patientRepository, never()).findByName(any());
        verify(patientRepository, never()).findByContact(any());
    }

    @Test
    @DisplayName("Should update patient successfully")
    void update_WhenPatientExists_ShouldReturnUpdatedPatientResponseDto() {
        // Arrange
        when(patientRepository.findById(testPatientId)).thenReturn(Optional.of(testPatient));
        when(patientRepository.save(testPatient)).thenReturn(testPatient);
        when(patientMapper.toDto(testPatient)).thenReturn(testPatientResponseDto);

        // Act
        PatientResponseDto result = patientService.update(testPatientId, testPatientRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(testPatientResponseDto, result);
        verify(patientRepository).findById(testPatientId);
        verify(patientMapper).updateEntity(testPatientRequestDto, testPatient);
        verify(patientRepository).save(testPatient);
        verify(patientMapper).toDto(testPatient);
    }

    @Test
    @DisplayName("Should throw CustomGenericException when updating non-existing patient")
    void update_WhenPatientNotExists_ShouldThrowCustomGenericException() {
        // Arrange
        when(patientRepository.findById(testPatientId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomGenericException exception = assertThrows(
                CustomGenericException.class,
                () -> patientService.update(testPatientId, testPatientRequestDto));

        assertEquals("Paciente não encontrado", exception.getMessage());
        verify(patientRepository).findById(testPatientId);
        verify(patientMapper, never()).updateEntity(any(), any());
        verify(patientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete patient successfully")
    void delete_WhenPatientExists_ShouldDeletePatient() {
        // Arrange
        when(patientRepository.existsById(testPatientId)).thenReturn(true);

        // Act
        patientService.delete(testPatientId);

        // Assert
        verify(patientRepository).existsById(testPatientId);
        verify(patientRepository).deleteById(testPatientId);
    }

    @Test
    @DisplayName("Should throw CustomGenericException when deleting non-existing patient")
    void delete_WhenPatientNotExists_ShouldThrowCustomGenericException() {
        // Arrange
        when(patientRepository.existsById(testPatientId)).thenReturn(false);

        // Act & Assert
        CustomGenericException exception = assertThrows(
                CustomGenericException.class,
                () -> patientService.delete(testPatientId));

        assertEquals("Paciente não encontrado", exception.getMessage());
        verify(patientRepository).existsById(testPatientId);
        verify(patientRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should return PatientRepository when getPatientRepository is called")
    void getPatientRepository_ShouldReturnPatientRepository() {
        // Act
        PatientRepository result = patientService.getPatientRepository();

        // Assert
        assertNotNull(result);
        assertEquals(patientRepository, result);
    }

    @Test
    @DisplayName("Should find patients by professional id successfully")
    void findByProfessionalId_WhenPatientsExist_ShouldReturnListOfPatientResponseDto() {
        // Arrange
        List<Patient> patients = List.of(testPatient);
        when(patientRepository.findByProfessionalId(testProfessionalId)).thenReturn(patients);
        when(patientMapper.toDto(testPatient)).thenReturn(testPatientResponseDto);

        // Act
        List<PatientResponseDto> result = patientService.findByProfessionalId(testProfessionalId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPatientResponseDto, result.get(0));
        verify(patientRepository).findByProfessionalId(testProfessionalId);
        verify(patientMapper).toDto(testPatient);
    }

    @Test
    @DisplayName("Should save patient successfully")
    void save_WhenValidData_ShouldReturnPatientResponseDto() {
        // Arrange
        when(patientRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(userFeignClient.findById(testProfessionalId)).thenReturn(ResponseEntity.ok(testUserResponseDto));
        when(patientMapper.toEntity(testPatientRequestDto)).thenReturn(testPatient);
        when(patientRepository.save(testPatient)).thenReturn(testPatient);
        when(patientMapper.toDto(testPatient)).thenReturn(testPatientResponseDto);

        // Act
        PatientResponseDto result = patientService.save(testPatientRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(testPatientResponseDto, result);
        verify(patientRepository).findByEmail(testEmail);
        verify(userFeignClient).findById(testProfessionalId);
        verify(patientMapper).toEntity(testPatientRequestDto);
        verify(patientRepository).save(testPatient);
        verify(patientMapper).toDto(testPatient);
    }

    @Test
    @DisplayName("Should throw CustomGenericException when email already exists")
    void save_WhenEmailAlreadyExists_ShouldThrowCustomGenericException() {
        // Arrange
        when(patientRepository.findByEmail(testEmail)).thenReturn(Optional.of(testPatient));
        when(userFeignClient.findById(testProfessionalId)).thenReturn(ResponseEntity.ok(testUserResponseDto));

        // Act & Assert
        CustomGenericException exception = assertThrows(
                CustomGenericException.class,
                () -> patientService.save(testPatientRequestDto));

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(patientRepository).findByEmail(testEmail);
        verify(userFeignClient).findById(testProfessionalId);
    }

    @Test
    @DisplayName("Should throw BusinessException when user role is not PROFESSIONAL")
    void save_WhenUserRoleIsNotProfessional_ShouldThrowBusinessException() {
        // Arrange
        testUserResponseDto.setRole(UserRole.ADMIN);
        when(patientRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(userFeignClient.findById(testProfessionalId)).thenReturn(ResponseEntity.ok(testUserResponseDto));

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> patientService.save(testPatientRequestDto));

        assertEquals("Perfil não permitido para essa operação", exception.getMessage());
        verify(patientRepository).findByEmail(testEmail);
        verify(userFeignClient).findById(testProfessionalId);
        verify(patientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should execute fallback when user service is unavailable")
    void saveFallback_WhenCalled_ShouldThrowBusinessException() {
        // Arrange
        Throwable throwable = new RuntimeException("Service unavailable");

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> patientService.saveFallback(testPatientRequestDto, throwable));

        assertEquals("O serviço de usuários está temporariamente indisponível. Tente novamente mais tarde.",
                exception.getMessage());
    }
}
