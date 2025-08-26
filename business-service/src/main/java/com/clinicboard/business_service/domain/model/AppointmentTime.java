package com.clinicboard.business_service.domain.model;

import com.clinicboard.business_service.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Value Object que representa um horário de agendamento com suas regras de negócio.
 * 
 * Encapsula validações temporais específicas do domínio clínico:
 * - Horário comercial (8h às 18h)
 * - Não pode ser no passado
 * - Intervalos de 15 minutos
 * - Máximo de 1 ano no futuro
 */
public record AppointmentTime(LocalDateTime value) {
    
    private static final LocalTime BUSINESS_START = LocalTime.of(8, 0);
    private static final LocalTime BUSINESS_END = LocalTime.of(18, 0);

    public AppointmentTime {
        validateAppointmentTime(value);
    }

    private static void validateAppointmentTime(LocalDateTime value) {
        if (value == null) {
            throw new InvalidAppointmentTimeException("Horário do agendamento não pode ser nulo ou vazio");
        }

        // Não pode ser no passado ou muito próximo do presente
        if (value.isBefore(LocalDateTime.now()) || value.isEqual(LocalDateTime.now())) {
            throw new InvalidAppointmentTimeException("Agendamento deve ser no futuro");
        }

        // Não pode ser muito distante no futuro (máximo 1 ano)
        if (value.isAfter(LocalDateTime.now().plusYears(1))) {
            throw new InvalidAppointmentTimeException("Agendamento não pode ser feito com mais de 1 ano de antecedência (máximo de 1 ano)");
        }

        // Deve estar dentro do horário comercial
        LocalTime timeOfDay = value.toLocalTime();
        if (timeOfDay.isBefore(BUSINESS_START) || timeOfDay.isAfter(BUSINESS_END)) {
            throw new InvalidAppointmentTimeException(
                String.format("Horário deve estar dentro do horário comercial (%s às %s)", BUSINESS_START, BUSINESS_END)
            );
        }

        // Deve estar em intervalos de 15 minutos
        if (value.getMinute() % 15 != 0 || value.getSecond() != 0 || value.getNano() != 0) {
            throw new InvalidAppointmentTimeException(
                "Agendamentos devem ser em múltiplos de 15 minutos e segundos devem ser zero"
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

    /**
     * Verifica se este horário é antes de outro
     */
    public boolean isBefore(AppointmentTime other) {
        return this.value.isBefore(other.value);
    }

    /**
     * Verifica se este horário é depois de outro
     */
    public boolean isAfter(AppointmentTime other) {
        return this.value.isAfter(other.value);
    }

    /**
     * Exception específica para validação de horário de agendamento
     */
    public static class InvalidAppointmentTimeException extends DomainException {
        public InvalidAppointmentTimeException(String message) {
            super(message);
        }

        @Override
        public String getErrorCode() {
            return "INVALID_APPOINTMENT_TIME";
        }
    }
}
