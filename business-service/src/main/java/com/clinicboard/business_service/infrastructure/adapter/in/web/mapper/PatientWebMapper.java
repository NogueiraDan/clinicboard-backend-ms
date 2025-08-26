package com.clinicboard.business_service.infrastructure.adapter.in.web.mapper;

import com.clinicboard.business_service.application.port.in.ManagePatientCommand.CreatePatientRequest;
import com.clinicboard.business_service.application.port.in.ManagePatientCommand.CreatePatientResponse;
import com.clinicboard.business_service.infrastructure.adapter.in.web.dto.ManagePatientRequestDto;
import com.clinicboard.business_service.infrastructure.adapter.in.web.dto.PatientResponseDto;
import com.clinicboard.business_service.domain.model.PatientName;
import com.clinicboard.business_service.domain.model.Email;
import com.clinicboard.business_service.domain.model.ContactDetails;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper para conversão entre DTOs de paciente e objetos da camada de aplicação.
 * 
 * Mantém o isolamento do domínio através de conversões controladas.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PatientWebMapper {
    
    /**
     * Converte DTO de requisição REST para comando da camada de aplicação.
     */
    default CreatePatientRequest toApplicationRequest(ManagePatientRequestDto dto) {
        return new CreatePatientRequest(
            PatientName.of(dto.name()),
            Email.of(dto.email()),
            ContactDetails.of(dto.phone())
        );
    }
    
    /**
     * Converte resposta da camada de aplicação para DTO de resposta REST.
     */
    default PatientResponseDto toResponseDto(CreatePatientResponse response) {
        return new PatientResponseDto(
            response.patientId().value(),
            response.name().value(),
            response.email().value(),
            response.contactDetails().value()
        );
    }
}
