package com.clinicboard.business_service.application.usecase;

import com.clinicboard.business_service.application.port.inbound.AppointmentUseCase;
import com.clinicboard.business_service.domain.port.AppointmentRepositoryPort;
import com.clinicboard.business_service.application.port.outbound.EventPublisher;
import com.clinicboard.business_service.application.mapper.DomainAppointmentMapper;
import com.clinicboard.business_service.common.error.CustomGenericException;
import com.clinicboard.business_service.application.dto.AppointmentRequestDto;
import com.clinicboard.business_service.application.dto.AppointmentResponseDto;
import com.clinicboard.business_service.domain.model.Appointment;
import com.clinicboard.business_service.domain.model.valueobjects.AppointmentTime;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;
import com.clinicboard.business_service.domain.service.AppointmentSchedulingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AppointmentUseCaseImpl
 * Substitui os testes do AppointmentService legacy
 */
@ExtendWith(MockitoExtension.class)
class AppointmentUseCaseImplTest {

    @Mock
    private AppointmentRepositoryPort appointmentRepository;

    @Mock
    private AppointmentSchedulingService schedulingService;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private DomainAppointmentMapper appointmentMapper;

    private AppointmentUseCase appointmentUseCase;

    @BeforeEach
    void setUp() {
        appointmentUseCase = new AppointmentUseCaseImpl(
            appointmentRepository,
            schedulingService,
            eventPublisher,
            appointmentMapper
        );
    }

    @Test
    @DisplayName("Deve agendar com sucesso")
    void shouldScheduleAppointmentSuccessfully() {
        // Given
        AppointmentRequestDto requestDto = createAppointmentRequest();
        Appointment mockAppointment = createMockAppointmentWithEvents(true);
        AppointmentResponseDto expectedResponse = createAppointmentResponse("APPT-123");

        doNothing().when(schedulingService).validateAppointmentScheduling(any(AppointmentTime.class), any(ProfessionalId.class), anyString());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);
        when(appointmentMapper.toDto(any(Appointment.class))).thenReturn(expectedResponse);

        // When
        AppointmentResponseDto result = appointmentUseCase.scheduleAppointment(requestDto);

        // Then
        assertNotNull(result);
        assertEquals("APPT-123", result.getId());
        verify(schedulingService).validateAppointmentScheduling(any(AppointmentTime.class), any(ProfessionalId.class), anyString());
        verify(appointmentRepository).save(any(Appointment.class));
        verify(eventPublisher).publishEvent(any(com.clinicboard.business_service.domain.event.DomainEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando validação falha")
    void shouldThrowExceptionWhenValidationFails() {
        // Given
        AppointmentRequestDto requestDto = createAppointmentRequest();

        doThrow(new CustomGenericException("Horário já ocupado"))
            .when(schedulingService).validateAppointmentScheduling(any(AppointmentTime.class), any(ProfessionalId.class), anyString());

        // When/Then
        assertThrows(CustomGenericException.class, () ->
            appointmentUseCase.scheduleAppointment(requestDto));

        verify(appointmentRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any(com.clinicboard.business_service.domain.event.DomainEvent.class));
    }

    @Test
    @DisplayName("Deve reagendar agendamento com sucesso")
    void shouldRescheduleAppointmentSuccessfully() {
        // Given
        String appointmentId = "APPT-123";
        AppointmentRequestDto requestDto = createAppointmentRequest();
        Appointment existingAppointment = createMockAppointment();
        AppointmentResponseDto expectedResponse = createAppointmentResponse(appointmentId);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        doNothing().when(schedulingService).validateAppointmentScheduling(any(AppointmentTime.class), any(ProfessionalId.class), anyString());
        when(appointmentRepository.save(existingAppointment)).thenReturn(existingAppointment);
        when(appointmentMapper.toDto(existingAppointment)).thenReturn(expectedResponse);

        // When
        AppointmentResponseDto result = appointmentUseCase.rescheduleAppointment(appointmentId, requestDto);

        // Then
        assertNotNull(result);
        assertEquals(appointmentId, result.getId());
        verify(appointmentRepository).findById(appointmentId);
        verify(schedulingService).validateAppointmentScheduling(any(AppointmentTime.class), any(ProfessionalId.class), anyString());
        verify(appointmentRepository).save(existingAppointment);
    }

    @Test
    @DisplayName("Deve cancelar agendamento com sucesso")
    void shouldCancelAppointmentSuccessfully() {
        // Given
        String appointmentId = "APPT-123";
        String reason = "Paciente cancelou";
        Appointment existingAppointment = createMockAppointment();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(existingAppointment)).thenReturn(existingAppointment);

        // When
        appointmentUseCase.cancelAppointment(appointmentId, reason);

        // Then
        verify(appointmentRepository).findById(appointmentId);
        verify(appointmentRepository).save(existingAppointment);
        verify(existingAppointment).cancel(reason);
    }

    @Test
    @DisplayName("Deve buscar agendamento por ID")
    void shouldFindAppointmentById() {
        // Given
        String appointmentId = "APPT-123";
        Appointment mockAppointment = createMockAppointment();
        AppointmentResponseDto expectedResponse = createAppointmentResponse(appointmentId);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));
        when(appointmentMapper.toDto(mockAppointment)).thenReturn(expectedResponse);

        // When
        AppointmentResponseDto result = appointmentUseCase.findAppointmentById(appointmentId);

