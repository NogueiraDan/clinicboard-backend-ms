package com.clinicboard.business_service.domain.model;

import com.clinicboard.business_service.domain.model.enums.AppointmentStatus;
import com.clinicboard.business_service.domain.model.enums.AppointmentType;
import com.clinicboard.business_service.domain.model.valueobjects.AppointmentTime;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o agregado Appointment
 */
class AppointmentTest {

    @Test
    @DisplayName("Deve criar um agendamento válido")
    void shouldCreateValidAppointment() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureDate);
        ProfessionalId professionalId = new ProfessionalId("prof-123");
        String patientId = "patient-456";
        String observation = "Consulta de rotina";

        // When
        Appointment appointment = new Appointment(appointmentTime, professionalId, patientId, observation, AppointmentType.MARCACAO);

        // Then
        assertNotNull(appointment);
        assertEquals(futureDate, appointment.getDate());
        assertEquals("prof-123", appointment.getProfessionalIdValue());
        assertEquals(patientId, appointment.getPatientId());
        assertEquals(observation, appointment.getObservation());
        assertEquals(AppointmentStatus.PENDING, appointment.getStatus());
        assertEquals(AppointmentType.MARCACAO, appointment.getType());
    }

    @Test
    @DisplayName("Deve agendar um compromisso pendente")
    void shouldSchedulePendingAppointment() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureDate);
        ProfessionalId professionalId = new ProfessionalId("prof-123");
        
        Appointment appointment = new Appointment(appointmentTime, professionalId, "patient-456", "Test", AppointmentType.MARCACAO);

        // When
        appointment.schedule();

        // Then
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
    }

    @Test
    @DisplayName("Deve cancelar um agendamento válido")
    void shouldCancelValidAppointment() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureDate);
        ProfessionalId professionalId = new ProfessionalId("prof-123");
        
        Appointment appointment = new Appointment(appointmentTime, professionalId, "patient-456", "Test", AppointmentType.MARCACAO);
        appointment.schedule();

        // When
        appointment.cancel("Cancelado pelo paciente");

        // Then
        assertEquals(AppointmentStatus.CANCELED, appointment.getStatus());
    }

    @Test
    @DisplayName("Não deve cancelar um agendamento já concluído")
    void shouldNotCancelCompletedAppointment() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureDate);
        ProfessionalId professionalId = new ProfessionalId("prof-123");
        
        Appointment appointment = new Appointment(appointmentTime, professionalId, "patient-456", "Test", AppointmentType.MARCACAO);
        appointment.schedule();
        appointment.complete();

        // When/Then
        assertThrows(IllegalStateException.class, () -> appointment.cancel("Tentativa de cancelamento"));
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
    }

    @Test
    @DisplayName("Deve reagendar um agendamento válido")
    void shouldRescheduleValidAppointment() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime newDate = LocalDateTime.now().plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0);
        
        AppointmentTime originalTime = new AppointmentTime(futureDate);
        AppointmentTime newTime = new AppointmentTime(newDate);
        ProfessionalId professionalId = new ProfessionalId("prof-123");
        
        Appointment appointment = new Appointment(originalTime, professionalId, "patient-456", "Test", AppointmentType.MARCACAO);
        appointment.schedule();

        // When
        appointment.reschedule(newTime, "Mudança de horário");

        // Then
        assertEquals(newDate, appointment.getDate());
        assertEquals(AppointmentStatus.RESCHEDULED, appointment.getStatus());
        assertEquals(AppointmentType.REMARCACAO, appointment.getType());
    }

    @Test
    @DisplayName("Deve completar um agendamento confirmado")
    void shouldCompleteScheduledAppointment() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureDate);
        ProfessionalId professionalId = new ProfessionalId("prof-123");
        
        Appointment appointment = new Appointment(appointmentTime, professionalId, "patient-456", "Test", AppointmentType.MARCACAO);
        appointment.schedule();

        // When
        appointment.complete();

        // Then
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
    }

    @Test
    @DisplayName("Deve marcar como falta um agendamento confirmado")
    void shouldMarkScheduledAppointmentAsNoShow() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureDate);
        ProfessionalId professionalId = new ProfessionalId("prof-123");
        
        Appointment appointment = new Appointment(appointmentTime, professionalId, "patient-456", "Test", AppointmentType.MARCACAO);
        appointment.schedule();

        // When
        appointment.markAsNoShow();

        // Then
        assertEquals(AppointmentStatus.NO_SHOW, appointment.getStatus());
    }

    @Test
    @DisplayName("Deve verificar se pode ser cancelado")
    void shouldCheckIfCanBeCancelled() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureDate);
        ProfessionalId professionalId = new ProfessionalId("prof-123");
        
        Appointment appointment = new Appointment(appointmentTime, professionalId, "patient-456", "Test", AppointmentType.MARCACAO);

        // When/Then
        assertTrue(appointment.canBeCancelled()); // PENDING pode ser cancelado
        
        appointment.schedule();
        assertTrue(appointment.canBeCancelled()); // SCHEDULED pode ser cancelado
        
        appointment.complete();
        assertFalse(appointment.canBeCancelled()); // COMPLETED não pode ser cancelado
    }

    @Test
    @DisplayName("Deve verificar se é do mesmo dia")
    void shouldCheckIfIsSameDay() {
        // Given
        LocalDateTime date1 = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        LocalDateTime date2 = LocalDateTime.now().plusDays(1).withHour(15).withMinute(30);
        LocalDateTime date3 = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0);
        
        AppointmentTime appointmentTime = new AppointmentTime(date1);
        ProfessionalId professionalId = new ProfessionalId("prof-123");
        
        Appointment appointment = new Appointment(appointmentTime, professionalId, "patient-456", "Test", AppointmentType.MARCACAO);

        // When/Then
        assertTrue(appointment.isSameDay(date2)); // Mesmo dia, horário diferente
        assertFalse(appointment.isSameDay(date3)); // Dia diferente
    }

    @Test
    @DisplayName("Deve lançar exceção com dados inválidos")
    void shouldThrowExceptionWithInvalidData() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        AppointmentTime appointmentTime = new AppointmentTime(futureDate);
        ProfessionalId professionalId = new ProfessionalId("prof-123");

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new Appointment(null, professionalId, "patient-456", "Test", AppointmentType.MARCACAO));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Appointment(appointmentTime, null, "patient-456", "Test", AppointmentType.MARCACAO));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Appointment(appointmentTime, professionalId, "", "Test", AppointmentType.MARCACAO));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Appointment(appointmentTime, professionalId, null, "Test", AppointmentType.MARCACAO));
    }
}
