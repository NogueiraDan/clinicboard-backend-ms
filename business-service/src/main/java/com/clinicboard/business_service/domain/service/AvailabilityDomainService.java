package com.clinicboard.business_service.domain.service;

import com.clinicboard.business_service.domain.exception.AppointmentConflictException;
import com.clinicboard.business_service.domain.exception.InvalidTimeSlotException;
import com.clinicboard.business_service.domain.exception.PatientBusinessRuleException;
import com.clinicboard.business_service.domain.model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Domain Service responsável por regras complexas de disponibilidade de agendamentos.
 * 
 * Este é um Domain Service porque:
 * - Coordena regras que envolvem múltiplos agregados (Appointment, Patient)
 * - Implementa lógica de negócio que não pertence naturalmente a uma entidade específica
 * - Encapsula conhecimento sobre políticas de agendamento da clínica
 */
public class AvailabilityDomainService {

    /**
     * Verifica se um horário está disponível para agendamento
     */
    public boolean isTimeSlotAvailable(ProfessionalId professionalId,
                                     AppointmentTime requestedTime,
                                     List<Appointment> existingAppointments) {
        
        // Regra 1: Horário deve estar no futuro com antecedência mínima
        if (!requestedTime.value().isAfter(LocalDateTime.now().plusHours(2))) {
            return false;
        }

        // Regra 2: Deve estar dentro do horário comercial
        if (!requestedTime.isWithinBusinessHours()) {
            return false;
        }

        // Regra 3: Não deve conflitar com agendamentos existentes do profissional
        return existingAppointments.stream()
            .filter(apt -> apt.belongsToProfessional(professionalId))
            .filter(apt -> apt.isActive()) // Apenas agendamentos ativos
            .noneMatch(apt -> apt.getScheduledTime().conflictsWith(requestedTime));
    }

    /**
     * Verifica se paciente pode agendar na data especificada
     */
    public void validatePatientCanScheduleOnDate(PatientId patientId,
                                                LocalDate date,
                                                List<Appointment> patientAppointments) {
        
        // Regra de negócio: Paciente só pode ter 1 agendamento por dia
        boolean hasAppointmentOnDate = patientAppointments.stream()
            .filter(apt -> apt.belongsToPatient(patientId))
            .filter(apt -> apt.isActive())
            .anyMatch(apt -> apt.getScheduledTime().value().toLocalDate().equals(date));

        if (hasAppointmentOnDate) {
            throw new PatientBusinessRuleException(
                patientId.value(),
                "Paciente já possui agendamento ativo nesta data"
            );
        }
    }

    /**
     * Valida regras de antecedência para tipo de agendamento
     */
    public void validateAdvanceNotice(AppointmentTime requestedTime, AppointmentType type) {
        LocalDateTime minimumTime = LocalDateTime.now().plusHours(type.getMinimumAdvanceHours());
        
        if (requestedTime.value().isBefore(minimumTime)) {
            throw new InvalidTimeSlotException(
                requestedTime.getFormattedDateTime(),
                String.format("Tipo '%s' requer antecedência mínima de %d horas", 
                    type.getDisplayName(), 
                    type.getMinimumAdvanceHours())
            );
        }
    }

