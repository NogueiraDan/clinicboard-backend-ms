package com.clinicboard.business_service.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDto {

    @NotBlank(message = "O nome não pode ser vazio!")
    @NotNull(message = "O nome não pode ser nulo!")
    private String id;

    @NotBlank(message = "O nome não pode ser vazio!")
    @NotNull(message = "O nome não pode ser nulo!")
    private String name;

    @NotBlank(message = "O email não pode ser vazio!")
    @NotNull(message = "O email não pode ser nulo!")
    private String email;

    @NotBlank(message = "O contato não pode ser vazio!")
    @NotNull(message = "O contato não pode ser nulo!")
    private String contact;

    @NotBlank(message = "O id do profissional não pode ser vazio!")
    @NotNull(message = "O id do profissional não pode ser nulo!")
    private String professionalId;

}
