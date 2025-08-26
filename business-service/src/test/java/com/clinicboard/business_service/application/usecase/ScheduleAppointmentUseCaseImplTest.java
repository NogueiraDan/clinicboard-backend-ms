package com.clinicboard.business_service.application.usecase;

import com.clinicboard.business_service.application.port.in.ScheduleAppointmentCommand;
import com.clinicboard.business_service.application.port.in.ScheduleAppointmentCommand.ScheduleAppointmentRequest;
import com.clinicboard.business_service.application.port.in.ScheduleAppointmentCommand.ScheduleAppointmentResponse;
import com.clinicboard.business_service.application.port.out.AppointmentRepository;
import com.clinicboard.business_service.application.port.out.EventPublisher;
import com.clinicboard.business_service.application.port.out.PatientRepository;
import com.clinicboard.business_service.domain.exception.DomainException;
import com.clinicboard.business_service.domain.exception.PatientBusinessRuleException;
import com.clinicboard.business_service.domain.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ScheduleAppointmentUseCaseImpl
 * 
 * Foco em testar a lógica de orquestração e as regras de negócio
 * aplicadas durante o agendamento de consultas.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ScheduleAppointment UseCase Tests")
class ScheduleAppointmentUseCaseImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    
    @Mock
    private PatientRepository patientRepository;
    
    @Mock
    private EventPublisher eventPublisher;
    
    private ScheduleAppointmentCommand scheduleAppointmentUseCase;

    @BeforeEach
    void setUp() {
        scheduleAppointmentUseCase = new ScheduleAppointmentUseCaseImpl(
            appointmentRepository, 
            patientRepository, 
            eventPublisher
        );
    }

    @Nested
    @DisplayName("Agendamento Bem-Sucedido")
    class SuccessfulScheduling {

        @Test
        @DisplayName("Deve agendar consulta com dados válidos")
        void shouldScheduleAppointmentWithValidData() {
            // Given
            ScheduleAppointmentRequest request = createValidScheduleRequest();
            Patient activePatient = createActivePatient();
            Appointment appointment = createAppointment();
            
            when(patientRepository.findById(any(PatientId.class))).thenReturn(Optional.of(activePatient));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
            
            // When
            ScheduleAppointmentResponse response = scheduleAppointmentUseCase.scheduleAppointment(request);
            
            // Then
            assertNotNull(response);
            assertEquals(appointment.getId().value(), response.appointmentId().value());
            assertEquals(request.patientId(), response.patientId());
            assertEquals(request.professionalId(), response.professionalId());
            
            verify(appointmentRepository).save(any(Appointment.class));
            verify(eventPublisher).publishAppointmentScheduled(any());
        }

        @Test
        @DisplayName("Deve validar horário comercial antes de agendar")
        void shouldValidateBusinessHoursBeforeScheduling() {
            // Given
            ScheduleAppointmentRequest request = createValidScheduleRequest();
            Patient activePatient = createActivePatient();
            
            when(patientRepository.findById(any(PatientId.class))).thenReturn(Optional.of(activePatient));
            
            // When & Then
            assertDoesNotThrow(() -> scheduleAppointmentUseCase.scheduleAppointment(request));
        }
    }

    @Nested
    @DisplayName("Validações de Regras de Negócio")
    class BusinessRuleValidations {

        @Test
        @DisplayName("Deve falhar quando paciente não existir")
        void shouldFailWhenPatientDoesNotExist() {
            // Given
            ScheduleAppointmentRequest request = createValidScheduleRequest();
            
            when(patientRepository.findById(any(PatientId.class))).thenReturn(Optional.empty());
            
            // When & Then
            assertThrows(PatientBusinessRuleException.class, 
                () -> scheduleAppointmentUseCase.schedule(request));
            
            verify(appointmentRepository, never()).save(any());
            verify(eventPublisher, never()).publishAppointmentScheduled(any());
        }

        @Test
        @DisplayName("Deve falhar quando paciente estiver inativo")
        void shouldFailWhenPatientIsInactive() {
            // Given
            ScheduleAppointmentRequest request = createValidScheduleRequest();
            Patient inactivePatient = createInactivePatient();
            
            when(patientRepository.findById(any(PatientId.class))).thenReturn(Optional.of(inactivePatient));
            
            // When & Then
            assertThrows(PatientBusinessRuleException.class, 
                () -> scheduleAppointmentUseCase.schedule(request));
            
            verify(appointmentRepository, never()).save(any());
            verify(eventPublisher, never()).publishAppointmentScheduled(any());
        }

        @Test
        @DisplayName("Deve falhar quando tentar agendar no passado")
        void shouldFailWhenSchedulingInPast() {
            // Given
            LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
            ScheduleAppointmentRequest request = new ScheduleAppointmentRequest(
                PatientId.generate().value(),
                ProfessionalId.generate().value(),
                pastTime,
                AppointmentType.CONSULTATION,
                "Consulta de rotina"
            );
            Patient activePatient = createActivePatient();
            
            when(patientRepository.findById(any(PatientId.class))).thenReturn(Optional.of(activePatient));
            
            // When & Then
            assertThrows(AppointmentBusinessRuleException.class, 
                () -> scheduleAppointmentUseCase.schedule(request));
        }

        @Test
        @DisplayName("Deve falhar quando tentar agendar fora do horário comercial")
        void shouldFailWhenSchedulingOutsideBusinessHours() {
            // Given
            LocalDateTime outsideBusinessHours = LocalDateTime.now().plusDays(1).withHour(22).withMinute(0);
            ScheduleAppointmentRequest request = new ScheduleAppointmentRequest(
                PatientId.generate().value(),
                ProfessionalId.generate().value(),
                outsideBusinessHours,
                AppointmentType.CONSULTATION,
                "Consulta fora do horário"
            );
            Patient activePatient = createActivePatient();
            
            when(patientRepository.findById(any(PatientId.class))).thenReturn(Optional.of(activePatient));
            
            // When & Then
            assertThrows(AppointmentBusinessRuleException.class, 
                () -> scheduleAppointmentUseCase.schedule(request));
        }
    }

    @Nested
    @DisplayName("Publicação de Eventos")
    class EventPublishing {

        @Test
        @DisplayName("Deve publicar evento de agendamento criado")
        void shouldPublishAppointmentScheduledEvent() {
            // Given
            ScheduleAppointmentRequest request = createValidScheduleRequest();
            Patient activePatient = createActivePatient();
            Appointment appointment = createAppointment();
            
            when(patientRepository.findById(any(PatientId.class))).thenReturn(Optional.of(activePatient));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
            
            // When
            scheduleAppointmentUseCase.schedule(request);
            
            // Then
            verify(eventPublisher).publishAppointmentScheduled(any());
        }

        @Test
        @DisplayName("Não deve publicar evento quando agendamento falhar")
        void shouldNotPublishEventWhenSchedulingFails() {
            // Given
            ScheduleAppointmentRequest request = createValidScheduleRequest();
            
            when(patientRepository.findById(any(PatientId.class))).thenReturn(Optional.empty());
            
            // When & Then
            assertThrows(PatientBusinessRuleException.class, 
                () -> scheduleAppointmentUseCase.schedule(request));
            
            verify(eventPublisher, never()).publishAppointmentScheduled(any());
        }
    }

    // Helper methods para criar objetos de teste
    private ScheduleAppointmentRequest createValidScheduleRequest() {
        return new ScheduleAppointmentRequest(
            PatientId.generate().value(),
            ProfessionalId.generate().value(),
            LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
            AppointmentType.CONSULTATION,
            "Consulta de rotina"
        );
    }

    private Patient createActivePatient() {
        PatientName name = new PatientName("João Silva");
        Email email = new Email("joao@email.com");
        ContactDetails contact = new ContactDetails("11999999999");
        ProfessionalId professionalId = ProfessionalId.generate();
        return new Patient(name, email, contact, professionalId);
    }

    private Patient createInactivePatient() {
        Patient patient = createActivePatient();
        return patient.deactivate("Teste de inativação");
    }

    private Appointment createAppointment() {
        PatientId patientId = PatientId.generate();
        ProfessionalId professionalId = ProfessionalId.generate();
        AppointmentTime appointmentTime = AppointmentTime.of(
            LocalDateTime.now().plusDays(1).withHour(10).withMinute(0)
        );
        AppointmentType type = AppointmentType.CONSULTATION;
        
        return new Appointment(patientId, professionalId, appointmentTime, type);
    }
}
