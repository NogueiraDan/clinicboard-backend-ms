package com.clinicboard.business_service.domain.port;

import com.clinicboard.business_service.domain.model.Patient;
import java.util.List;
import java.util.Optional;

/**
 * Port (interface) para operações de persistência de Pacientes.
 * Define o contrato que a camada de domínio espera da infraestrutura.
 */
public interface PatientRepositoryPort {

    /**
     * Salva um agregado de Paciente
     */
    Patient save(Patient patient);

    /**
     * Busca um paciente por ID
     */
    Optional<Patient> findById(String id);

    /**
     * Lista todos os pacientes
     */
    List<Patient> findAll();

    /**
     * Remove um paciente
     */
    void deleteById(String id);

    /**
     * Busca pacientes por nome (contendo, ignorando case)
     */
    List<Patient> findByNameContainingIgnoreCase(String name);

    /**
     * Busca pacientes por email (contendo, ignorando case)
     */
    List<Patient> findByEmailContainingIgnoreCase(String email);

    /**
     * Busca pacientes por contato (contendo, ignorando case)
     */
    List<Patient> findByContactContainingIgnoreCase(String contact);

    /**
     * Busca pacientes por ID do profissional
     */
    List<Patient> findByProfessionalId(String professionalId);

    /**
     * Busca paciente por email
     */
    Optional<Patient> findByEmail(String email);
}
