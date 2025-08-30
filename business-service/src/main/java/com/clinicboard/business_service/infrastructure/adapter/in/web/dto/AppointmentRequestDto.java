package com.clinicboard.business_service.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO genérico para operações de agendamento.
 * Serve tanto para criação quanto para atualização de appointments.
 * 
 * Seguindo princípios DDD:
 * - DTO é anêmico (apenas transporte de dados)
 * - Validações básicas de entrada
 * - Isolamento entre camadas web e aplicação
 */
@Schema(description = "Request para operações de agendamento")
public record AppointmentRequestDto(
        
        @Schema(description = "ID do paciente", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotBlank(message = "ID do paciente é obrigatório")
        String patientId,
        
        @Schema(description = "ID do profissional", example = "660e8400-e29b-41d4-a716-446655440001")
        @NotBlank(message = "ID do profissional é obrigatório")
        String professionalId,
        
        @Schema(description = "Data e hora da consulta", example = "2025-09-15T10:30:00")
        @NotNull(message = "Data e hora da consulta são obrigatórias")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime scheduledTime,
        
        @Schema(description = "Tipo da consulta", example = "FIRST_CONSULTATION", 
                allowableValues = {"FIRST_CONSULTATION", "FOLLOW_UP", "EMERGENCY", "PROCEDURE", "EXAM", "VACCINATION", "TELEMEDICINE"})
        @NotBlank(message = "Tipo da consulta é obrigatório")
        String appointmentType,
        
        @Schema(description = "Observações da consulta", example = "Consulta de rotina")
        String observations
) {
    public AppointmentRequestDto {
        // Validações e limpeza de dados
        if (patientId != null) {
            patientId = patientId.trim();
        }
        if (professionalId != null) {
            professionalId = professionalId.trim();
        }
        if (appointmentType != null) {
            appointmentType = appointmentType.trim().toUpperCase();
        }
        if (observations != null) {
            observations = observations.trim();
        }
    }
    
    /**
     * Factory method para criação de request com dados mínimos.
     */
    public static AppointmentRequestDto of(String patientId, String professionalId, 
                                         LocalDateTime scheduledTime, String appointmentType) {
        return new AppointmentRequestDto(patientId, professionalId, scheduledTime, appointmentType, null);
    }
}
