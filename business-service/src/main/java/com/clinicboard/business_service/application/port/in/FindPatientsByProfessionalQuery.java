package com.clinicboard.business_service.application.port.in;

import com.clinicboard.business_service.domain.model.PatientStatus;

import java.util.List;

/**
 * Porta de entrada para consultas relacionadas a pacientes.
 * 
 * Define operações de leitura para pacientes, seguindo o padrão CQRS
 * onde commands e queries são separados.
 */
public interface FindPatientsByProfessionalQuery {

    /**
     * Busca todos os pacientes ativos de um profissional.
     */
    FindPatientsByProfessionalResponse findActivePatientsByProfessional(FindPatientsByProfessionalRequest request);

    /**
     * Busca pacientes de um profissional por status.
     */
    FindPatientsByProfessionalResponse findPatientsByProfessionalAndStatus(
            FindPatientsByProfessionalAndStatusRequest request);

    /**
     * Conta pacientes de um profissional.
     */
    CountPatientsByProfessionalResponse countPatientsByProfessional(CountPatientsByProfessionalRequest request);

    // Records para request/response
    record FindPatientsByProfessionalRequest(String professionalId) {}
    
    record FindPatientsByProfessionalAndStatusRequest(String professionalId, PatientStatus status) {}
    
    record CountPatientsByProfessionalRequest(String professionalId) {}

    record FindPatientsByProfessionalResponse(
            String professionalId,
            List<PatientSummary> patients,
            int totalPatients
    ) {}

    record CountPatientsByProfessionalResponse(
            String professionalId,
            long totalActive,
            long totalInactive,
            long totalSuspended,
            long totalBlocked
    ) {}

    record PatientSummary(
            String patientId,
            String name,
            String email,
            String contact,
            PatientStatus status,
            String createdAt,
            String updatedAt
    ) {}
}
