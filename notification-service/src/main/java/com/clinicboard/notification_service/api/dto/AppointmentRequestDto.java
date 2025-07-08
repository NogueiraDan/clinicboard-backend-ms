package com.clinicboard.notification_service.api.dto;

import java.time.LocalDateTime;

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
public class AppointmentRequestDto {

    @NotNull(message = "A data não pode ser nula!")
    private LocalDateTime date;

    @NotBlank(message = "O id do profissional não pode ser vazio!")
    @NotNull(message = "O id do profissional não pode ser nulo!")
    private String professionalId;

    @NotBlank(message = "O id do paciente não pode ser vazio!")
    @NotNull(message = "O id do paciente não pode ser nulo!")
    private String patientId;

    private String observation;

}
