package com.clinicboard.business_service.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.clinicboard.business_service.application.dto.PatientRequestDto;
import com.clinicboard.business_service.application.dto.PatientResponseDto;
import com.clinicboard.business_service.domain.entity.Patient;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PatientMapper {

    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    @Mapping(target = "id", ignore = true)
    Patient toEntity(PatientRequestDto patientRequestDto);

    PatientResponseDto toDto(Patient patient);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "professionalId", ignore = true)
    void updateEntity(PatientRequestDto patientRequestDto, @MappingTarget Patient patient);
}
