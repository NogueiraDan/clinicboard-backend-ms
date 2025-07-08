package com.clinicboard.business_service.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.clinicboard.business_service.domain.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, String> {

    @Query("SELECT p FROM patients p WHERE p.professionalId = :professionalId")
    List<Patient> findByProfessionalId(String professionalId);

    @Query(value = "SELECT * FROM patients WHERE LOWER(nome) LIKE :value", nativeQuery = true)
    List<Patient> findByName(@Param("value") String value);

    // @Query(value = "SELECT * FROM patients WHERE LOWER(email) LIKE :value", nativeQuery = true)
    // List<Patient> findByEmail(@Param("value") String value);

    Optional<Patient> findByEmail(String email);

    @Query(value = "SELECT * FROM patients WHERE LOWER(contato) LIKE :value", nativeQuery = true)
    List<Patient> findByContact(@Param("value") String value);
}