    /**
     * Gera lista de horários disponíveis para um profissional em uma data
     */
    public List<AppointmentTime> generateAvailableSlots(ProfessionalId professionalId,
                                                       LocalDate date,
                                                       List<Appointment> existingAppointments) {
        
        List<AppointmentTime> availableSlots = new ArrayList<>();
        
        // Horário comercial: 8h às 19h, intervalos de 30 minutos
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(19, 0);
        
        // Coleta horários ocupados
        Set<LocalTime> busyTimes = new HashSet<>();
        existingAppointments.stream()
            .filter(apt -> apt.belongsToProfessional(professionalId))
            .filter(apt -> apt.isActive())
            .filter(apt -> apt.getScheduledTime().value().toLocalDate().equals(date))
            .forEach(apt -> {
                LocalTime appointmentTime = apt.getScheduledTime().value().toLocalTime();
                busyTimes.add(appointmentTime);
                // Bloqueia também os 30 minutos seguintes
                busyTimes.add(appointmentTime.plusMinutes(30));
            });

        // Gera slots disponíveis
        LocalTime current = start;
        while (!current.isAfter(end)) {
            if (!busyTimes.contains(current)) {
                LocalDateTime slotDateTime = LocalDateTime.of(date, current);
                
                // Verifica se está no futuro com antecedência mínima
                if (slotDateTime.isAfter(LocalDateTime.now().plusHours(2))) {
                    try {
                        AppointmentTime slot = AppointmentTime.of(slotDateTime);
                        availableSlots.add(slot);
                    } catch (InvalidTimeSlotException e) {
                        // Horário inválido, pula para o próximo
                    }
                }
            }
            current = current.plusMinutes(30);
        }
        
        return availableSlots;
    }

    /**
     * Calcula estatísticas de disponibilidade para um período
     */
    public AvailabilityStats calculateAvailabilityStats(ProfessionalId professionalId,
                                                       LocalDate startDate,
                                                       LocalDate endDate,
                                                       List<Appointment> appointments) {
        
        int totalWorkingDays = calculateWorkingDays(startDate, endDate);
        int slotsPerDay = calculateSlotsPerDay();
        int totalAvailableSlots = totalWorkingDays * slotsPerDay;
        
        long bookedSlots = appointments.stream()
            .filter(apt -> apt.belongsToProfessional(professionalId))
            .filter(apt -> apt.isActive())
            .filter(apt -> {
                LocalDate aptDate = apt.getScheduledTime().value().toLocalDate();
                return !aptDate.isBefore(startDate) && !aptDate.isAfter(endDate);
            })
            .count();

        double occupancyRate = totalAvailableSlots > 0 ? 
            (double) bookedSlots / totalAvailableSlots * 100 : 0;

        return new AvailabilityStats(
            totalAvailableSlots,
            (int) bookedSlots,
            totalAvailableSlots - (int) bookedSlots,
            occupancyRate
        );
    }

    /**
     * Valida se um agendamento pode ser criado (regra principal)
     */
    public void validateAppointmentCreation(PatientId patientId,
                                          ProfessionalId professionalId,
                                          AppointmentTime requestedTime,
                                          AppointmentType type,
                                          List<Appointment> existingAppointments,
                                          Patient patient) {
        
        // 1. Validar se paciente está ativo
        if (!patient.isActive()) {
            throw new PatientBusinessRuleException(
                patientId.value(),
                "Paciente inativo não pode agendar consultas"
            );
        }

        // 2. Validar se paciente pode agendar neste tipo
        if (!patient.canScheduleAppointment(requestedTime)) {
            throw new PatientBusinessRuleException(
                patientId.value(),
                "Paciente não atende aos critérios para agendamento"
            );
        }

        // 3. Validar antecedência necessária
        validateAdvanceNotice(requestedTime, type);

        // 4. Validar disponibilidade do horário
        if (!isTimeSlotAvailable(professionalId, requestedTime, existingAppointments)) {
            throw new AppointmentConflictException(
                professionalId.value(),
                requestedTime.getFormattedDateTime()
            );
        }

        // 5. Validar regra de um agendamento por dia
        validatePatientCanScheduleOnDate(
            patientId, 
            requestedTime.value().toLocalDate(), 
            existingAppointments
        );
    }

    // Métodos auxiliares
    private int calculateWorkingDays(LocalDate start, LocalDate end) {
        // Simplificado: assume todos os dias como úteis (exceto fins de semana)
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        // Aproximação: ~71% dos dias são úteis (5/7)
        return (int) (totalDays * 0.71);
    }

    private int calculateSlotsPerDay() {
        // 8h às 19h = 11 horas = 22 slots de 30 minutos
        return 22;
    }

    /**
     * Record para estatísticas de disponibilidade
     */
    public record AvailabilityStats(
        int totalSlots,
        int bookedSlots,
        int availableSlots,
        double occupancyRate
    ) {}
}
