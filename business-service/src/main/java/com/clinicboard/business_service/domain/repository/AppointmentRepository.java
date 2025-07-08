package com.clinicboard.business_service.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.clinicboard.business_service.domain.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, String> {

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
        List<Appointment> findByStatus(@Param("id") String id, @Param("value") String value);

        @Query(value = "SELECT * FROM agendamentos WHERE id_profissional = :id AND DATE(data) = CAST(:date AS DATE)", nativeQuery = true)
        List<Appointment> findByDate(@Param("id") String id,
                        @Param("date") String date);

}
