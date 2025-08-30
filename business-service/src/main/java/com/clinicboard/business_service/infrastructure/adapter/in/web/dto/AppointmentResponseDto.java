package com.clinicboard.business_service.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO de resposta para operações de agendamento.
 * 
 * Contém todas as informações necessárias sobre um agendamento
 * após qualquer operação (criação, atualização, consulta).
 * 
 * Princípios aplicados:
 * - Imutável (record)
 * - Documentação clara com Swagger
 * - Separação de responsabilidades
 */
@Schema(description = "Response com informações completas da consulta")
public record AppointmentResponseDto(
        
        @Schema(description = "ID da consulta", example = "550e8400-e29b-41d4-a716-446655440001")
        String appointmentId,
        
        @Schema(description = "ID do paciente", example = "550e8400-e29b-41d4-a716-446655440000")
        String patientId,
        
        @Schema(description = "ID do profissional", example = "660e8400-e29b-41d4-a716-446655440001")
        String professionalId,
        
        @Schema(description = "Data e hora da consulta", example = "2025-09-15T10:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime scheduledTime,
        
        @Schema(description = "Tipo da consulta", example = "FIRST_CONSULTATION")
        String appointmentType,
        
        @Schema(description = "Status da consulta", example = "SCHEDULED", 
                allowableValues = {"SCHEDULED", "CONFIRMED", "IN_PROGRESS", "COMPLETED", "CANCELLED", "NO_SHOW"})
        String status,
        
        @Schema(description = "Observações da consulta", example = "Consulta de rotina")
        String observations,
        
        @Schema(description = "Data de criação do agendamento", example = "2025-08-30T14:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        
        @Schema(description = "Data da última atualização", example = "2025-08-30T14:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt,
        
        @Schema(description = "Data de cancelamento (se aplicável)", example = "2025-08-30T16:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime cancelledAt,
        
        @Schema(description = "Motivo do cancelamento (se aplicável)", example = "Paciente não pode comparecer")
        String cancellationReason
) {
    
    /**
     * Factory method para agendamento recém-criado.
     */
    public static AppointmentResponseDto created(String appointmentId, String patientId, String professionalId,
                                               LocalDateTime scheduledTime, String appointmentType, String observations) {
        return new AppointmentResponseDto(
            appointmentId, patientId, professionalId, scheduledTime, appointmentType,
            "SCHEDULED", observations, LocalDateTime.now(), LocalDateTime.now(), null, null
        );
    }
    
    /**
     * Factory method para agendamento cancelado.
     */
    public static AppointmentResponseDto cancelled(AppointmentResponseDto original, String reason) {
        return new AppointmentResponseDto(
            original.appointmentId, original.patientId, original.professionalId,
            original.scheduledTime, original.appointmentType, "CANCELLED",
            original.observations, original.createdAt, LocalDateTime.now(),
            LocalDateTime.now(), reason
        );
    }
}
