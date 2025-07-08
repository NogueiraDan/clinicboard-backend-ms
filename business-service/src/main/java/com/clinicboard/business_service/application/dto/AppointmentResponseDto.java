package com.clinicboard.business_service.application.dto;

import java.time.LocalDateTime;

import com.clinicboard.business_service.domain.entity.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {

    private String id;

    private LocalDateTime date;

    private AppointmentStatus status;

    private String professionalId;

    private String patientId;

    private String observation;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
