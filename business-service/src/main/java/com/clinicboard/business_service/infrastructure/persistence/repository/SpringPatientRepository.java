package com.clinicboard.business_service.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.clinicboard.business_service.infrastructure.persistence.entity.PatientEntity;

/**
 * Repositório JPA Spring para entidades de Paciente.
 * Esta interface será implementada automaticamente pelo Spring Data JPA.
 */
public interface SpringPatientRepository extends JpaRepository<PatientEntity, String> {

    // Spring Data JPA vai gerar automaticamente a query correta
    List<PatientEntity> findByNameContainingIgnoreCase(String name);

    List<PatientEntity> findByEmailContainingIgnoreCase(String email);

    List<PatientEntity> findByContactContainingIgnoreCase(String contact);

    List<PatientEntity> findByProfessionalId(String professionalId);

    Optional<PatientEntity> findByEmail(String email);
}
