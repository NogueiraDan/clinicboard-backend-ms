package com.clinicboard.business_service.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.clinicboard.business_service.domain.model.Appointment;
import com.clinicboard.business_service.domain.model.enums.AppointmentStatus;
import com.clinicboard.business_service.domain.model.enums.AppointmentType;
import com.clinicboard.business_service.domain.model.valueobjects.AppointmentTime;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;
import com.clinicboard.business_service.infrastructure.persistence.entity.AppointmentEntity;
import com.clinicboard.business_service.infrastructure.persistence.entity.AppointmentStatusEntity;
import com.clinicboard.business_service.infrastructure.persistence.entity.AppointmentTypeEntity;

/**
 * Mapper responsável por converter entre Agregado de Domínio e Entidade JPA.
 * Separa claramente a camada de domínio da camada de infraestrutura.
 */
@Component
public class AppointmentEntityMapper {

    /**
     * Converte do Agregado de Domínio para Entidade JPA
     */
    public AppointmentEntity toEntity(Appointment aggregate) {
        if (aggregate == null) {
            return null;
        }

        AppointmentEntity entity = new AppointmentEntity();
        entity.setId(aggregate.getId());
        entity.setDate(aggregate.getAppointmentTime().getValue());
        entity.setStatus(toEntityStatus(aggregate.getStatus()));
        entity.setType(toEntityType(aggregate.getType()));
        entity.setProfessionalId(aggregate.getProfessionalId().getValue());
        entity.setPatientId(aggregate.getPatientId());
        entity.setObservation(aggregate.getObservation());
        entity.setCreatedAt(aggregate.getCreatedAt());
        entity.setUpdatedAt(aggregate.getUpdatedAt());

        return entity;
    }

    /**
     * Converte da Entidade JPA para Agregado de Domínio
     */
    public Appointment toDomain(AppointmentEntity entity) {
        if (entity == null) {
            return null;
        }

        // Criar value objects
        AppointmentTime appointmentTime = new AppointmentTime(entity.getDate());
        ProfessionalId professionalId = new ProfessionalId(entity.getProfessionalId());

        // Reconstruir agregado usando construtor de reconstrução
        return new Appointment(
            entity.getId(),
            appointmentTime,
            toDomainStatus(entity.getStatus()),
            toDomainType(entity.getType()),
            professionalId,
            entity.getPatientId(),
            entity.getObservation(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    private AppointmentStatusEntity toEntityStatus(AppointmentStatus domainStatus) {
        return switch (domainStatus) {
            case PENDING -> AppointmentStatusEntity.PENDING;
            case SCHEDULED -> AppointmentStatusEntity.SCHEDULED;
            case CANCELED -> AppointmentStatusEntity.CANCELED;
            case COMPLETED -> AppointmentStatusEntity.COMPLETED;
            case NO_SHOW -> AppointmentStatusEntity.NO_SHOW;
            case RESCHEDULED -> AppointmentStatusEntity.RESCHEDULED;
        };
    }

    private AppointmentStatus toDomainStatus(AppointmentStatusEntity entityStatus) {
        return switch (entityStatus) {
            case PENDING -> AppointmentStatus.PENDING;
            case SCHEDULED -> AppointmentStatus.SCHEDULED;
            case CANCELED -> AppointmentStatus.CANCELED;
            case COMPLETED -> AppointmentStatus.COMPLETED;
            case NO_SHOW -> AppointmentStatus.NO_SHOW;
            case RESCHEDULED -> AppointmentStatus.RESCHEDULED;
        };
    }

    private AppointmentTypeEntity toEntityType(AppointmentType domainType) {
        return switch (domainType) {
            case MARCACAO -> AppointmentTypeEntity.MARCACAO;
            case REMARCACAO -> AppointmentTypeEntity.REMARCACAO;
        };
    }

    private AppointmentType toDomainType(AppointmentTypeEntity entityType) {
        return switch (entityType) {
            case MARCACAO -> AppointmentType.MARCACAO;
            case REMARCACAO -> AppointmentType.REMARCACAO;
        };
    }
}
