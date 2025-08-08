package com.clinicboard.business_service.domain.model;

import com.clinicboard.business_service.domain.event.PatientRegisteredEvent;
import com.clinicboard.business_service.domain.model.valueobjects.Contact;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;

/**
 * Agregado Patient - representa um paciente no contexto de agendamento médico
 * PURO - sem dependências de infraestrutura
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Patient extends AbstractAggregateRoot<Patient> {

    private String id;
    private String name;
    private Contact contact;
    private ProfessionalId professionalId;

    // Construtor para criação do agregado
    public Patient(String name, Contact contact, ProfessionalId professionalId) {
        validateName(name);
        validateContact(contact);
        validateProfessionalId(professionalId);
        
        this.name = name;
        this.contact = contact;
        this.professionalId = professionalId;
        
        // Registra evento de domínio
        registerEvent(new PatientRegisteredEvent(this.id, this.name, this.contact.getEmail(), this.professionalId.getValue()));
    }

    // Construtor para reconstrução a partir da infraestrutura (usado pelos mappers)
    public Patient(String id, String name, Contact contact, ProfessionalId professionalId) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.professionalId = professionalId;
    }

    // Comportamentos de domínio
    public void updateInformation(String newName, Contact newContact) {
        validateName(newName);
        validateContact(newContact);
        
        this.name = newName;
        this.contact = newContact;
    }

    public void changeProfessional(ProfessionalId newProfessionalId) {
        validateProfessionalId(newProfessionalId);
        this.professionalId = newProfessionalId;
    }

    public boolean isManagedBy(ProfessionalId professionalId) {
        return this.professionalId.equals(professionalId);
    }

    public String getEmail() {
        return contact.getEmail();
    }

    public String getContactPhone() {
        return contact.getPhone();
    }

    public String getProfessionalIdValue() {
        return professionalId.getValue();
    }

    // Validações de negócio
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do paciente não pode ser vazio");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Nome do paciente não pode ter mais de 100 caracteres");
        }
    }

    private void validateContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Informações de contato são obrigatórias");
        }
    }

    private void validateProfessionalId(ProfessionalId professionalId) {
        if (professionalId == null) {
            throw new IllegalArgumentException("ID do profissional é obrigatório");
        }
    }
}
