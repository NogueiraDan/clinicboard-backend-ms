package com.clinicboard.business_service.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.clinicboard.business_service.infrastructure.persistence.entity.AppointmentEntity;

/**
 * Repositório JPA Spring para entidades de Agendamento.
 * Esta interface será implementada automaticamente pelo Spring Data JPA.
 */
public interface SpringAppointmentRepository extends JpaRepository<AppointmentEntity, String> {

        @Query(value = "SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM agendamentos a " +
                        "WHERE a.id_profissional = :professionalId AND a.data > :startRange AND a.data < :endRange", nativeQuery = true)
        boolean existsByProfessionalIdAndDate(
                        @Param("professionalId") String professionalId,
                        @Param("startRange") LocalDateTime startRange,
                        @Param("endRange") LocalDateTime endRange);

        @Query(value = "SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM agendamentos a " +
                        "WHERE a.data > :startRange AND a.data < :endRange", nativeQuery = true)
        boolean existsByDate(
                        @Param("startRange") LocalDateTime date,
                        @Param("endRange") LocalDateTime endRange);

        @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM agendamentos WHERE id_paciente = :patientId AND DATE(data) = CAST(:date AS DATE)", nativeQuery = true)
        boolean existsByPatientIdAndDate(@Param("date") LocalDateTime date, @Param("patientId") String patientId);

        @Query(value = "SELECT * FROM agendamentos WHERE LOWER(status) LIKE :value AND id_profissional = :id", nativeQuery = true)
        List<AppointmentEntity> findByStatus(@Param("id") String id, @Param("value") String value);

        @Query(value = "SELECT * FROM agendamentos WHERE id_profissional = :id AND DATE(data) = CAST(:date AS DATE)", nativeQuery = true)
        List<AppointmentEntity> findByDate(@Param("id") String id, @Param("date") String date);

        @Query(value = "SELECT * FROM agendamentos WHERE id_profissional = :professionalId", nativeQuery = true)
        List<AppointmentEntity> findByProfessionalId(@Param("professionalId") String professionalId);

        @Query(value = "SELECT * FROM agendamentos WHERE id_paciente = :patientId", nativeQuery = true)
        List<AppointmentEntity> findByPatientId(@Param("patientId") String patientId);

}
