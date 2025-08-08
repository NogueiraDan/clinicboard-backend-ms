package com.clinicboard.business_service.infrastructure.persistence;

import com.clinicboard.business_service.application.port.outbound.PatientRepository;
import com.clinicboard.business_service.domain.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA para o repositório de pacientes
 */
@Repository
public interface JpaPatientRepository extends JpaRepository<Patient, String>, PatientRepository {

    @Query("SELECT p FROM patients p WHERE p.contact.email = :email")
    @Override
    Optional<Patient> findByEmail(@Param("email") String email);

    @Query("SELECT p FROM patients p WHERE p.professionalId.value = :professionalId")
    @Override
    List<Patient> findByProfessionalId(@Param("professionalId") String professionalId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM patients p WHERE p.contact.email = :email")
    @Override
    boolean existsByEmail(@Param("email") String email);
}
