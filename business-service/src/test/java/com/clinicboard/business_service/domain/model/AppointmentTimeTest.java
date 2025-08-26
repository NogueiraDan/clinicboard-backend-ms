package com.clinicboard.business_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para AppointmentTime Value Object
 * 
 * Objetivo: Garantir validação rigorosa de datas e horários de agendamento
 */
@DisplayName("AppointmentTime - Value Object Tests")
class AppointmentTimeTest {

    @Nested
    @DisplayName("Criação Válida")
    class ValidCreation {

        @Test
        @DisplayName("Deve criar AppointmentTime com data futura válida")
        void shouldCreateWithValidFutureDate() {
            // Given
            LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
            
            // When
            AppointmentTime appointmentTime = new AppointmentTime(futureDateTime);
            
            // Then
            assertNotNull(appointmentTime);
            assertEquals(futureDateTime, appointmentTime.value());
        }

        @Test
        @DisplayName("Deve aceitar horário em horário comercial")
        void shouldAcceptBusinessHours() {
            // Given
            LocalDateTime businessHour = LocalDateTime.now().plusDays(1).withHour(10).withMinute(30).withSecond(0).withNano(0);
            
            // When & Then
            assertDoesNotThrow(() -> new AppointmentTime(businessHour));
        }

        @Test
        @DisplayName("Deve aceitar múltiplos de 15 minutos")
        void shouldAcceptQuarterHourIntervals() {
            // Given
            LocalDateTime baseTime = LocalDateTime.now().plusDays(1).withHour(14).withSecond(0).withNano(0);
            
            // When & Then
            assertDoesNotThrow(() -> new AppointmentTime(baseTime.withMinute(0)));
            assertDoesNotThrow(() -> new AppointmentTime(baseTime.withMinute(15)));
            assertDoesNotThrow(() -> new AppointmentTime(baseTime.withMinute(30)));
            assertDoesNotThrow(() -> new AppointmentTime(baseTime.withMinute(45)));
        }

        @Test
        @DisplayName("Deve usar factory method of()")
        void shouldUseFactoryMethod() {
            // Given
            LocalDateTime dateTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
            
            // When
            AppointmentTime appointmentTime = AppointmentTime.of(dateTime);
            
            // Then
            assertNotNull(appointmentTime);
            assertEquals(dateTime, appointmentTime.value());
        }
    }

    @Nested
    @DisplayName("Validações de Horário Comercial")
    class BusinessHourValidations {

        @ParameterizedTest
        @DisplayName("Deve rejeitar horários fora do expediente")
        @ValueSource(ints = {7, 19, 20, 22, 23, 0, 1, 6})
        void shouldRejectOutOfBusinessHours(int hour) {
            // Given
            LocalDateTime outOfBusinessHours = LocalDateTime.now().plusDays(1).withHour(hour).withMinute(0).withSecond(0).withNano(0);
            
            // When & Then
            AppointmentTime.InvalidAppointmentTimeException exception = 
                assertThrows(AppointmentTime.InvalidAppointmentTimeException.class,
                           () -> new AppointmentTime(outOfBusinessHours));
            
            assertTrue(exception.getMessage().contains("horário comercial"));
        }

        @ParameterizedTest
        @DisplayName("Deve aceitar horários dentro do expediente")
        @ValueSource(ints = {8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18})
        void shouldAcceptBusinessHours(int hour) {
            // Given
            LocalDateTime businessHours = LocalDateTime.now().plusDays(1).withHour(hour).withMinute(0).withSecond(0).withNano(0);
            
            // When & Then
            assertDoesNotThrow(() -> new AppointmentTime(businessHours));
        }
    }

    @Nested
    @DisplayName("Validações de Intervalo de Tempo")
    class TimeIntervalValidations {

