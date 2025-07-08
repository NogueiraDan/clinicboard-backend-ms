package com.clinicboard.user_service.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank(message = "Email não pode ser vazio!")
    @Email(message = "Digite um email válido!")
    private String email;
    
    @NotBlank(message = "Senha não pode ser vazia!")
    @NotNull(message = "Senha não pode ser nula!")
    private String password;
}