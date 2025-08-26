package com.clinicboard.business_service.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Request para agendamento de consulta")
public record ScheduleAppointmentRequestDto(
        
        @Schema(description = "ID do paciente", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotBlank(message = "ID do paciente é obrigatório")
        String patientId,
        
        @Schema(description = "Nome do paciente", example = "João Silva")
        @NotBlank(message = "Nome do paciente é obrigatório")
        String patientName,
        
        @Schema(description = "Data e hora da consulta", example = "2024-12-25T10:30:00")
        @NotNull(message = "Data e hora da consulta são obrigatórias")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime scheduledTime,
        
        @Schema(description = "Observações da consulta", example = "Consulta de rotina")
        String notes
) {
    public ScheduleAppointmentRequestDto {
        // Validações adicionais podem ser implementadas aqui se necessário
        if (patientId != null) {
            patientId = patientId.trim();
        }
        if (patientName != null) {
            patientName = patientName.trim();
        }
        if (notes != null) {
            notes = notes.trim();
        }
    }
}
