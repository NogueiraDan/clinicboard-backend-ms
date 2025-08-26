package com.clinicboard.business_service.application.port.in;

import com.clinicboard.business_service.domain.model.AppointmentId;
import com.clinicboard.business_service.domain.model.AppointmentTime;
import com.clinicboard.business_service.domain.model.AppointmentType;
import com.clinicboard.business_service.domain.model.AppointmentStatus;
import com.clinicboard.business_service.domain.model.PatientId;
import com.clinicboard.business_service.domain.model.ProfessionalId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Query para buscar consultas no sistema.
 * 
 * Centraliza todas as operações de consulta relacionadas a agendamentos,
 * seguindo o padrão CQRS para separar comandos de queries.
 * 
 * Princípios DDD aplicados:
 * - Separação clara entre comandos e consultas
 * - Queries expressivas na linguagem ubíqua
 * - Read models otimizados para consulta
 */
public interface FindAppointmentQuery {

    /**
     * Busca uma consulta por ID.
     * 
     * @param appointmentId ID da consulta
     * @return consulta encontrada ou vazio se não existir
     */
    Optional<AppointmentView> findById(AppointmentId appointmentId);

    /**
     * Busca consultas por paciente.
     * 
     * @param patientId ID do paciente
     * @return lista de consultas do paciente
     */
    List<AppointmentView> findByPatientId(PatientId patientId);

    /**
     * Busca consultas por profissional.
     * 
     * @param professionalId ID do profissional
     * @return lista de consultas do profissional
     */
    List<AppointmentView> findByProfessionalId(ProfessionalId professionalId);

    /**
     * Busca consultas por data.
     * 
     * @param date data das consultas
     * @return lista de consultas na data especificada
     */
    List<AppointmentView> findByDate(LocalDate date);

    /**
     * Busca consultas por período.
     * 
     * @param startDate data inicial
     * @param endDate data final
     * @return lista de consultas no período
     */
    List<AppointmentView> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Busca consultas por status.
     * 
     * @param status status da consulta
     * @return lista de consultas com o status especificado
     */
    List<AppointmentView> findByStatus(AppointmentStatus status);

    /**
     * Verifica disponibilidade de horário para um profissional.
     * 
     * @param professionalId ID do profissional
     * @param appointmentTime horário desejado
     * @return true se o horário estiver disponível
     */
    boolean isTimeSlotAvailable(ProfessionalId professionalId, AppointmentTime appointmentTime);

    /**
     * View otimizada para leitura de consultas.
     * 
     * Representa uma projeção dos dados de consulta
     * otimizada para operações de leitura.
     */
    record AppointmentView(
        AppointmentId appointmentId,
        PatientId patientId,
        String patientName,
        ProfessionalId professionalId,
        String professionalName,
        AppointmentTime appointmentTime,
        AppointmentType appointmentType,
        AppointmentStatus status,
        String observations,
        String cancellationReason
    ) {
        /**
         * Factory method para criar view a partir de dados básicos.
         */
        public static AppointmentView of(
                AppointmentId appointmentId,
                PatientId patientId,
                String patientName,
                ProfessionalId professionalId,
                String professionalName,
                AppointmentTime appointmentTime,
                AppointmentType appointmentType,
                AppointmentStatus status) {
            return new AppointmentView(
                appointmentId,
                patientId,
                patientName,
                professionalId,
                professionalName,
                appointmentTime,
                appointmentType,
                status,
                null,
                null
            );
        }

        /**
         * Verifica se a consulta está ativa (não cancelada).
         */
        public boolean isActive() {
            return status != AppointmentStatus.CANCELLED;
        }

        /**
         * Verifica se a consulta pode ser cancelada.
         */
        public boolean canBeCancelled() {
            return status == AppointmentStatus.SCHEDULED;
        }
    }
}