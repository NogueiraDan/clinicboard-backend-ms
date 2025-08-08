package com.clinicboard.business_service.application.mapper;

import com.clinicboard.business_service.application.dto.PatientRequestDto;
import com.clinicboard.business_service.application.dto.PatientResponseDto;
import com.clinicboard.business_service.domain.model.Patient;
import com.clinicboard.business_service.domain.model.valueobjects.Contact;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre DTOs e agregados de Patient
 */
@Component
public class DomainPatientMapper {

    public Patient toEntity(PatientRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Contact contact = new Contact(dto.getContact(), dto.getEmail());
        ProfessionalId professionalId = new ProfessionalId(dto.getProfessionalId());
        
        return new Patient(dto.getName(), contact, professionalId);
    }

    public PatientResponseDto toDto(Patient patient) {
        if (patient == null) {
            return null;
        }

        PatientResponseDto dto = new PatientResponseDto();
        dto.setId(patient.getId());
        dto.setName(patient.getName());
        dto.setEmail(patient.getEmail());
        dto.setContact(patient.getContactPhone());
        dto.setProfessionalId(patient.getProfessionalIdValue());
        
        return dto;
    }
}
