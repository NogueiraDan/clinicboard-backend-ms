package com.clinicboard.business_service.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.clinicboard.business_service.domain.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, String> {

    // Spring Data JPA vai gerar automaticamente a query correta
    List<Patient> findByNameContainingIgnoreCase(String name);

    List<Patient> findByEmailContainingIgnoreCase(String email);

    List<Patient> findByContactContainingIgnoreCase(String contact);

    List<Patient> findByProfessionalId(String professionalId);

    Optional<Patient> findByEmail(String email);
}