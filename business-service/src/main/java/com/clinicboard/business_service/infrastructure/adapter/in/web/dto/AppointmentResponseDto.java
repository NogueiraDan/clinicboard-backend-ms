package com.clinicboard.business_service.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response com informações da consulta")
public record AppointmentResponseDto(
        
        @Schema(description = "ID da consulta", example = "550e8400-e29b-41d4-a716-446655440001")
        String appointmentId,
        
        @Schema(description = "ID do paciente", example = "550e8400-e29b-41d4-a716-446655440000")
        String patientId,
        
        @Schema(description = "Nome do paciente", example = "João Silva")
        String patientName,
        
        @Schema(description = "Data e hora da consulta", example = "2024-12-25T10:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime scheduledTime,
        
        @Schema(description = "Status da consulta", example = "SCHEDULED")
        String status,
        
        @Schema(description = "Observações da consulta", example = "Consulta de rotina")
        String notes,
        
        @Schema(description = "Data de criação", example = "2024-12-20T14:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt
) {
}
