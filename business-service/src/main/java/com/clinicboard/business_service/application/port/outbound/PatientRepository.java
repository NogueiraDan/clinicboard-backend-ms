package com.clinicboard.business_service.application.port.outbound;

import com.clinicboard.business_service.domain.model.Patient;

import java.util.List;
import java.util.Optional;

/**
 * Porta de saída para persistência de pacientes
 */
public interface PatientRepository {
    
    Patient save(Patient patient);
    
    Optional<Patient> findById(String id);
    
    Optional<Patient> findByEmail(String email);
    
    List<Patient> findAll();
    
    List<Patient> findByProfessionalId(String professionalId);
    
    void deleteById(String id);
    
    boolean existsById(String id);
    
    boolean existsByEmail(String email);
}
