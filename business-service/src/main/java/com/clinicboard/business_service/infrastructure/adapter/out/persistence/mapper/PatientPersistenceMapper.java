package com.clinicboard.business_service.infrastructure.adapter.out.persistence.mapper;

import com.clinicboard.business_service.domain.model.PatientId;
import com.clinicboard.business_service.domain.model.PatientName;
import com.clinicboard.business_service.domain.model.Email;
import com.clinicboard.business_service.domain.model.Patient;
import com.clinicboard.business_service.domain.model.ContactDetails;
import com.clinicboard.business_service.domain.model.ProfessionalId;
import com.clinicboard.business_service.domain.model.PatientStatus;
import com.clinicboard.business_service.infrastructure.adapter.out.persistence.entity.PatientJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper entre entidades JPA e objetos de domínio para Pacientes.
 * 
 * Responsável por converter entre a representação de persistência (JPA)
 * e os objetos de domínio, mantendo o isolamento das camadas.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PatientPersistenceMapper {
    
    /**
     * Converte entidade JPA para objeto de domínio.
     */
    default Patient toDomainEntity(PatientJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        return new Patient(
            PatientId.of(jpaEntity.getPatientId()),
            PatientName.of(jpaEntity.getName()),
            Email.of(jpaEntity.getEmail()),
            ContactDetails.of(jpaEntity.getContactDetails()),
            ProfessionalId.of("default-professional"), // TODO: Implementar mapeamento correto
            jpaEntity.getActive() ? PatientStatus.ACTIVE : PatientStatus.INACTIVE,
            jpaEntity.getCreatedAt(),
            jpaEntity.getUpdatedAt()
        );
    }
    
    /**
     * Converte objeto de domínio para entidade JPA.
     */
    default PatientJpaEntity toJpaEntity(Patient patient) {
        if (patient == null) {
            return null;
        }
        
        return PatientJpaEntity.builder()
            .patientId(patient.getId() != null ? patient.getId().value() : null)
            .name(patient.getName())
            .email(patient.getEmail().value())
            .contactDetails(patient.getContact().value())
            .active(patient.getStatus() == PatientStatus.ACTIVE)
            .build();
    }
    
    /**
     * Atualiza entidade JPA existente com dados do domínio.
     * Preserva metadados como createdAt, version, etc.
     */
    default void updateJpaEntity(PatientJpaEntity jpaEntity, Patient patient) {
        if (jpaEntity == null || patient == null) {
            return;
        }
        
        jpaEntity.setName(patient.getName());
        jpaEntity.setEmail(patient.getEmail().value());
        jpaEntity.setContactDetails(patient.getContact().value());
        jpaEntity.setActive(patient.getStatus() == PatientStatus.ACTIVE);
        // createdAt, version permanecem inalterados
    }
}
