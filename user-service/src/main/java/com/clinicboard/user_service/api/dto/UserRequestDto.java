package com.clinicboard.user_service.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @NotBlank(message = "O nome não pode ser vazio!")
    private String name;

    @NotBlank(message = "O email não pode ser vazio!")
    @Email(message = "Digite um email válido")
    private String email;

    @NotBlank(message = "A senha não pode ser vazia!")
    @Size(min = 6, message = "A senha precisa ter no mínimo 6 caracteres!")
    @Size(max = 20, message = "A senha precisa ter no máximo 20 caracteres!")
    private String password;

    @NotBlank(message = "O contato não pode ser vazio!")
    private String contact;

    private UserRole role = UserRole.PROFESSIONAL;
}