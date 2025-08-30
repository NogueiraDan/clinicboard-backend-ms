package com.clinicboard.business_service.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO específico para cancelamento de agendamentos.
 * 
 * Separado do AppointmentRequestDto para manter responsabilidades claras
 * e seguir o princípio de responsabilidade única.
 */
@Schema(description = "Request para cancelamento de agendamento")
public record CancelAppointmentRequestDto(
        
        @Schema(description = "Motivo do cancelamento", example = "Paciente não pode comparecer")
        @NotBlank(message = "Motivo do cancelamento é obrigatório")
        @Size(max = 500, message = "Motivo não pode exceder 500 caracteres")
        String reason
) {
    public CancelAppointmentRequestDto {
        if (reason != null) {
            reason = reason.trim();
        }
    }
}
