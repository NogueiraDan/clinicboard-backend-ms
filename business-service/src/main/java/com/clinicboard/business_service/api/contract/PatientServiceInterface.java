package com.clinicboard.business_service.api.contract;

import java.util.List;

import com.clinicboard.business_service.application.dto.PatientRequestDto;
import com.clinicboard.business_service.application.dto.PatientResponseDto;
import com.clinicboard.business_service.domain.repository.PatientRepository;

public interface PatientServiceInterface {

    PatientResponseDto save(PatientRequestDto patientRequestDto);

    List<PatientResponseDto> findAll();

    PatientResponseDto findById(String id);

    List<PatientResponseDto> findByProfessionalId(String id);

    List<PatientResponseDto> findByFilter(String param, String value);

    PatientResponseDto update(String id, PatientRequestDto patientRequestDto);

    void delete(String id);

    PatientRepository getPatientRepository();
}
