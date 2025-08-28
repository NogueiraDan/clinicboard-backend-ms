package com.clinicboard.business_service.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para criação/atualização de paciente")
public record ManagePatientRequestDto(
        
        @Schema(description = "Nome do paciente", example = "João Silva")
        @NotBlank(message = "Nome do paciente é obrigatório")
        String name,
        
        @Schema(description = "Email do paciente", example = "joao.silva@email.com")
        @Email(message = "Email deve ter formato válido")
        String email,
        
        @Schema(description = "Telefone do paciente", example = "(11) 99999-9999")
        String phone,
        
        @Schema(description = "ID do profissional responsável", example = "PROF-12345")
        @NotBlank(message = "ID do profissional responsável é obrigatório")
        String professionalId
) {
    public ManagePatientRequestDto {
        // Limpeza e formatação dos dados
        if (name != null) {
            name = name.trim();
        }
        if (email != null) {
            email = email.trim().toLowerCase();
        }
        if (phone != null) {
            phone = phone.trim();
        }
        if (professionalId != null) {
            professionalId = professionalId.trim();
        }
    }
}
