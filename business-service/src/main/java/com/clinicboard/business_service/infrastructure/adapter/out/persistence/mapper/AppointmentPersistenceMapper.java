package com.clinicboard.business_service.infrastructure.adapter.out.persistence.mapper;

import com.clinicboard.business_service.domain.model.Appointment;
import com.clinicboard.business_service.domain.model.AppointmentId;
import com.clinicboard.business_service.domain.model.PatientId;
import com.clinicboard.business_service.domain.model.ProfessionalId;
import com.clinicboard.business_service.domain.model.AppointmentTime;
import com.clinicboard.business_service.domain.model.AppointmentType;
import com.clinicboard.business_service.domain.model.AppointmentStatus;
import com.clinicboard.business_service.infrastructure.adapter.out.persistence.entity.AppointmentJpaEntity;
import com.clinicboard.business_service.infrastructure.adapter.out.persistence.entity.AppointmentJpaEntity.AppointmentTypeEnum;
import com.clinicboard.business_service.infrastructure.adapter.out.persistence.entity.AppointmentJpaEntity.AppointmentStatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper entre entidades JPA e objetos de domínio para Appointments.
 * 
 * Converte entre representação de persistência e objetos de domínio,
 * incluindo mapeamento de enums e Value Objects.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppointmentPersistenceMapper {
    
    /**
     * Converte entidade JPA para objeto de domínio.
     */
    default Appointment toDomainEntity(AppointmentJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        return new Appointment(
            AppointmentId.of(jpaEntity.getAppointmentId()),
            PatientId.of(jpaEntity.getPatientId()),
            ProfessionalId.of(jpaEntity.getProfessionalId()),
            AppointmentTime.of(jpaEntity.getScheduledTime()),
            mapToDomainAppointmentStatus(jpaEntity.getStatus()),
            mapToDomainAppointmentType(jpaEntity.getAppointmentType()),
            jpaEntity.getObservations(),
            jpaEntity.getCreatedAt(),
            jpaEntity.getUpdatedAt()
        );
    }
    
    /**
     * Converte objeto de domínio para entidade JPA.
     */
    default AppointmentJpaEntity toJpaEntity(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        
        return AppointmentJpaEntity.builder()
            .appointmentId(appointment.getId() != null ? appointment.getId().value() : null)
            .patientId(appointment.getPatientId().value())
            .professionalId(appointment.getProfessionalId().value())
            .scheduledTime(appointment.getScheduledTime().value())
            .appointmentType(mapToJpaAppointmentType(appointment.getType()))
            .status(mapToJpaAppointmentStatus(appointment.getStatus()))
            .observations(appointment.getObservation())
            .build();
    }
    
    /**
     * Atualiza entidade JPA existente com dados do domínio.
     */
    default void updateJpaEntity(AppointmentJpaEntity jpaEntity, Appointment appointment) {
        if (jpaEntity == null || appointment == null) {
            return;
        }
        
        jpaEntity.setScheduledTime(appointment.getScheduledTime().value());
        jpaEntity.setAppointmentType(mapToJpaAppointmentType(appointment.getType()));
        jpaEntity.setStatus(mapToJpaAppointmentStatus(appointment.getStatus()));
        jpaEntity.setObservations(appointment.getObservation());
        // ID, createdAt, version permanecem inalterados
    }
    
    // Métodos de mapeamento de enums
    
    private AppointmentType mapToDomainAppointmentType(AppointmentTypeEnum jpaType) {
        return switch (jpaType) {
            case FIRST_CONSULTATION -> AppointmentType.FIRST_CONSULTATION;
            case FOLLOW_UP -> AppointmentType.FOLLOW_UP;
            case EMERGENCY -> AppointmentType.EMERGENCY;
            case PROCEDURE -> AppointmentType.PROCEDURE;
            case EXAM -> AppointmentType.EXAM;
            case VACCINATION -> AppointmentType.VACCINATION;
            case TELEMEDICINE -> AppointmentType.TELEMEDICINE;
        };
    }
    
    private AppointmentTypeEnum mapToJpaAppointmentType(AppointmentType domainType) {
        return switch (domainType) {
            case FIRST_CONSULTATION -> AppointmentTypeEnum.FIRST_CONSULTATION;
            case FOLLOW_UP -> AppointmentTypeEnum.FOLLOW_UP;
            case EMERGENCY -> AppointmentTypeEnum.EMERGENCY;
            case PROCEDURE -> AppointmentTypeEnum.PROCEDURE;
            case EXAM -> AppointmentTypeEnum.EXAM;
            case VACCINATION -> AppointmentTypeEnum.VACCINATION;
            case TELEMEDICINE -> AppointmentTypeEnum.TELEMEDICINE;
        };
    }
    
    private AppointmentStatus mapToDomainAppointmentStatus(AppointmentStatusEnum jpaStatus) {
        return switch (jpaStatus) {
            case SCHEDULED -> AppointmentStatus.SCHEDULED;
            case CONFIRMED -> AppointmentStatus.CONFIRMED;
            case IN_PROGRESS -> AppointmentStatus.IN_PROGRESS;
            case COMPLETED -> AppointmentStatus.COMPLETED;
            case CANCELLED -> AppointmentStatus.CANCELLED;
            case NO_SHOW -> AppointmentStatus.NO_SHOW;
        };
    }
    
    private AppointmentStatusEnum mapToJpaAppointmentStatus(AppointmentStatus domainStatus) {
        return switch (domainStatus) {
            case PENDING -> AppointmentStatusEnum.SCHEDULED; // Mapeamento: PENDING -> SCHEDULED
            case CONFIRMED -> AppointmentStatusEnum.CONFIRMED;
            case SCHEDULED -> AppointmentStatusEnum.SCHEDULED;
            case IN_PROGRESS -> AppointmentStatusEnum.IN_PROGRESS;
            case COMPLETED -> AppointmentStatusEnum.COMPLETED;
            case CANCELLED -> AppointmentStatusEnum.CANCELLED;
            case NO_SHOW -> AppointmentStatusEnum.NO_SHOW;
            case RESCHEDULED -> AppointmentStatusEnum.SCHEDULED; // Mapeamento: RESCHEDULED -> SCHEDULED
        };
    }
}
