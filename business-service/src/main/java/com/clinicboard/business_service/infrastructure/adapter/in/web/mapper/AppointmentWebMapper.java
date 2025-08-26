package com.clinicboard.business_service.infrastructure.adapter.in.web.mapper;

import com.clinicboard.business_service.application.port.in.ScheduleAppointmentCommand.ScheduleAppointmentRequest;
import com.clinicboard.business_service.application.port.in.ScheduleAppointmentCommand.ScheduleAppointmentResponse;
import com.clinicboard.business_service.infrastructure.adapter.in.web.dto.ScheduleAppointmentRequestDto;
import com.clinicboard.business_service.infrastructure.adapter.in.web.dto.AppointmentResponseDto;
import com.clinicboard.business_service.domain.model.PatientId;
import com.clinicboard.business_service.domain.model.ProfessionalId;
import com.clinicboard.business_service.domain.model.AppointmentTime;
import com.clinicboard.business_service.domain.model.AppointmentType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper para conversão entre DTOs de consulta e objetos da camada de aplicação.
 * 
 * Seguindo os princípios DDD, este mapper atua como um tradutor entre o mundo externo
 * (representado pelos DTOs REST) e nossa camada de aplicação, mantendo o domínio
 * isolado dos detalhes de infraestrutura.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppointmentWebMapper {
    
    /**
     * Converte DTO de requisição REST para comando da camada de aplicação.
     */
    default ScheduleAppointmentRequest toApplicationRequest(ScheduleAppointmentRequestDto dto) {
        return new ScheduleAppointmentRequest(
            PatientId.of(dto.patientId()),
            ProfessionalId.of("default-professional"), // Temporary - será obtido do contexto de segurança
            AppointmentTime.of(dto.scheduledTime()),
            AppointmentType.FIRST_CONSULTATION, // Temporary - será parametrizável
            dto.notes()
        );
    }
    
    /**
     * Converte resposta da camada de aplicação para DTO de resposta REST.
     */
    default AppointmentResponseDto toResponseDto(ScheduleAppointmentResponse response) {
        return new AppointmentResponseDto(
            response.appointmentId().value(),
            response.patientId().value(),
            "Patient Name", // Temporary - será obtido do agregado
            response.appointmentTime().value(),
            response.appointmentType().name(),
            response.observations(),
            response.appointmentTime().value() // createdAt temporary
        );
    }
}
