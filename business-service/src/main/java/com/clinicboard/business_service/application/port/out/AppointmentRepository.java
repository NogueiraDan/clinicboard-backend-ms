package com.clinicboard.business_service.application.port.out;

import com.clinicboard.business_service.domain.model.Appointment;
import com.clinicboard.business_service.domain.model.AppointmentId;
import com.clinicboard.business_service.domain.model.AppointmentTime;
import com.clinicboard.business_service.domain.model.AppointmentStatus;
import com.clinicboard.business_service.domain.model.PatientId;
import com.clinicboard.business_service.domain.model.ProfessionalId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Porta de saída para persistência de agendamentos.
 * 
 * Define o contrato que a infraestrutura deve implementar
 * para operações de persistência relacionadas a consultas.
 * 
 * Princípios DDD aplicados:
 * - Interface na camada de aplicação (Dependency Inversion)
 * - Contrato agnóstico à tecnologia de persistência
 * - Foco no domínio, não na implementação técnica
 */
public interface AppointmentRepository {

    /**
     * Salva uma nova consulta ou atualiza uma existente.
     * 
     * @param appointment consulta a ser persistida
     * @return consulta persistida com ID gerado (se novo)
     */
    Appointment save(Appointment appointment);

    /**
     * Busca uma consulta por ID.
     * 
     * @param appointmentId ID da consulta
     * @return consulta encontrada ou vazio se não existir
     */
    Optional<Appointment> findById(AppointmentId appointmentId);

    /**
     * Busca consultas por paciente.
     * 
     * @param patientId ID do paciente
     * @return lista de consultas do paciente
     */
    List<Appointment> findByPatientId(PatientId patientId);

    /**
     * Busca consultas por profissional.
     * 
     * @param professionalId ID do profissional
     * @return lista de consultas do profissional
     */
    List<Appointment> findByProfessionalId(ProfessionalId professionalId);

    /**
     * Busca consultas por data.
     * 
     * @param date data das consultas
     * @return lista de consultas na data especificada
     */
    List<Appointment> findByDate(LocalDate date);

    /**
     * Busca consultas por período.
     * 
     * @param startDate data inicial
     * @param endDate data final
     * @return lista de consultas no período
     */
    List<Appointment> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Busca consultas por intervalo de data/hora específico.
     * 
     * @param startDateTime data/hora inicial
     * @param endDateTime data/hora final
     * @return lista de consultas no intervalo
     */
    List<Appointment> findByDateTimeRange(java.time.LocalDateTime startDateTime, java.time.LocalDateTime endDateTime);

    /**
     * Busca consultas por status.
     * 
     * @param status status da consulta
     * @return lista de consultas com o status especificado
     */
    List<Appointment> findByStatus(AppointmentStatus status);

    /**
     * Verifica se existe conflito de horário para um profissional.
     * 
     * @param professionalId ID do profissional
     * @param appointmentTime horário desejado
     * @return true se houver conflito
     */
    boolean hasConflictingAppointment(ProfessionalId professionalId, AppointmentTime appointmentTime);

    /**
     * Verifica se existe conflito de horário excluindo uma consulta específica.
     * 
     * @param professionalId ID do profissional
     * @param appointmentTime horário desejado
     * @param excludeAppointmentId ID da consulta a ser excluída da verificação
     * @return true se houver conflito
     */
    boolean hasConflictingAppointmentExcluding(
        ProfessionalId professionalId, 
        AppointmentTime appointmentTime, 
        AppointmentId excludeAppointmentId
    );

    /**
     * Busca consultas ativas (não canceladas) por profissional e data.
     * 
     * @param professionalId ID do profissional
     * @param date data das consultas
     * @return lista de consultas ativas
     */
    List<Appointment> findActiveAppointmentsByProfessionalAndDate(
        ProfessionalId professionalId, 
        LocalDate date
    );

    /**
     * Remove uma consulta do repositório.
     * 
     * @param appointmentId ID da consulta a ser removida
     */
    void deleteById(AppointmentId appointmentId);

    /**
     * Verifica se uma consulta existe.
     * 
     * @param appointmentId ID da consulta
     * @return true se a consulta existir
     */
    boolean existsById(AppointmentId appointmentId);

    /**
     * Conta o número de consultas por status.
     * 
     * @param status status da consulta
     * @return número de consultas com o status especificado
     */
    long countByStatus(AppointmentStatus status);

    /**
     * Busca próximas consultas de um paciente.
     * 
     * @param patientId ID do paciente
     * @param limit limite de resultados
     * @return lista das próximas consultas
     */
    List<Appointment> findUpcomingAppointmentsByPatient(PatientId patientId, int limit);
}
