package com.clinicboard.business_service.infrastructure.adapter.out.persistence.repository;

import com.clinicboard.business_service.infrastructure.adapter.out.persistence.entity.AppointmentJpaEntity;
import com.clinicboard.business_service.infrastructure.adapter.out.persistence.entity.AppointmentJpaEntity.AppointmentStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para operações de persistência de consultas.
 * 
 * Define consultas customizadas para suporte às regras de negócio
 * como verificação de conflitos de horário e consultas por paciente.
 */
@Repository
public interface AppointmentJpaRepository extends JpaRepository<AppointmentJpaEntity, String> {
    
    /**
     * Busca consulta ativa (não cancelada) por ID.
     */
    @Query("SELECT a FROM AppointmentJpaEntity a WHERE a.appointmentId = :appointmentId AND a.status != 'CANCELLED'")
    Optional<AppointmentJpaEntity> findActiveAppointmentById(@Param("appointmentId") String appointmentId);
    
    /**
     * Busca consultas agendadas para um paciente específico.
     */
    @Query("SELECT a FROM AppointmentJpaEntity a WHERE a.patientId = :patientId AND a.status IN ('SCHEDULED', 'CONFIRMED') ORDER BY a.scheduledTime")
    List<AppointmentJpaEntity> findScheduledAppointmentsByPatient(@Param("patientId") String patientId);
    
    /**
     * Verifica conflitos de horário para um profissional em um período específico.
     * A lógica de sobreposição de horários é calculada no código Java.
     */
    @Query("""
        SELECT a FROM AppointmentJpaEntity a 
        WHERE a.professionalId = :professionalId 
        AND a.status IN ('SCHEDULED', 'CONFIRMED', 'IN_PROGRESS')
        AND (
            (a.scheduledTime BETWEEN :startTime AND :endTime)
            OR (a.scheduledTime <= :scheduledTime AND :scheduledTime <= :endTime)
        )
        """)
    List<AppointmentJpaEntity> findConflictingAppointments(
        @Param("professionalId") String professionalId,
        @Param("scheduledTime") LocalDateTime scheduledTime,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * Busca consultas por período e status.
     */
    @Query("SELECT a FROM AppointmentJpaEntity a WHERE a.scheduledTime BETWEEN :startDate AND :endDate AND a.status = :status ORDER BY a.scheduledTime")
    List<AppointmentJpaEntity> findAppointmentsByPeriodAndStatus(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("status") AppointmentStatusEnum status
    );
    
    /**
     * Conta consultas agendadas para um paciente em um período.
     * Útil para aplicar regras de limite de consultas por período.
     */
    @Query("""
        SELECT COUNT(a) FROM AppointmentJpaEntity a 
        WHERE a.patientId = :patientId 
        AND a.scheduledTime BETWEEN :startDate AND :endDate 
        AND a.status IN ('SCHEDULED', 'CONFIRMED')
        """)
    long countScheduledAppointmentsByPatientInPeriod(
        @Param("patientId") String patientId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
