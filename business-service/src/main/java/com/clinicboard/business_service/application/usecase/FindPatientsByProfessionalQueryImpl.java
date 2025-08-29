package com.clinicboard.business_service.application.usecase;

import com.clinicboard.business_service.application.port.in.FindPatientsByProfessionalQuery;
import com.clinicboard.business_service.application.port.out.PatientRepository;
import com.clinicboard.business_service.domain.model.Patient;
import com.clinicboard.business_service.domain.model.ProfessionalId;
import com.clinicboard.business_service.domain.model.PatientStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Implementação do caso de uso para buscar pacientes por profissional.
 * 
 * Orquestra consultas relacionadas aos pacientes de um profissional específico,
 * seguindo os princípios DDD e CQRS.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FindPatientsByProfessionalQueryImpl implements FindPatientsByProfessionalQuery {

    private final PatientRepository patientRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public FindPatientsByProfessionalResponse findActivePatientsByProfessional(FindPatientsByProfessionalRequest request) {
        Objects.requireNonNull(request, "FindPatientsByProfessionalRequest não pode ser nulo");
        Objects.requireNonNull(request.professionalId(), "ID do profissional não pode ser nulo");
        
        log.info("Buscando pacientes ativos do profissional: {}", request.professionalId());
        
        try {
            ProfessionalId professionalId = ProfessionalId.of(request.professionalId());
            List<Patient> patients = patientRepository.findByProfessionalId(professionalId);
            
            List<PatientSummary> patientSummaries = patients.stream()
                    .filter(Patient::isActive)
                    .map(this::toPatientSummary)
                    .toList();
            
            log.info("Encontrados {} pacientes ativos para o profissional {}", 
                    patientSummaries.size(), request.professionalId());
            
            return new FindPatientsByProfessionalResponse(
                    request.professionalId(),
                    patientSummaries,
                    patientSummaries.size()
            );
            
        } catch (Exception e) {
            log.error("Erro ao buscar pacientes do profissional {}: {}", 
                     request.professionalId(), e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar pacientes", e);
        }
    }

    @Override
    public FindPatientsByProfessionalResponse findPatientsByProfessionalAndStatus(
            FindPatientsByProfessionalAndStatusRequest request) {
        Objects.requireNonNull(request, "FindPatientsByProfessionalAndStatusRequest não pode ser nulo");
        Objects.requireNonNull(request.professionalId(), "ID do profissional não pode ser nulo");
        Objects.requireNonNull(request.status(), "Status não pode ser nulo");
        
        log.info("Buscando pacientes do profissional {} com status {}", 
                request.professionalId(), request.status());
        
        try {
            ProfessionalId professionalId = ProfessionalId.of(request.professionalId());
            List<Patient> patients = patientRepository.findByProfessionalIdAndStatus(
                    professionalId, request.status());
            
            List<PatientSummary> patientSummaries = patients.stream()
                    .map(this::toPatientSummary)
                    .toList();
            
            log.info("Encontrados {} pacientes com status {} para o profissional {}", 
                    patientSummaries.size(), request.status(), request.professionalId());
            
            return new FindPatientsByProfessionalResponse(
                    request.professionalId(),
                    patientSummaries,
                    patientSummaries.size()
            );
            
        } catch (Exception e) {
            log.error("Erro ao buscar pacientes do profissional {} com status {}: {}", 
                     request.professionalId(), request.status(), e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar pacientes por status", e);
        }
    }

    @Override
    public CountPatientsByProfessionalResponse countPatientsByProfessional(CountPatientsByProfessionalRequest request) {
        Objects.requireNonNull(request, "CountPatientsByProfessionalRequest não pode ser nulo");
        Objects.requireNonNull(request.professionalId(), "ID do profissional não pode ser nulo");
        
        log.info("Contando pacientes do profissional: {}", request.professionalId());
        
        try {
            ProfessionalId professionalId = ProfessionalId.of(request.professionalId());
            
            long totalActive = patientRepository.findByProfessionalIdAndStatus(
                    professionalId, PatientStatus.ACTIVE).size();
            long totalInactive = patientRepository.findByProfessionalIdAndStatus(
                    professionalId, PatientStatus.INACTIVE).size();
            long totalSuspended = patientRepository.findByProfessionalIdAndStatus(
                    professionalId, PatientStatus.SUSPENDED).size();
            long totalBlocked = patientRepository.findByProfessionalIdAndStatus(
                    professionalId, PatientStatus.BLOCKED).size();
            
            log.info("Contagem de pacientes do profissional {}: Ativos={}, Inativos={}, Suspensos={}, Bloqueados={}", 
                    request.professionalId(), totalActive, totalInactive, totalSuspended, totalBlocked);
            
            return new CountPatientsByProfessionalResponse(
                    request.professionalId(),
                    totalActive,
                    totalInactive,
                    totalSuspended,
                    totalBlocked
            );
            
        } catch (Exception e) {
            log.error("Erro ao contar pacientes do profissional {}: {}", 
                     request.professionalId(), e.getMessage(), e);
            throw new RuntimeException("Erro ao contar pacientes", e);
        }
    }

    private PatientSummary toPatientSummary(Patient patient) {
        return new PatientSummary(
                patient.getId() != null ? patient.getId().value() : null,
                patient.getName(),
                patient.getEmail().value(),
                patient.getContact().value(),
                patient.getStatus(),
                patient.getCreatedAt() != null ? patient.getCreatedAt().format(DATE_FORMATTER) : null,
                patient.getUpdatedAt() != null ? patient.getUpdatedAt().format(DATE_FORMATTER) : null
        );
    }
}
