package com.clinicboard.business_service.infrastructure.adapter.in.web.mapper;

import com.clinicboard.business_service.application.port.in.ScheduleAppointmentCommand.ScheduleAppointmentRequest;
import com.clinicboard.business_service.application.port.in.ScheduleAppointmentCommand.ScheduleAppointmentResponse;
import com.clinicboard.business_service.infrastructure.adapter.in.web.dto.AppointmentRequestDto;
import com.clinicboard.business_service.infrastructure.adapter.in.web.dto.AppointmentResponseDto;
import com.clinicboard.business_service.domain.model.PatientId;
import com.clinicboard.business_service.domain.model.ProfessionalId;
import com.clinicboard.business_service.domain.model.Appointment;
import com.clinicboard.business_service.domain.model.AppointmentTime;
import com.clinicboard.business_service.domain.model.AppointmentType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper para conversão entre DTOs de consulta e objetos da camada de aplicação.
 * 
 * Responsabilidades:
 * - Tradução entre representação HTTP e Application Layer
 * - Isolamento do domínio dos detalhes de infraestrutura web
 * - Conversão de tipos primitivos para Value Objects
 * 
 * Seguindo DDD e Arquitetura Hexagonal:
 * - Adaptador de entrada (Web → Application)
 * - Mapeamento explícito e controlado
 * - Validação e transformação de dados
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppointmentWebMapper {
    
    /**
     * Converte DTO de requisição REST para comando da camada de aplicação.
     * Usado para operações de criação e atualização de appointments.
     */
    default ScheduleAppointmentRequest toApplicationRequest(AppointmentRequestDto dto) {
        return new ScheduleAppointmentRequest(
            PatientId.of(dto.patientId()),
            ProfessionalId.of(dto.professionalId()),
            AppointmentTime.of(dto.scheduledTime()),
            mapDtoToAppointmentType(dto.appointmentType()),
            dto.observations()
        );
    }
    
    /**
     * Converte resposta da camada de aplicação para DTO de resposta REST.
     * Usado para retornar dados após operações bem-sucedidas.
     */
    default AppointmentResponseDto toResponseDto(ScheduleAppointmentResponse response) {
        return new AppointmentResponseDto(
            response.appointmentId().value(),
            response.patientId().value(),
            response.professionalId().value(),
            response.appointmentTime().value(),
            response.appointmentType().name(),
            "SCHEDULED", // Status inicial padrão
            response.observations(),
            java.time.LocalDateTime.now(), // createdAt
            java.time.LocalDateTime.now(), // updatedAt
            null, // cancelledAt
            null  // cancellationReason
        );
    }
    
    /**
     * Converte entidade de domínio completa para DTO de resposta REST.
     * Usado para consultas e operações que retornam dados completos.
     */
    default AppointmentResponseDto toResponseDto(Appointment appointment) {
        return new AppointmentResponseDto(
            appointment.getId().value(),
            appointment.getPatientId().value(),
            appointment.getProfessionalId().value(),
            appointment.getScheduledTime().value(),
            appointment.getType().name(),
            appointment.getStatus().name(),
            appointment.getObservation(),
            appointment.getCreatedAt(),
            appointment.getUpdatedAt(),
            appointment.getCancelledAt(),
            appointment.getCancellationReason()
        );
    }
    
    /**
     * Mapeia string do DTO para enum do domínio.
     * Centraliza a lógica de conversão de tipos de appointment.
     */
    private AppointmentType mapDtoToAppointmentType(String appointmentType) {
        if (appointmentType == null || appointmentType.trim().isEmpty()) {
            return AppointmentType.FIRST_CONSULTATION; // Padrão
        }
        
        return switch (appointmentType.trim().toUpperCase()) {
            case "FIRST_CONSULTATION" -> AppointmentType.FIRST_CONSULTATION;
            case "FOLLOW_UP" -> AppointmentType.FOLLOW_UP;
            case "EMERGENCY" -> AppointmentType.EMERGENCY;
            case "PROCEDURE" -> AppointmentType.PROCEDURE;
            case "EXAM" -> AppointmentType.EXAM;
            case "VACCINATION" -> AppointmentType.VACCINATION;
            case "TELEMEDICINE" -> AppointmentType.TELEMEDICINE;
            default -> AppointmentType.FIRST_CONSULTATION;
        };
    }
}
