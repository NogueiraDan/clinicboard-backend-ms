package com.clinicboard.business_service.domain.model;

import com.clinicboard.business_service.domain.exception.InvalidTimeSlotException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Value Object que representa um horário de agendamento com suas regras de negócio.
 * 
 * Encapsula validações temporais específicas do domínio clínico:
 * - Horário comercial (8h às 19h)
 * - Não pode ser no passado
 * - Intervalos mínimos entre consultas
 */
public record AppointmentTime(LocalDateTime value) {
    
    private static final LocalTime BUSINESS_START = LocalTime.of(8, 0);
    private static final LocalTime BUSINESS_END = LocalTime.of(19, 0);
    private static final int MINIMUM_ADVANCE_HOURS = 2;

    public AppointmentTime {
        validateAppointmentTime(value);
    }

    private static void validateAppointmentTime(LocalDateTime value) {
        if (value == null) {
            throw new InvalidTimeSlotException("null", "Horário do agendamento não pode ser nulo");
        }

        // Não pode ser no passado
        if (value.isBefore(LocalDateTime.now().plusHours(MINIMUM_ADVANCE_HOURS))) {
            throw new InvalidTimeSlotException(
                value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                "Agendamento deve ser feito com pelo menos 2 horas de antecedência"
            );
        }

        // Deve estar dentro do horário comercial
        LocalTime timeOfDay = value.toLocalTime();
        if (timeOfDay.isBefore(BUSINESS_START) || timeOfDay.isAfter(BUSINESS_END)) {
            throw new InvalidTimeSlotException(
                timeOfDay.format(DateTimeFormatter.ofPattern("HH:mm")),
                String.format("Horário deve estar entre %s e %s", BUSINESS_START, BUSINESS_END)
            );
        }

        // Deve estar em intervalos de 30 minutos
        if (value.getMinute() % 30 != 0 || value.getSecond() != 0 || value.getNano() != 0) {
            throw new InvalidTimeSlotException(
                value.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                "Agendamentos devem ser em intervalos de 30 minutos (ex: 08:00, 08:30, 09:00)"
            );
        }
    }

    /**
     * Factory method para criação segura
     */
    public static AppointmentTime of(LocalDateTime dateTime) {
        return new AppointmentTime(dateTime);
    }

    /**
     * Verifica se este horário está dentro do horário comercial
     */
    public boolean isWithinBusinessHours() {
        LocalTime timeOfDay = value.toLocalTime();
        return !timeOfDay.isBefore(BUSINESS_START) && !timeOfDay.isAfter(BUSINESS_END);
    }

    /**
     * Verifica se há conflito temporal com outro agendamento
     * Considera uma consulta de 30 minutos
     */
    public boolean conflictsWith(AppointmentTime other) {
        if (other == null) return false;
        
        LocalDateTime thisStart = this.value;
        LocalDateTime thisEnd = thisStart.plusMinutes(30);
        
        LocalDateTime otherStart = other.value;
        LocalDateTime otherEnd = otherStart.plusMinutes(30);
        
        // Há conflito se há sobreposição entre os intervalos
        return thisStart.isBefore(otherEnd) && thisEnd.isAfter(otherStart);
    }

    /**
     * Calcula a diferença em minutos para outro horário
     */
    public long minutesUntil(AppointmentTime other) {
        return ChronoUnit.MINUTES.between(this.value, other.value);
    }

    /**
     * Retorna representação formatada para exibição
     */
    public String getFormattedDateTime() {
        return value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Retorna apenas o horário formatado
     */
    public String getFormattedTime() {
        return value.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * Retorna o próximo slot disponível (30 minutos depois)
     */
    public AppointmentTime nextSlot() {
        return new AppointmentTime(value.plusMinutes(30));
    }

    /**
     * Retorna o slot anterior (30 minutos antes)
     */
    public AppointmentTime previousSlot() {
        return new AppointmentTime(value.minusMinutes(30));
    }
}