        @ParameterizedTest
        @DisplayName("Deve rejeitar minutos que não são múltiplos de 15")
        @ValueSource(ints = {1, 5, 7, 12, 14, 16, 23, 29, 31, 44, 46, 59})
        void shouldRejectNonQuarterHourMinutes(int minute) {
            // Given
            LocalDateTime invalidMinutes = LocalDateTime.now().plusDays(1).withHour(14).withMinute(minute).withSecond(0).withNano(0);
            
            // When & Then
            AppointmentTime.InvalidAppointmentTimeException exception = 
                assertThrows(AppointmentTime.InvalidAppointmentTimeException.class,
                           () -> new AppointmentTime(invalidMinutes));
            
            assertTrue(exception.getMessage().contains("múltiplos de 15"));
        }

        @Test
        @DisplayName("Deve rejeitar segundos diferentes de zero")
        void shouldRejectNonZeroSeconds() {
            // Given
            LocalDateTime withSeconds = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(30).withNano(0);
            
            // When & Then
            AppointmentTime.InvalidAppointmentTimeException exception = 
                assertThrows(AppointmentTime.InvalidAppointmentTimeException.class,
                           () -> new AppointmentTime(withSeconds));
            
            assertTrue(exception.getMessage().contains("segundos devem ser zero"));
        }

        @Test
        @DisplayName("Deve rejeitar nanossegundos diferentes de zero")
        void shouldRejectNonZeroNanos() {
            // Given
            LocalDateTime withNanos = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(123456789);
            
            // When & Then
            AppointmentTime.InvalidAppointmentTimeException exception = 
                assertThrows(AppointmentTime.InvalidAppointmentTimeException.class,
                           () -> new AppointmentTime(withNanos));
            
            assertTrue(exception.getMessage().contains("segundos devem ser zero"));
        }
    }

    @Nested
    @DisplayName("Validações de Data")
    class DateValidations {

        @Test
        @DisplayName("Deve rejeitar data nula")
        void shouldRejectNullDate() {
            // When & Then
            AppointmentTime.InvalidAppointmentTimeException exception = 
                assertThrows(AppointmentTime.InvalidAppointmentTimeException.class,
                           () -> new AppointmentTime(null));
            
            assertTrue(exception.getMessage().contains("não pode ser"));
        }

        @Test
        @DisplayName("Deve rejeitar datas passadas")
        void shouldRejectPastDates() {
            // Given
            LocalDateTime pastDate = LocalDateTime.now().minusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
            
            // When & Then
            AppointmentTime.InvalidAppointmentTimeException exception = 
                assertThrows(AppointmentTime.InvalidAppointmentTimeException.class,
                           () -> new AppointmentTime(pastDate));
            
            assertTrue(exception.getMessage().contains("deve ser no futuro"));
        }

        @Test
        @DisplayName("Deve rejeitar data atual (muito próxima)")
        void shouldRejectCurrentTime() {
            // Given
            LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
            
            // When & Then
            AppointmentTime.InvalidAppointmentTimeException exception = 
                assertThrows(AppointmentTime.InvalidAppointmentTimeException.class,
                           () -> new AppointmentTime(now));
            
            assertTrue(exception.getMessage().contains("deve ser no futuro"));
        }

        @Test
        @DisplayName("Deve rejeitar agendamentos muito distantes (> 1 ano)")
        void shouldRejectTooFarInFuture() {
            // Given
            LocalDateTime tooFarFuture = LocalDateTime.now().plusYears(2).withHour(14).withMinute(0).withSecond(0).withNano(0);
            
            // When & Then
            AppointmentTime.InvalidAppointmentTimeException exception = 
                assertThrows(AppointmentTime.InvalidAppointmentTimeException.class,
                           () -> new AppointmentTime(tooFarFuture));
            
            assertTrue(exception.getMessage().contains("máximo de 1 ano"));
        }

        @Test
        @DisplayName("Deve aceitar limite máximo de 1 ano menos 1 dia")
        void shouldAcceptMaximumValidDate() {
            // Given
            LocalDateTime maxValidDate = LocalDateTime.now().plusYears(1).minusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
            
            // When & Then
            assertDoesNotThrow(() -> new AppointmentTime(maxValidDate));
        }
    }

