package com.clinicboard.business_service.domain.model.valueobjects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Value Object que representa um horário de agendamento
 * PURO - sem dependências de infraestrutura
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppointmentTime {
    
    private static final LocalTime BUSINESS_START = LocalTime.of(8, 0);
    private static final LocalTime BUSINESS_END = LocalTime.of(19, 0);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private LocalDateTime dateTime;
    
    public AppointmentTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("Appointment time cannot be null");
        }
        this.dateTime = dateTime;
    }
    
    private void validateDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("Data e hora não podem ser nulas");
        }
        
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível agendar para uma data/hora passada");
        }
        
        LocalTime time = dateTime.toLocalTime();
        if (time.isBefore(BUSINESS_START) || time.isAfter(BUSINESS_END)) {
            throw new IllegalArgumentException(
                String.format("Horário deve estar entre %s e %s", BUSINESS_START, BUSINESS_END)
            );
        }
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public LocalDateTime getValue() {
        return dateTime;
    }

    public LocalDateTime toLocalDateTime() {
        return dateTime;
    }
    
    public LocalTime getTime() {
        return dateTime.toLocalTime();
    }

    public boolean isInPast() {
        return dateTime.isBefore(LocalDateTime.now());
    }

    public String format() {
        return dateTime.format(FORMATTER);
    }

    public boolean isBusinessHours() {
        LocalTime time = dateTime.toLocalTime();
        return !time.isBefore(BUSINESS_START) && !time.isAfter(BUSINESS_END);
    }

    public boolean isBefore(AppointmentTime other) {
        return this.dateTime.isBefore(other.dateTime);
    }

    public boolean isAfter(AppointmentTime other) {
        return this.dateTime.isAfter(other.dateTime);
    }
    
    public boolean isWithinRange(LocalDateTime startRange, LocalDateTime endRange) {
        return dateTime.isAfter(startRange) && dateTime.isBefore(endRange);
    }
    
    public boolean isSameDay(LocalDateTime otherDate) {
        return dateTime.toLocalDate().equals(otherDate.toLocalDate());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentTime that = (AppointmentTime) o;
        return Objects.equals(dateTime, that.dateTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(dateTime);
    }
    
    @Override
    public String toString() {
        return "AppointmentTime{" +
                "value=" + dateTime.format(FORMATTER) +
                '}';
    }
}
