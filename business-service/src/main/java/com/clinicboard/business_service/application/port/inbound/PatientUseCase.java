package com.clinicboard.business_service.application.port.inbound;

import com.clinicboard.business_service.application.dto.PatientRequestDto;
import com.clinicboard.business_service.application.dto.PatientResponseDto;

import java.util.List;

/**
 * Porta de entrada para operações de paciente
 */
public interface PatientUseCase {
    
    PatientResponseDto registerPatient(PatientRequestDto request);
    
    PatientResponseDto updatePatient(String patientId, PatientRequestDto request);
    
    void removePatient(String patientId);
    
    PatientResponseDto findPatientById(String patientId);
    
    List<PatientResponseDto> findAllPatients();
    
    List<PatientResponseDto> findPatientsByProfessional(String professionalId);
    
    List<PatientResponseDto> findPatientsByFilter(String param, String value);
}
