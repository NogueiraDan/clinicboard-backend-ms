package com.clinicboard.business_service.infrastructure.persistence;

import com.clinicboard.business_service.application.port.outbound.AppointmentRepository;
import com.clinicboard.business_service.domain.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Adaptador JPA para o repositório de agendamentos
 */
@Repository
public interface JpaAppointmentRepository extends JpaRepository<Appointment, String>, AppointmentRepository {

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM appointments a " +
           "WHERE a.professionalId.value = :professionalId " +
           "AND a.appointmentTime.dateTime > :startRange " +
           "AND a.appointmentTime.dateTime < :endRange")
    @Override
    boolean existsByProfessionalAndTimeRange(
        @Param("professionalId") String professionalId,
        @Param("startRange") LocalDateTime startRange,
        @Param("endRange") LocalDateTime endRange);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM appointments a " +
           "WHERE a.appointmentTime.dateTime > :startRange " +
           "AND a.appointmentTime.dateTime < :endRange")
    @Override
    boolean existsByTimeRange(
        @Param("startRange") LocalDateTime startRange,
        @Param("endRange") LocalDateTime endRange);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM appointments a " +
           "WHERE a.patientId = :patientId " +
           "AND DATE(a.appointmentTime.dateTime) = DATE(:date)")
    @Override
    boolean existsByPatientAndDate(
        @Param("patientId") String patientId,
        @Param("date") LocalDateTime date);

    @Query("SELECT a FROM appointments a " +
           "WHERE a.professionalId.value = :professionalId " +
           "AND DATE(a.appointmentTime.dateTime) = CAST(:date AS DATE)")
    @Override
    List<Appointment> findByProfessionalAndDate(
        @Param("professionalId") String professionalId,
        @Param("date") String date);

    @Query("SELECT a FROM appointments a " +
           "WHERE LOWER(a.status) LIKE :status " +
           "AND a.professionalId.value = :professionalId")
    @Override
    List<Appointment> findByStatus(
        @Param("professionalId") String professionalId,
        @Param("status") String status);
}
