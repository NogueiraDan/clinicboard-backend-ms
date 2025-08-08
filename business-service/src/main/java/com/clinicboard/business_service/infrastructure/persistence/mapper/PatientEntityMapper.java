package com.clinicboard.business_service.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.clinicboard.business_service.domain.model.Patient;
import com.clinicboard.business_service.domain.model.valueobjects.Contact;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;
import com.clinicboard.business_service.infrastructure.persistence.entity.PatientEntity;

/**
 * Mapper responsável por converter entre Agregado de Paciente e Entidade JPA.
 * Separa claramente a camada de domínio da camada de infraestrutura.
 */
@Component
public class PatientEntityMapper {

    /**
     * Converte do Agregado de Domínio para Entidade JPA
     */
    public PatientEntity toEntity(Patient aggregate) {
        if (aggregate == null) {
            return null;
        }

        PatientEntity entity = new PatientEntity();
        entity.setId(aggregate.getId());
        entity.setName(aggregate.getName());
        entity.setEmail(aggregate.getEmail());
        entity.setContact(aggregate.getContactPhone());
        entity.setProfessionalId(aggregate.getProfessionalId().getValue());

        return entity;
    }

    /**
     * Converte da Entidade JPA para Agregado de Domínio
     */
    public Patient toDomain(PatientEntity entity) {
        if (entity == null) {
            return null;
        }

        // Criar value objects
        Contact contact = new Contact(entity.getEmail(), entity.getContact());
        ProfessionalId professionalId = new ProfessionalId(entity.getProfessionalId());

        // Reconstruir agregado usando construtor de reconstrução
        return new Patient(
            entity.getId(),
            entity.getName(),
            contact,
            professionalId
        );
    }
}