        // Then
        assertNotNull(result);
        assertEquals(appointmentId, result.getId());
        verify(appointmentRepository).findById(appointmentId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento não encontrado")
    void shouldThrowExceptionWhenAppointmentNotFound() {
        // Given
        String appointmentId = "INVALID-ID";
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(CustomGenericException.class, () ->
            appointmentUseCase.findAppointmentById(appointmentId));

        verify(appointmentRepository).findById(appointmentId);
    }

    @Test
    @DisplayName("Deve buscar todos os agendamentos")
    void shouldFindAllAppointments() {
        // Given
        List<Appointment> mockAppointments = Arrays.asList(createMockAppointment(), createMockAppointment());
        List<AppointmentResponseDto> expectedResponses = Arrays.asList(
            createAppointmentResponse("APPT-1"),
            createAppointmentResponse("APPT-2")
        );

        when(appointmentRepository.findAll()).thenReturn(mockAppointments);
        when(appointmentMapper.toDto(any(Appointment.class)))
            .thenReturn(expectedResponses.get(0))
            .thenReturn(expectedResponses.get(1));

        // When
        List<AppointmentResponseDto> result = appointmentUseCase.findAllAppointments();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(appointmentRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar agendamentos por profissional")
    void shouldFindAppointmentsByProfessional() {
        // Given
        String professionalId = "PROF-456";
        Appointment mockAppointment = createMockAppointment();
        List<Appointment> mockAppointments = Arrays.asList(mockAppointment);
        List<AppointmentResponseDto> expectedResponses = Arrays.asList(createAppointmentResponse("APPT-1"));

        when(appointmentRepository.findAll()).thenReturn(mockAppointments);
        when(mockAppointment.isManagedBy(any(ProfessionalId.class))).thenReturn(true);
        when(appointmentMapper.toDto(any(Appointment.class))).thenReturn(expectedResponses.get(0));

        // When
        List<AppointmentResponseDto> result = appointmentUseCase.findAppointmentsByProfessional(professionalId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar agendamentos por paciente")
    void shouldFindAppointmentsByPatient() {
        // Given
        String patientId = "PATIENT-123";
        List<Appointment> mockAppointments = Arrays.asList(createMockAppointment());
        List<AppointmentResponseDto> expectedResponses = Arrays.asList(createAppointmentResponse("APPT-1"));

        when(appointmentRepository.findByPatientId(eq(patientId))).thenReturn(mockAppointments);
        when(appointmentMapper.toDto(any(Appointment.class))).thenReturn(expectedResponses.get(0));

        // When
        List<AppointmentResponseDto> result = appointmentUseCase.findAppointmentsByPatient(patientId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository).findByPatientId(eq(patientId));
    }

    @Test
    @DisplayName("Deve buscar agendamentos por data")
    void shouldFindAppointmentsByDate() {
        // Given
        String professionalId = "PROF-456";
        String date = "2024-12-01";
        List<Appointment> mockAppointments = Arrays.asList(createMockAppointment());
        List<AppointmentResponseDto> expectedResponses = Arrays.asList(createAppointmentResponse("APPT-1"));

        when(appointmentRepository.findByProfessionalId(eq(professionalId)))
            .thenReturn(mockAppointments);
        when(appointmentMapper.toDto(any(Appointment.class))).thenReturn(expectedResponses.get(0));

        // When
        List<AppointmentResponseDto> result = appointmentUseCase.findAppointmentsByDate(professionalId, date);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository).findByProfessionalId(eq(professionalId));
    }

    @Test
    @DisplayName("Deve buscar horários disponíveis")
    void shouldGetAvailableTimes() {
        // Given
        String professionalId = "PROF-456";
        String date = "2024-12-01";

        when(appointmentRepository.findByProfessionalId(eq(professionalId)))
            .thenReturn(Arrays.asList());

        // When
        List<LocalTime> result = appointmentUseCase.getAvailableTimes(professionalId, date);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty()); // Deve ter horários disponíveis
        verify(appointmentRepository).findByProfessionalId(eq(professionalId));
    }

    // Helper methods
    private AppointmentRequestDto createAppointmentRequest() {
        AppointmentRequestDto dto = new AppointmentRequestDto();
        dto.setDate(LocalDateTime.now().plusDays(1));
        dto.setPatientId("PATIENT-123");
        dto.setProfessionalId("PROF-456");
        dto.setObservation("Consulta médica");
        return dto;
    }

    private AppointmentResponseDto createAppointmentResponse(String id) {
        AppointmentResponseDto dto = new AppointmentResponseDto();
        dto.setId(id);
        dto.setDate(LocalDateTime.now().plusDays(1));
        dto.setPatientId("PATIENT-123");
        dto.setProfessionalId("PROF-456");
        dto.setObservation("Consulta médica");
        return dto;
    }

    private Appointment createMockAppointment() {
        return createMockAppointmentWithEvents(false);
    }
    
    private Appointment createMockAppointmentWithEvents(boolean withEvents) {
        Appointment mockAppointment = mock(Appointment.class);
        
        if (withEvents) {
            // Simula eventos de domínio no agregado
            com.clinicboard.business_service.domain.event.AppointmentScheduledEvent event = 
                new com.clinicboard.business_service.domain.event.AppointmentScheduledEvent(
                    "APPT-123",
                    "PATIENT-123", 
                    "PROF-456",
                    LocalDateTime.now().plusDays(1),
                    "MARCACAO"
                );
            
            when(mockAppointment.getDomainEvents()).thenReturn(java.util.List.of(event));
        }
        
        return mockAppointment;
    }
}
