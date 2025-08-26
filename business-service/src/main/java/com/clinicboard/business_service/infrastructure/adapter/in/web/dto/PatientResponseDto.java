package com.clinicboard.business_service.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response com informações do paciente")
public record PatientResponseDto(
        
        @Schema(description = "ID do paciente", example = "550e8400-e29b-41d4-a716-446655440000")
        String patientId,
        
        @Schema(description = "Nome do paciente", example = "João Silva")
        String name,
        
        @Schema(description = "Email do paciente", example = "joao.silva@email.com")
        String email,
        
        @Schema(description = "Telefone do paciente", example = "(11) 99999-9999")
        String phone
) {
}
