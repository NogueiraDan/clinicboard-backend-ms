package com.clinicboard.business_service.infrastructure.adapter.out.persistence.repository;

import com.clinicboard.business_service.infrastructure.adapter.out.persistence.entity.PatientJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório JPA para operações de persistência de pacientes.
 * 
 * Interface que estende JpaRepository para operações CRUD básicas
 * e define consultas customizadas específicas do domínio.
 */
@Repository
public interface PatientJpaRepository extends JpaRepository<PatientJpaEntity, String> {
    
    /**
     * Busca paciente por email.
     * Útil para validar unicidade de email durante criação/atualização.
     */
    Optional<PatientJpaEntity> findByEmailAndActiveTrue(String email);
    
    /**
     * Busca paciente ativo por ID.
     * Garante que apenas pacientes ativos sejam retornados nas consultas.
     */
    @Query("SELECT p FROM PatientJpaEntity p WHERE p.patientId = :patientId AND p.active = true")
    Optional<PatientJpaEntity> findActivePatientById(@Param("patientId") String patientId);
    
    /**
     * Verifica se existe paciente ativo com o email informado.
     */
    boolean existsByEmailAndActiveTrue(String email);
    
    /**
     * Conta total de pacientes ativos no sistema.
     */
    @Query("SELECT COUNT(p) FROM PatientJpaEntity p WHERE p.active = true")
    long countActivePatients();
}
