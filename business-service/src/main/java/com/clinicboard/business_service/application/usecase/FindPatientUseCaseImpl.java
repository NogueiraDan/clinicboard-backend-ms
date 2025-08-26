package com.clinicboard.business_service.application.usecase;

import com.clinicboard.business_service.application.port.in.FindPatientQuery;
import com.clinicboard.business_service.application.port.out.PatientRepository;
import com.clinicboard.business_service.domain.model.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação dos casos de uso de consulta de pacientes.
 * 
 * Responsável por todas as operações de leitura relacionadas
 * a pacientes, seguindo o padrão CQRS.
 * 
 * Princípios DDD aplicados:
 * - Separação clara entre Commands e Queries (CQRS)
 * - Foco em consultas sem efeitos colaterais
 * - Uso de linguagem ubíqua
 */
public class FindPatientUseCaseImpl implements FindPatientQuery {

    private final PatientRepository patientRepository;

    public FindPatientUseCaseImpl(PatientRepository patientRepository) {
        this.patientRepository = Objects.requireNonNull(patientRepository, "PatientRepository cannot be null");
    }

    @Override
    public Optional<PatientView> findById(PatientId patientId) {
        Objects.requireNonNull(patientId, "PatientId cannot be null");
        
        return patientRepository.findById(patientId)
                .map(this::toPatientView);
    }

    @Override
    public Optional<PatientView> findByEmail(Email email) {
        Objects.requireNonNull(email, "Email cannot be null");
        
        return patientRepository.findByEmail(email)
                .map(this::toPatientView);
    }

    @Override
    public List<PatientView> findByNameContaining(String name) {
        Objects.requireNonNull(name, "Name cannot be null");
        
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        
        return patientRepository.findByNameContaining(name)
                .stream()
                .map(this::toPatientView)
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientView> findByStatus(PatientStatus status) {
        Objects.requireNonNull(status, "Status cannot be null");
        
        return patientRepository.findByStatus(status)
                .stream()
                .map(this::toPatientView)
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientView> findActivePatients() {
        return patientRepository.findActivePatients()
                .stream()
                .map(this::toPatientView)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEmailInUse(Email email) {
        Objects.requireNonNull(email, "Email cannot be null");
        
        return patientRepository.existsByEmail(email);
    }

    @Override
    public boolean isEmailInUseByOtherPatient(Email email, PatientId excludePatientId) {
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(excludePatientId, "PatientId cannot be null");
        
        return patientRepository.existsByEmailAndIdNot(email, excludePatientId);
    }

    /**
     * Converte um agregado Patient para PatientView (read model).
     */
    private PatientView toPatientView(Patient patient) {
        return PatientView.of(
                patient.getId(),
                patient.getDomainName(),
                patient.getEmail(),
                patient.getContact(),
                patient.getStatus(),
                patient.getCreatedAt()
        );
    }
}
