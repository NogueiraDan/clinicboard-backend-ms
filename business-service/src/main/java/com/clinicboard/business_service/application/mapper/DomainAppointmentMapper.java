package com.clinicboard.business_service.application.mapper;

import com.clinicboard.business_service.application.dto.AppointmentRequestDto;
import com.clinicboard.business_service.application.dto.AppointmentResponseDto;
import com.clinicboard.business_service.domain.model.enums.AppointmentType;
import com.clinicboard.business_service.domain.model.Appointment;
import com.clinicboard.business_service.domain.model.valueobjects.AppointmentTime;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre DTOs e agregados de Appointment
 */
@Component
public class DomainAppointmentMapper {

    public Appointment toEntity(AppointmentRequestDto dto) {
        if (dto == null) {
            return null;
        }

        AppointmentTime appointmentTime = new AppointmentTime(dto.getDate());
        ProfessionalId professionalId = new ProfessionalId(dto.getProfessionalId());
        
        return new Appointment(
            appointmentTime,
            professionalId,
            dto.getPatientId(),
            dto.getObservation(),
            AppointmentType.MARCACAO
        );
    }

    public AppointmentResponseDto toDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        AppointmentResponseDto dto = new AppointmentResponseDto();
        dto.setId(appointment.getId());
        dto.setDate(appointment.getDate());
        dto.setStatus(appointment.getStatus());
        dto.setProfessionalId(appointment.getProfessionalIdValue());
        dto.setPatientId(appointment.getPatientId());
        dto.setObservation(appointment.getObservation());
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setUpdatedAt(appointment.getUpdatedAt());
        
        return dto;
    }
}
