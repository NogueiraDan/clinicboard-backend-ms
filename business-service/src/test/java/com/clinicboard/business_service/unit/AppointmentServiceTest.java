package com.clinicboard.business_service.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.clinicboard.business_service.api.contract.MessagingInterface;
import com.clinicboard.business_service.application.dto.AppointmentRequestDto;
import com.clinicboard.business_service.application.dto.AppointmentResponseDto;
import com.clinicboard.business_service.application.mapper.AppointmentMapper;
import com.clinicboard.business_service.application.service.AppointmentService;
import com.clinicboard.business_service.common.error.BusinessException;
import com.clinicboard.business_service.common.error.CustomGenericException;
import com.clinicboard.business_service.domain.entity.Appointment;
import com.clinicboard.business_service.domain.repository.AppointmentRepository;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private MessagingInterface messagingInterface;

    private Appointment testAppointment;
    private AppointmentRequestDto testAppointmentRequestDto;
    private AppointmentResponseDto testAppointmentResponseDto;
    private String testAppointmentId;
    private String testProfessionalId;
    private String testPatientId;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testAppointmentId = "appointment-123";
        testProfessionalId = "professional-456";
        testPatientId = "patient-789";
        testDate = LocalDateTime.of(2025, 7, 15, 10, 0); // Data válida (dentro do horário comercial)

        testAppointment = new Appointment();
        testAppointment.setId(testAppointmentId);
        testAppointment.setProfessionalId(testProfessionalId);
        testAppointment.setPatientId(testPatientId);
        testAppointment.setDate(testDate);

        testAppointmentRequestDto = new AppointmentRequestDto();
        testAppointmentRequestDto.setProfessionalId(testProfessionalId);
        testAppointmentRequestDto.setPatientId(testPatientId);
        testAppointmentRequestDto.setDate(testDate);

        testAppointmentResponseDto = new AppointmentResponseDto();
        testAppointmentResponseDto.setId(testAppointmentId);
        testAppointmentResponseDto.setProfessionalId(testProfessionalId);
        testAppointmentResponseDto.setPatientId(testPatientId);
        testAppointmentResponseDto.setDate(testDate);
    }

    @Test
    @DisplayName("Should save appointment successfully")
    void save_WhenValidAppointment_ShouldReturnAppointmentResponseDto() {
        // Arrange
        LocalDateTime startRange = testDate.minusMinutes(30);
        LocalDateTime endRange = testDate.plusMinutes(30);

        when(appointmentRepository.existsByProfessionalIdAndDate(
                testProfessionalId, startRange, endRange)).thenReturn(false);
        when(appointmentRepository.existsByDate(startRange, endRange)).thenReturn(false);
        when(appointmentRepository.existsByPatientIdAndDate(testDate, testPatientId)).thenReturn(false);

        when(appointmentMapper.toEntity(testAppointmentRequestDto)).thenReturn(testAppointment);
        when(appointmentRepository.save(testAppointment)).thenReturn(testAppointment);
        when(appointmentMapper.toDto(testAppointment)).thenReturn(testAppointmentResponseDto);

        // Act
        AppointmentResponseDto result = appointmentService.save(testAppointmentRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(testAppointmentResponseDto, result);
        verify(appointmentRepository).save(testAppointment);
        verify(messagingInterface).publishNotification(testAppointmentRequestDto);
        verify(appointmentMapper).toEntity(testAppointmentRequestDto);
        verify(appointmentMapper).toDto(testAppointment);
    }

    @Test
    @DisplayName("Should throw BusinessException when appointment time is before business hours")
    void save_WhenAppointmentTimeBeforeBusinessHours_ShouldThrowBusinessException() {
        // Arrange
        testAppointmentRequestDto.setDate(LocalDateTime.of(2025, 7, 15, 7, 30)); // 07:30

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> appointmentService.save(testAppointmentRequestDto));

        assertEquals("O horário do agendamento está fora do horário comercial (08:00 às 19:00).",
                exception.getMessage());
        verify(appointmentRepository, never()).save(any());
        verify(messagingInterface, never()).publishNotification(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when appointment time is after business hours")
    void save_WhenAppointmentTimeAfterBusinessHours_ShouldThrowBusinessException() {
        // Arrange
        testAppointmentRequestDto.setDate(LocalDateTime.of(2025, 7, 15, 19, 30)); // 19:30

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> appointmentService.save(testAppointmentRequestDto));

        assertEquals("O horário do agendamento está fora do horário comercial (08:00 às 19:00).",
                exception.getMessage());
        verify(appointmentRepository, never()).save(any());
        verify(messagingInterface, never()).publishNotification(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when professional is busy")
    void save_WhenProfessionalIsBusy_ShouldThrowBusinessException() {
        // Arrange
        LocalDateTime startRange = testDate.minusMinutes(30);
        LocalDateTime endRange = testDate.plusMinutes(30);

        when(appointmentRepository.existsByProfessionalIdAndDate(
                testProfessionalId, startRange, endRange)).thenReturn(true); // Profissional ocupado

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> appointmentService.save(testAppointmentRequestDto));

        assertEquals(
                "Profissional já possui um agendamento nesta data/hora ou horário está fora do intervalo permitido.",
                exception.getMessage());
        verify(appointmentRepository, never()).save(any());
        verify(messagingInterface, never()).publishNotification(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when date is busy")
    void save_WhenDateIsBusy_ShouldThrowBusinessException() {
        // Arrange
        LocalDateTime startRange = testDate.minusMinutes(30);
        LocalDateTime endRange = testDate.plusMinutes(30);

        // Configure apenas os mocks necessários para este teste específico
        when(appointmentRepository.existsByProfessionalIdAndDate(
                testProfessionalId, startRange, endRange)).thenReturn(false);
        when(appointmentRepository.existsByDate(startRange, endRange)).thenReturn(true); // Data ocupada

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> appointmentService.save(testAppointmentRequestDto));

        assertEquals("Já existe um agendamento marcado nesta data/hora ou horário está fora do intervalo permitido.",
                exception.getMessage());
        verify(appointmentRepository, never()).save(any());
        verify(messagingInterface, never()).publishNotification(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when patient is busy")
    void save_WhenPatientIsBusy_ShouldThrowBusinessException() {
        // Arrange
        LocalDateTime startRange = testDate.minusMinutes(30);
        LocalDateTime endRange = testDate.plusMinutes(30);

        when(appointmentRepository.existsByProfessionalIdAndDate(
                testProfessionalId, startRange, endRange)).thenReturn(false);
        when(appointmentRepository.existsByDate(startRange, endRange)).thenReturn(false);
        when(appointmentRepository.existsByPatientIdAndDate(testDate, testPatientId)).thenReturn(true); // Paciente
                                                                                                        // ocupado

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> appointmentService.save(testAppointmentRequestDto));

        assertEquals("Paciente já possui um agendamento nesta data!", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
        verify(messagingInterface, never()).publishNotification(any());
    }

    @Test
    @DisplayName("Should find all appointments successfully")
    void findAll_WhenAppointmentsExist_ShouldReturnListOfAppointmentResponseDto() {
        // Arrange
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);
        when(appointmentMapper.toDto(testAppointment)).thenReturn(testAppointmentResponseDto);

        // Act
        List<AppointmentResponseDto> result = appointmentService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAppointmentResponseDto, result.get(0));
        verify(appointmentRepository).findAll();
        verify(appointmentMapper).toDto(testAppointment);
    }

    @Test
    @DisplayName("Should find appointment by id successfully")
    void findById_WhenAppointmentExists_ShouldReturnAppointmentResponseDto() {
        // Arrange
        when(appointmentRepository.findById(testAppointmentId)).thenReturn(Optional.of(testAppointment));
        when(appointmentMapper.toDto(testAppointment)).thenReturn(testAppointmentResponseDto);

        // Act
        AppointmentResponseDto result = appointmentService.findById(testAppointmentId);

        // Assert
        assertNotNull(result);
        assertEquals(testAppointmentResponseDto, result);
        verify(appointmentRepository).findById(testAppointmentId);
        verify(appointmentMapper).toDto(testAppointment);
    }

    @Test
    @DisplayName("Should throw CustomGenericException when appointment not found by id")
    void findById_WhenAppointmentNotExists_ShouldThrowCustomGenericException() {
        // Arrange
        when(appointmentRepository.findById(testAppointmentId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomGenericException exception = assertThrows(
                CustomGenericException.class,
                () -> appointmentService.findById(testAppointmentId));

        assertEquals("Consulta não encontrada", exception.getMessage());
        verify(appointmentRepository).findById(testAppointmentId);
        verify(appointmentMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should find appointments by professional id successfully")
    void findByProfessionalId_WhenAppointmentsExist_ShouldReturnFilteredAppointments() {
        // Arrange
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);
        when(appointmentMapper.toDto(testAppointment)).thenReturn(testAppointmentResponseDto);

        // Act
        List<AppointmentResponseDto> result = appointmentService.findByProfessionalId(testProfessionalId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAppointmentResponseDto, result.get(0));
        verify(appointmentRepository).findAll();
        verify(appointmentMapper).toDto(testAppointment);
    }

    @Test
    @DisplayName("Should find appointments by patient id successfully")
    void findByPatientId_WhenAppointmentsExist_ShouldReturnFilteredAppointments() {
        // Arrange
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);
        when(appointmentMapper.toDto(testAppointment)).thenReturn(testAppointmentResponseDto);

        // Act
        List<AppointmentResponseDto> result = appointmentService.findByPatientId(testPatientId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAppointmentResponseDto, result.get(0));
        verify(appointmentRepository).findAll();
        verify(appointmentMapper).toDto(testAppointment);
    }

    @Test
    @DisplayName("Should find appointments by status filter successfully")
    void findByFilter_WhenFilterByStatus_ShouldReturnFilteredAppointments() {
        // Arrange
        String param = "status";
        String value = "confirmado";
        String expectedValue = "%" + value + "%";
        List<Appointment> appointments = List.of(testAppointment);

        when(appointmentRepository.findByStatus(testProfessionalId, expectedValue)).thenReturn(appointments);
        when(appointmentMapper.toDto(testAppointment)).thenReturn(testAppointmentResponseDto);

        // Act
        List<AppointmentResponseDto> result = appointmentService.findByFilter(testProfessionalId, param, value);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAppointmentResponseDto, result.get(0));
        verify(appointmentRepository).findByStatus(testProfessionalId, expectedValue);
        verify(appointmentMapper).toDto(testAppointment);
    }

    @Test
    @DisplayName("Should throw BusinessException for invalid filter parameter")
    void findByFilter_WhenInvalidParam_ShouldThrowBusinessException() {
        // Arrange
        String param = "invalid";
        String value = "test";

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> appointmentService.findByFilter(testProfessionalId, param, value));

        assertEquals("Parâmetro de busca inválido.", exception.getMessage());
        verify(appointmentRepository, never()).findByStatus(any(), any());
    }

    @Test
    @DisplayName("Should update appointment successfully")
    void update_WhenAppointmentExists_ShouldReturnUpdatedAppointmentResponseDto() {
        // Arrange
        LocalDateTime startRange = testDate.minusMinutes(30);
        LocalDateTime endRange = testDate.plusMinutes(30);

        when(appointmentRepository.existsByProfessionalIdAndDate(
                testProfessionalId, startRange, endRange)).thenReturn(false);
        when(appointmentRepository.existsByDate(startRange, endRange)).thenReturn(false);
        when(appointmentRepository.existsByPatientIdAndDate(testDate, testPatientId)).thenReturn(false);

        when(appointmentRepository.findById(testAppointmentId)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(testAppointment)).thenReturn(testAppointment);
        when(appointmentMapper.toDto(testAppointment)).thenReturn(testAppointmentResponseDto);

        // Act
        AppointmentResponseDto result = appointmentService.update(testAppointmentId, testAppointmentRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(testAppointmentResponseDto, result);
        verify(appointmentRepository).findById(testAppointmentId);
        verify(appointmentMapper).updateEntity(testAppointmentRequestDto, testAppointment);
        verify(appointmentRepository).save(testAppointment);
        verify(appointmentMapper).toDto(testAppointment);
    }

    @Test
    @DisplayName("Should throw CustomGenericException when updating non-existing appointment")
    void update_WhenAppointmentNotExists_ShouldThrowCustomGenericException() {
        // Arrange
        when(appointmentRepository.findById(testAppointmentId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomGenericException exception = assertThrows(
                CustomGenericException.class,
                () -> appointmentService.update(testAppointmentId, testAppointmentRequestDto));

        assertEquals("Consulta não encontrada para atualização.", exception.getMessage());
        verify(appointmentRepository).findById(testAppointmentId);
        verify(appointmentMapper, never()).updateEntity(any(), any());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete appointment successfully")
    void delete_WhenAppointmentExists_ShouldDeleteAppointment() {
        // Arrange
        when(appointmentRepository.existsById(testAppointmentId)).thenReturn(true);

        // Act
        appointmentService.delete(testAppointmentId);

        // Assert
        verify(appointmentRepository).existsById(testAppointmentId);
        verify(appointmentRepository).deleteById(testAppointmentId);
    }

    @Test
    @DisplayName("Should throw CustomGenericException when deleting non-existing appointment")
    void delete_WhenAppointmentNotExists_ShouldThrowCustomGenericException() {
        // Arrange
        when(appointmentRepository.existsById(testAppointmentId)).thenReturn(false);

        // Act & Assert
        CustomGenericException exception = assertThrows(
                CustomGenericException.class,
                () -> appointmentService.delete(testAppointmentId));

        assertEquals("Consulta não encontrada para exclusão.", exception.getMessage());
        verify(appointmentRepository).existsById(testAppointmentId);
        verify(appointmentRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should return AppointmentRepository when getAppointmentRepository is called")
    void getAppointmentRepository_ShouldReturnAppointmentRepository() {
        // Act
        AppointmentRepository result = appointmentService.getAppointmentRepository();

        // Assert
        assertNotNull(result);
        assertEquals(appointmentRepository, result);
    }

    @Test
    @DisplayName("Should find appointments by date successfully")
    void findByDate_WhenAppointmentsExist_ShouldReturnListOfAppointmentResponseDto() {
        // Arrange
        String date = "2025-07-15";
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentRepository.findByDate(testProfessionalId, date)).thenReturn(appointments);
        when(appointmentMapper.toDto(testAppointment)).thenReturn(testAppointmentResponseDto);

        // Act
        List<AppointmentResponseDto> result = appointmentService.findByDate(testProfessionalId, date);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAppointmentResponseDto, result.get(0));
        verify(appointmentRepository).findByDate(testProfessionalId, date);
        verify(appointmentMapper).toDto(testAppointment);
    }

    @Test
    @DisplayName("Should return available times successfully")
    void getAvailableTimes_WhenCalled_ShouldReturnAvailableTimeSlots() {
        // Arrange
        String date = "2025-07-15";
        List<Appointment> appointments = List.of(testAppointment); // Um agendamento às 10:00
        when(appointmentRepository.findByDate(testProfessionalId, date)).thenReturn(appointments);

        // Act
        List<LocalTime> result = appointmentService.getAvailableTimes(testProfessionalId, date);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertFalse(result.contains(LocalTime.of(10, 0))); // Horário ocupado não deve estar na lista
        assertTrue(result.contains(LocalTime.of(8, 0))); // Primeiro horário disponível
        assertTrue(result.contains(LocalTime.of(8, 30))); // Segundo horário disponível
        verify(appointmentRepository).findByDate(testProfessionalId, date);
    }
    
}
