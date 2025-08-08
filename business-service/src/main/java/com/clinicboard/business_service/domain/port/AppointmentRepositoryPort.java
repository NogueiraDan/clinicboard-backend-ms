package com.clinicboard.business_service.domain.port;

import com.clinicboard.business_service.domain.model.Appointment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Port (interface) para operações de persistência de Agendamentos.
 * Define o contrato que a camada de domínio espera da infraestrutura.
 */
public interface AppointmentRepositoryPort {

    /**
     * Salva um agregado de Agendamento
     */
    Appointment save(Appointment appointment);

    /**
     * Busca um agendamento por ID
     */
    Optional<Appointment> findById(String id);

    /**
     * Lista todos os agendamentos
     */
    List<Appointment> findAll();

    /**
     * Remove um agendamento
     */
    void deleteById(String id);

    /**
     * Verifica se existe agendamento para profissional em determinado período
     */
    boolean existsByProfessionalIdAndDateRange(String professionalId, LocalDateTime startRange, LocalDateTime endRange);

    /**
     * Verifica se existe agendamento em determinado período
     */
    boolean existsByDateRange(LocalDateTime startRange, LocalDateTime endRange);

    /**
     * Verifica se existe agendamento para paciente em determinada data
     */
    boolean existsByPatientIdAndDate(LocalDateTime date, String patientId);

    /**
     * Busca agendamentos por status e profissional
     */
    List<Appointment> findByStatusAndProfessionalId(String status, String professionalId);

    /**
     * Busca agendamentos por profissional
     */
    List<Appointment> findByProfessionalId(String professionalId);

    /**
     * Busca agendamentos por paciente
     */
    List<Appointment> findByPatientId(String patientId);
}
