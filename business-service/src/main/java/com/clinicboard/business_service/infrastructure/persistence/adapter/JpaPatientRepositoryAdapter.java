package com.clinicboard.business_service.infrastructure.persistence.adapter;

import com.clinicboard.business_service.domain.model.Patient;
import com.clinicboard.business_service.domain.port.PatientRepositoryPort;
import com.clinicboard.business_service.infrastructure.persistence.entity.PatientEntity;
import com.clinicboard.business_service.infrastructure.persistence.mapper.PatientEntityMapper;
import com.clinicboard.business_service.infrastructure.persistence.repository.SpringPatientRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa a Port de repositório de Paciente usando infraestrutura JPA.
 * Esta classe faz a ponte entre o domínio puro e a tecnologia de persistência.
 */
@Repository
public class JpaPatientRepositoryAdapter implements PatientRepositoryPort {

    private final SpringPatientRepository springRepository;
    private final PatientEntityMapper mapper;

    public JpaPatientRepositoryAdapter(SpringPatientRepository springRepository, 
                                     PatientEntityMapper mapper) {
        this.springRepository = springRepository;
        this.mapper = mapper;
    }

    @Override
    public Patient save(Patient patient) {
        PatientEntity entity = mapper.toEntity(patient);
        PatientEntity savedEntity = springRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Patient> findById(String id) {
        return springRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Patient> findAll() {
        return springRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        springRepository.deleteById(id);
    }

    @Override
    public List<Patient> findByNameContainingIgnoreCase(String name) {
        return springRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> findByEmailContainingIgnoreCase(String email) {
        return springRepository.findByEmailContainingIgnoreCase(email)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> findByContactContainingIgnoreCase(String contact) {
        return springRepository.findByContactContainingIgnoreCase(contact)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> findByProfessionalId(String professionalId) {
        return springRepository.findByProfessionalId(professionalId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Patient> findByEmail(String email) {
        return springRepository.findByEmail(email)
                .map(mapper::toDomain);
    }
}
