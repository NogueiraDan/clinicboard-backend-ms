package com.clinicboard.business_service.infrastructure.adapter.out.persistence;
import lombok.extern.slf4j.Slf4j;

import com.clinicboard.business_service.application.port.out.PatientRepository;
import com.clinicboard.business_service.domain.model.Patient;
import com.clinicboard.business_service.domain.model.PatientId;
import com.clinicboard.business_service.domain.model.PatientStatus;
import com.clinicboard.business_service.domain.model.Email;
import com.clinicboard.business_service.infrastructure.adapter.out.persistence.entity.PatientJpaEntity;
import com.clinicboard.business_service.infrastructure.adapter.out.persistence.repository.PatientJpaRepository;
import com.clinicboard.business_service.infrastructure.adapter.out.persistence.mapper.PatientPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador de persistência para Patient.
 * 
 * Implementa a porta de saída PatientRepository usando JPA,
 * fazendo a bridge entre o domínio e a infraestrutura de dados.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PatientPersistenceAdapter implements PatientRepository {

    private final PatientJpaRepository patientJpaRepository;
    private final PatientPersistenceMapper patientMapper;

    @Override
    public Patient save(Patient patient) {
        log.debug("Salvando paciente: {}", patient.getId());
        
        try {
            PatientJpaEntity jpaEntity;
            
            if (patient.getId() == null) {
                // Novo paciente - gerar ID
                jpaEntity = patientMapper.toJpaEntity(patient);
                jpaEntity.setPatientId(UUID.randomUUID().toString());
            } else {
                // Paciente existente - buscar e atualizar
                Optional<PatientJpaEntity> existingEntity = patientJpaRepository.findById(patient.getId().value());
                
                if (existingEntity.isPresent()) {
                    jpaEntity = existingEntity.get();
                    patientMapper.updateJpaEntity(jpaEntity, patient);
                } else {
                    // Paciente com ID mas não existe no banco
                    jpaEntity = patientMapper.toJpaEntity(patient);
                }
            }
            
            PatientJpaEntity savedEntity = patientJpaRepository.save(jpaEntity);
            Patient savedPatient = patientMapper.toDomainEntity(savedEntity);
            
            log.info("Paciente salvo com sucesso: id={}", savedEntity.getPatientId());
            return savedPatient;
            
        } catch (Exception e) {
            log.error("Erro ao salvar paciente: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao persistir paciente", e);
        }
    }

    @Override
    public Optional<Patient> findById(PatientId patientId) {
        log.debug("Buscando paciente por ID: {}", patientId.value());
        
        try {
            return patientJpaRepository.findActivePatientById(patientId.value())
                    .map(patientMapper::toDomainEntity);
                    
        } catch (Exception e) {
            log.error("Erro ao buscar paciente por ID {}: {}", patientId.value(), e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Patient> findByEmail(Email email) {
        log.debug("Buscando paciente por email: {}", email.value());
        
        try {
            return patientJpaRepository.findByEmailAndActiveTrue(email.value())
                    .map(patientMapper::toDomainEntity);
                    
        } catch (Exception e) {
            log.error("Erro ao buscar paciente por email {}: {}", email.value(), e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<Patient> findByNameContaining(String name) {
        log.debug("Buscando pacientes por nome: {}", name);
        
        try {
            // TODO: Implementar query no repository
            return patientJpaRepository.findAll()
                    .stream()
                    .filter(entity -> entity.getName().toLowerCase().contains(name.toLowerCase()))
                    .filter(PatientJpaEntity::getActive)
                    .map(patientMapper::toDomainEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Erro ao buscar pacientes por nome {}: {}", name, e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Patient> findByStatus(PatientStatus status) {
        log.debug("Buscando pacientes por status: {}", status);
        
        try {
            boolean isActive = status == PatientStatus.ACTIVE;
            return patientJpaRepository.findAll()
                    .stream()
                    .filter(entity -> entity.getActive() == isActive)
                    .map(patientMapper::toDomainEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Erro ao buscar pacientes por status {}: {}", status, e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Patient> findActivePatients() {
        log.debug("Buscando pacientes ativos");
        
        try {
            return patientJpaRepository.findAll()
                    .stream()
                    .filter(PatientJpaEntity::getActive)
                    .map(patientMapper::toDomainEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Erro ao buscar pacientes ativos: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public boolean existsByEmail(Email email) {
        log.debug("Verificando existência de paciente por email: {}", email.value());
        
        try {
            return patientJpaRepository.existsByEmailAndActiveTrue(email.value());
            
        } catch (Exception e) {
            log.error("Erro ao verificar existência por email {}: {}", email.value(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean existsByEmailAndIdNot(Email email, PatientId excludePatientId) {
        log.debug("Verificando existência de email {} excluindo paciente {}", email.value(), excludePatientId.value());
        
        try {
            return patientJpaRepository.findByEmailAndActiveTrue(email.value())
                    .map(entity -> !entity.getPatientId().equals(excludePatientId.value()))
                    .orElse(false);
                    
        } catch (Exception e) {
            log.error("Erro ao verificar existência por email excluindo ID: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean existsById(PatientId patientId) {
        log.debug("Verificando existência de paciente: {}", patientId.value());
        
        try {
            return patientJpaRepository.findActivePatientById(patientId.value()).isPresent();
            
        } catch (Exception e) {
            log.error("Erro ao verificar existência por ID {}: {}", patientId.value(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void deleteById(PatientId patientId) {
        log.debug("Desativando paciente: {}", patientId.value());
        
        try {
            Optional<PatientJpaEntity> patientEntity = patientJpaRepository.findById(patientId.value());
            
            if (patientEntity.isPresent()) {
                PatientJpaEntity entity = patientEntity.get();
                entity.setActive(Boolean.FALSE);
                patientJpaRepository.save(entity);
                
                log.info("Paciente desativado com sucesso: id={}", patientId.value());
            } else {
                log.warn("Tentativa de desativar paciente inexistente: {}", patientId.value());
            }
            
        } catch (Exception e) {
            log.error("Erro ao desativar paciente {}: {}", patientId.value(), e.getMessage(), e);
            throw new RuntimeException("Falha ao desativar paciente", e);
        }
    }

    @Override
    public long count() {
        log.debug("Contando pacientes");
        
        try {
            return patientJpaRepository.countActivePatients();
            
        } catch (Exception e) {
            log.error("Erro ao contar pacientes: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public long countByStatus(PatientStatus status) {
        log.debug("Contando pacientes por status: {}", status);
        
        try {
            boolean isActive = status == PatientStatus.ACTIVE;
            return patientJpaRepository.findAll()
                    .stream()
                    .filter(entity -> entity.getActive() == isActive)
                    .count();
                    
        } catch (Exception e) {
            log.error("Erro ao contar pacientes por status {}: {}", status, e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public List<Patient> findRecentPatients(int limit) {
        log.debug("Buscando {} pacientes recentes", limit);
        
        try {
            Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
            return patientJpaRepository.findAll(pageable)
                    .getContent()
                    .stream()
                    .filter(PatientJpaEntity::getActive)
                    .map(patientMapper::toDomainEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Erro ao buscar pacientes recentes: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Patient> findAll(int page, int size) {
        log.debug("Buscando pacientes - página {} tamanho {}", page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
            return patientJpaRepository.findAll(pageable)
                    .getContent()
                    .stream()
                    .filter(PatientJpaEntity::getActive)
                    .map(patientMapper::toDomainEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Erro ao buscar pacientes paginados: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
