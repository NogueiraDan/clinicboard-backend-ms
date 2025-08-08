package com.clinicboard.business_service.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o Value Object AppointmentTime
 */
class AppointmentTimeTest {

    @Test
    @DisplayName("Deve criar horário válido no futuro")
    void shouldCreateValidFutureTime() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusHours(1);

        // When
        AppointmentTime appointmentTime = new AppointmentTime(futureTime);

        // Then
        assertEquals(futureTime, appointmentTime.getValue());
        assertFalse(appointmentTime.isInPast());
    }

    @Test
    @DisplayName("Deve identificar horário no passado")
    void shouldIdentifyPastTime() {
        // Given
        LocalDateTime pastTime = LocalDateTime.now().minusHours(1);

        // When
        AppointmentTime appointmentTime = new AppointmentTime(pastTime);

        // Then
        assertTrue(appointmentTime.isInPast());
    }

    @Test
    @DisplayName("Deve lançar exceção com horário nulo")
    void shouldThrowExceptionWithNullTime() {
        // Given/When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new AppointmentTime(null));
    }

    @Test
    @DisplayName("Deve formatar horário corretamente")
    void shouldFormatTimeCorrectly() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 14, 30);
        AppointmentTime appointmentTime = new AppointmentTime(dateTime);

        // When
        String formatted = appointmentTime.format();

        // Then
        assertEquals("15/01/2024 14:30", formatted);
    }

    @Test
    @DisplayName("Deve verificar igualdade de horários")
    void shouldVerifyTimeEquality() {
        // Given
        LocalDateTime dateTime1 = LocalDateTime.of(2024, 1, 15, 14, 30);
        LocalDateTime dateTime2 = LocalDateTime.of(2024, 1, 15, 14, 30);
        LocalDateTime dateTime3 = LocalDateTime.of(2024, 1, 15, 15, 30);
        
        AppointmentTime time1 = new AppointmentTime(dateTime1);
        AppointmentTime time2 = new AppointmentTime(dateTime2);
        AppointmentTime time3 = new AppointmentTime(dateTime3);

        // When/Then
        assertEquals(time1, time2);
        assertEquals(time1.hashCode(), time2.hashCode());
        
        assertNotEquals(time1, time3);
        assertNotEquals(time1, null);
        assertNotEquals(time1, "not an appointment time");
    }

    @Test
    @DisplayName("Deve validar se horário é em horário comercial")
    void shouldValidateBusinessHours() {
        // Given
        AppointmentTime morningTime = new AppointmentTime(LocalDateTime.of(2024, 1, 15, 9, 0));
        AppointmentTime afternoonTime = new AppointmentTime(LocalDateTime.of(2024, 1, 15, 15, 0));
        AppointmentTime eveningTime = new AppointmentTime(LocalDateTime.of(2024, 1, 15, 19, 0));
        AppointmentTime earlyTime = new AppointmentTime(LocalDateTime.of(2024, 1, 15, 7, 0));
        AppointmentTime lateTime = new AppointmentTime(LocalDateTime.of(2024, 1, 15, 21, 0));

        // When/Then
        assertTrue(morningTime.isBusinessHours());
        assertTrue(afternoonTime.isBusinessHours());
        assertTrue(eveningTime.isBusinessHours());
        assertFalse(earlyTime.isBusinessHours());
        assertFalse(lateTime.isBusinessHours());
    }

    @Test
    @DisplayName("Deve ter representação string adequada")
    void shouldHaveProperStringRepresentation() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 14, 30);
        AppointmentTime appointmentTime = new AppointmentTime(dateTime);

        // When
        String stringRepresentation = appointmentTime.toString();

        // Then
        assertTrue(stringRepresentation.contains("AppointmentTime"));
        assertTrue(stringRepresentation.contains("15/01/2024 14:30"));
    }

    @Test
    @DisplayName("Deve comparar horários corretamente")
    void shouldCompareTimesCorrectly() {
        // Given
        AppointmentTime earlier = new AppointmentTime(LocalDateTime.of(2024, 1, 15, 9, 0));
        AppointmentTime later = new AppointmentTime(LocalDateTime.of(2024, 1, 15, 15, 0));
        AppointmentTime same = new AppointmentTime(LocalDateTime.of(2024, 1, 15, 9, 0));

        // When/Then
        assertTrue(earlier.isBefore(later));
        assertFalse(later.isBefore(earlier));
        assertFalse(earlier.isBefore(same));
        
        assertTrue(later.isAfter(earlier));
        assertFalse(earlier.isAfter(later));
        assertFalse(earlier.isAfter(same));
    }
}
