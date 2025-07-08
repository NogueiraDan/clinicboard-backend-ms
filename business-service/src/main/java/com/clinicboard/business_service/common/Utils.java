package com.clinicboard.business_service.common;

import com.clinicboard.business_service.application.dto.AppointmentResponseDto;
import com.clinicboard.business_service.domain.entity.Appointment;

public class Utils {

    public static AppointmentResponseDto convertToAppointmentResponseDto(Appointment appointment) {
        if (appointment != null) {
            AppointmentResponseDto dto = new AppointmentResponseDto();
            dto.setId(appointment.getId());
            dto.setDate(appointment.getDate());
            dto.setStatus(appointment.getStatus());
            dto.setProfessionalId(appointment.getProfessionalId());
            dto.setPatientId(appointment.getPatientId());
            dto.setObservation(appointment.getObservation());
            dto.setCreatedAt(appointment.getCreatedAt());
            dto.setUpdatedAt(appointment.getUpdatedAt());
            return dto;
        }
        return null;
    }

}