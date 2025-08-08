package com.clinicboard.business_service.domain.service;

import com.clinicboard.business_service.common.error.BusinessException;
import com.clinicboard.business_service.domain.model.valueobjects.AppointmentTime;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;
import com.clinicboard.business_service.domain.port.AppointmentRepositoryPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para AppointmentSchedulingService
 */
@ExtendWith(MockitoExtension.class)
class AppointmentSchedulingServiceTest {

    @Mock
    private AppointmentRepositoryPort appointmentRepository;

    private AppointmentSchedulingService appointmentSchedulingService;

    @BeforeEach
    void setUp() {
        appointmentSchedulingService = new AppointmentSchedulingService(appointmentRepository);
    }

    @Test
    @DisplayName("Deve validar agendamento com sucesso quando não há conflitos")
    void shouldValidateAppointmentSuccessfullyWhenNoConflicts() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureTime);
        ProfessionalId professionalId = new ProfessionalId("PROF-123");
        String patientId = "PATIENT-456";
        
        when(appointmentRepository.existsByProfessionalIdAndDateRange(
            eq(professionalId.getValue()), any(), any()))
            .thenReturn(false);
        
        when(appointmentRepository.existsByDateRange(any(), any()))
            .thenReturn(false);
            
        when(appointmentRepository.existsByPatientIdAndDate(any(), eq(patientId)))
            .thenReturn(false);

        // When/Then - não deve lançar exceção
        assertDoesNotThrow(() -> 
            appointmentSchedulingService.validateAppointmentScheduling(
                appointmentTime, professionalId, patientId));
    }

    @Test
    @DisplayName("Deve lançar exceção quando profissional não está disponível")
    void shouldThrowExceptionWhenProfessionalIsNotAvailable() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureTime);
        ProfessionalId professionalId = new ProfessionalId("PROF-123");
        String patientId = "PATIENT-456";
        
        when(appointmentRepository.existsByProfessionalIdAndDateRange(
            eq(professionalId.getValue()), any(), any()))
            .thenReturn(true);

        // When/Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            appointmentSchedulingService.validateAppointmentScheduling(
                appointmentTime, professionalId, patientId));
                
        assertTrue(exception.getMessage().contains("Profissional já possui um agendamento"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando há conflito de horário geral")
    void shouldThrowExceptionWhenThereIsGeneralTimeConflict() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureTime);
        ProfessionalId professionalId = new ProfessionalId("PROF-123");
        String patientId = "PATIENT-456";
        
        when(appointmentRepository.existsByProfessionalIdAndDateRange(
            eq(professionalId.getValue()), any(), any()))
            .thenReturn(false);
        
        when(appointmentRepository.existsByDateRange(any(), any()))
            .thenReturn(true);

        // When/Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            appointmentSchedulingService.validateAppointmentScheduling(
                appointmentTime, professionalId, patientId));
                
        assertTrue(exception.getMessage().contains("Já existe um agendamento marcado"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando paciente já tem agendamento no dia")
    void shouldThrowExceptionWhenPatientAlreadyHasAppointmentOnDay() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureTime);
        ProfessionalId professionalId = new ProfessionalId("PROF-123");
        String patientId = "PATIENT-456";
        
        when(appointmentRepository.existsByProfessionalIdAndDateRange(
            eq(professionalId.getValue()), any(), any()))
            .thenReturn(false);
        
        when(appointmentRepository.existsByDateRange(any(), any()))
            .thenReturn(false);
            
        when(appointmentRepository.existsByPatientIdAndDate(any(), eq(patientId)))
            .thenReturn(true);

        // When/Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            appointmentSchedulingService.validateAppointmentScheduling(
                appointmentTime, professionalId, patientId));
                
        assertTrue(exception.getMessage().contains("Paciente já possui um agendamento nesta data"));
    }

    @Test
    @DisplayName("Deve verificar que profissional está disponível")
    void shouldVerifyProfessionalIsAvailable() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureTime);
        ProfessionalId professionalId = new ProfessionalId("PROF-123");
        
        when(appointmentRepository.existsByProfessionalIdAndDateRange(
            eq(professionalId.getValue()), any(), any()))
            .thenReturn(false);

        // When
        boolean isAvailable = appointmentSchedulingService.isProfessionalAvailable(
            professionalId, appointmentTime);

        // Then
        assertTrue(isAvailable);
    }

    @Test
    @DisplayName("Deve verificar que profissional não está disponível")
    void shouldVerifyProfessionalIsNotAvailable() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureTime);
        ProfessionalId professionalId = new ProfessionalId("PROF-123");
        
        when(appointmentRepository.existsByProfessionalIdAndDateRange(
            eq(professionalId.getValue()), any(), any()))
            .thenReturn(true);

        // When
        boolean isAvailable = appointmentSchedulingService.isProfessionalAvailable(
            professionalId, appointmentTime);

        // Then
        assertFalse(isAvailable);
    }

    @Test
    @DisplayName("Deve verificar que paciente está disponível")
    void shouldVerifyPatientIsAvailable() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureTime);
        String patientId = "PATIENT-456";
        
        when(appointmentRepository.existsByPatientIdAndDate(any(), eq(patientId)))
            .thenReturn(false);

        // When
        boolean isAvailable = appointmentSchedulingService.isPatientAvailable(
            patientId, appointmentTime);

        // Then
        assertTrue(isAvailable);
    }

    @Test
    @DisplayName("Deve verificar que paciente não está disponível")
    void shouldVerifyPatientIsNotAvailable() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureTime);
        String patientId = "PATIENT-456";
        
        when(appointmentRepository.existsByPatientIdAndDate(any(), eq(patientId)))
            .thenReturn(true);

        // When
        boolean isAvailable = appointmentSchedulingService.isPatientAvailable(
            patientId, appointmentTime);

        // Then
        assertFalse(isAvailable);
    }
}