    @Nested
    @DisplayName("Comportamento de Value Object")
    class ValueObjectBehavior {

        @Test
        @DisplayName("Deve ter igualdade por valor")
        void shouldHaveValueEquality() {
            // Given
            LocalDateTime dateTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
            AppointmentTime time1 = new AppointmentTime(dateTime);
            AppointmentTime time2 = new AppointmentTime(dateTime);
            
            // When & Then
            assertEquals(time1, time2);
            assertEquals(time1.hashCode(), time2.hashCode());
        }

        @Test
        @DisplayName("Deve ter desigualdade para valores diferentes")
        void shouldHaveValueInequality() {
            // Given
            LocalDateTime dateTime1 = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime dateTime2 = LocalDateTime.now().plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0);
            AppointmentTime time1 = new AppointmentTime(dateTime1);
            AppointmentTime time2 = new AppointmentTime(dateTime2);
            
            // When & Then
            assertNotEquals(time1, time2);
        }

        @Test
        @DisplayName("Deve ser imutável")
        void shouldBeImmutable() {
            // Given
            LocalDateTime originalDateTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
            AppointmentTime appointmentTime = new AppointmentTime(originalDateTime);
            
            // When
            LocalDateTime retrievedValue = appointmentTime.value();
            
            // Then
            assertEquals(originalDateTime, retrievedValue);
            
            // LocalDateTime é imutável, então modificações não afetam o Value Object
            assertEquals(originalDateTime, appointmentTime.value());
        }
    }

    @Nested
    @DisplayName("Métodos Utilitários")
    class UtilityMethods {

        @Test
        @DisplayName("Deve implementar isBefore corretamente")
        void shouldImplementIsBeforeCorrectly() {
            // Given
            LocalDateTime earlier = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime later = LocalDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0);
            
            AppointmentTime earlierTime = new AppointmentTime(earlier);
            AppointmentTime laterTime = new AppointmentTime(later);
            
            // When & Then
            assertTrue(earlierTime.isBefore(laterTime));
            assertFalse(laterTime.isBefore(earlierTime));
            assertFalse(earlierTime.isBefore(earlierTime));
        }

        @Test
        @DisplayName("Deve implementar isAfter corretamente")
        void shouldImplementIsAfterCorrectly() {
            // Given
            LocalDateTime earlier = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime later = LocalDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0);
            
            AppointmentTime earlierTime = new AppointmentTime(earlier);
            AppointmentTime laterTime = new AppointmentTime(later);
            
            // When & Then
            assertTrue(laterTime.isAfter(earlierTime));
            assertFalse(earlierTime.isAfter(laterTime));
            assertFalse(earlierTime.isAfter(earlierTime));
        }

        @Test
        @DisplayName("Deve calcular diferença de tempo corretamente")
        void shouldCalculateTimeDifferenceCorrectly() {
            // Given
            LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = start.plusMinutes(45);
            
            AppointmentTime startTime = new AppointmentTime(start);
            AppointmentTime endTime = new AppointmentTime(end);
            
            // When
            long minutesDifference = startTime.minutesUntil(endTime);
            
            // Then
            assertEquals(45, minutesDifference);
        }
    }

    @Nested
    @DisplayName("Exception Behavior")
    class ExceptionBehavior {

        @Test
        @DisplayName("InvalidAppointmentTimeException deve estender DomainException")
        void exceptionShouldExtendDomainException() {
            // When
            AppointmentTime.InvalidAppointmentTimeException exception = 
                new AppointmentTime.InvalidAppointmentTimeException("test message");
            
            // Then
            assertTrue(exception instanceof com.clinicboard.business_service.domain.exception.DomainException);
            assertEquals("INVALID_APPOINTMENT_TIME", exception.getErrorCode());
            assertEquals("test message", exception.getMessage());
        }
    }
}
