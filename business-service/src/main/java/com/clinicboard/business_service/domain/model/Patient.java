package com.clinicboard.business_service.domain.model;

import com.clinicboard.business_service.domain.exception.PatientBusinessRuleException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Aggregate Root: Patient
 * 
 * Entidade rica que encapsula todas as regras de negócio relacionadas a pacientes
 * no contexto de agendamento clínico.
 * 
 * Responsabilidades:
 * - Manter consistência das informações do paciente
 * - Aplicar regras de negócio para agendamentos
 * - Controlar estado e comportamentos específicos do domínio
 */
public class Patient {
    
    private final PatientId id;
    private final PatientName name;
    private final Email email;
    private final ContactDetails contact;
    private final ProfessionalId assignedProfessionalId;
    private PatientStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Construtor para novos pacientes (sem ID)
    public Patient(PatientName name, Email email, ContactDetails contact, ProfessionalId assignedProfessionalId) {
        this.id = null; // Será definido pela infraestrutura
        this.name = Objects.requireNonNull(name, "Nome do paciente não pode ser nulo");
        this.email = Objects.requireNonNull(email, "Email do paciente não pode ser nulo");
        this.contact = Objects.requireNonNull(contact, "Contato do paciente não pode ser nulo");
        this.assignedProfessionalId = Objects.requireNonNull(assignedProfessionalId, "Profissional responsável não pode ser nulo");
        this.status = PatientStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Construtor para pacientes existentes (com ID)
    public Patient(PatientId id, PatientName name, Email email, ContactDetails contact, 
                   ProfessionalId assignedProfessionalId, PatientStatus status,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "ID do paciente não pode ser nulo para paciente existente");
        this.name = Objects.requireNonNull(name, "Nome do paciente não pode ser nulo");
        this.email = Objects.requireNonNull(email, "Email do paciente não pode ser nulo");
        this.contact = Objects.requireNonNull(contact, "Contato do paciente não pode ser nulo");
        this.assignedProfessionalId = Objects.requireNonNull(assignedProfessionalId, "Profissional responsável não pode ser nulo");
        this.status = Objects.requireNonNull(status, "Status do paciente não pode ser nulo");
        this.createdAt = Objects.requireNonNull(createdAt, "Data de criação não pode ser nula");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Data de atualização não pode ser nula");
    }

    // Construtor protegido para frameworks
    protected Patient() {
        this.id = null;
        this.name = null;
        this.email = null;
        this.contact = null;
        this.assignedProfessionalId = null;
        this.status = null;
        this.createdAt = null;
        this.updatedAt = null;
    }

    /**
     * Método de negócio: Verifica se o paciente pode agendar consulta
     */
    public boolean canScheduleAppointment(AppointmentTime appointmentTime) {
        // Regra 1: Paciente deve estar ativo
        if (!isActive()) {
            return false;
        }

        // Regra 2: Não pode agendar no passado
        if (appointmentTime.value().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Regra 3: Deve estar dentro do horário comercial
        return appointmentTime.isWithinBusinessHours();
    }

    /**
     * Método de negócio: Verifica se paciente já tem agendamento no dia
     */
    public void validateNoDuplicateAppointmentOnDate(LocalDate date) {
        // Esta validação será delegada para o domain service
        // pois precisa de informações de outros agregados
    }

    /**
     * Método de negócio: Ativa o paciente
     */
    public Patient activate() {
        if (this.status == PatientStatus.ACTIVE) {
            throw new PatientBusinessRuleException(
                this.id != null ? this.id.value() : "novo",
                "Paciente já está ativo"
            );
        }
        
        PatientStatus newStatus = PatientStatus.ACTIVE;
        return new Patient(this.id, this.name, this.email, this.contact, 
                          this.assignedProfessionalId, newStatus, this.createdAt, LocalDateTime.now());
    }

    /**
     * Método de negócio: Inativa o paciente
     */
    public Patient deactivate(String reason) {
        if (this.status == PatientStatus.INACTIVE) {
            throw new PatientBusinessRuleException(
                this.id != null ? this.id.value() : "novo",
                "Paciente já está inativo"
            );
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new PatientBusinessRuleException(
                this.id != null ? this.id.value() : "novo",
                "Motivo da inativação é obrigatório"
            );
        }
        
        PatientStatus newStatus = PatientStatus.INACTIVE;
        return new Patient(this.id, this.name, this.email, this.contact, 
                          this.assignedProfessionalId, newStatus, this.createdAt, LocalDateTime.now());
    }

    /**
     * Método de negócio: Atualiza informações de contato
     */
    public Patient updateContactInfo(ContactDetails newContact) {
        Objects.requireNonNull(newContact, "Novo contato não pode ser nulo");
        
        return new Patient(this.id, this.name, this.email, newContact, 
                          this.assignedProfessionalId, this.status, this.createdAt, LocalDateTime.now());
    }

    /**
     * Método de negócio: Reatribui paciente a novo profissional
     */
    public Patient reassignToProfessional(ProfessionalId newProfessionalId) {
        Objects.requireNonNull(newProfessionalId, "Novo profissional não pode ser nulo");
        
        if (this.assignedProfessionalId.equals(newProfessionalId)) {
            throw new PatientBusinessRuleException(
                this.id != null ? this.id.value() : "novo",
                "Paciente já está atribuído a este profissional"
            );
        }
        
        return new Patient(this.id, this.name, this.email, this.contact, 
                          newProfessionalId, this.status, this.createdAt, LocalDateTime.now());
    }

    /**
     * Método de negócio: Define ID após persistência
     */
    public Patient withId(PatientId newId) {
        Objects.requireNonNull(newId, "ID não pode ser nulo");
        return new Patient(newId, this.name, this.email, this.contact, 
                          this.assignedProfessionalId, this.status, this.createdAt, this.updatedAt);
    }

    /**
     * Verifica se o paciente está ativo
     */
    public boolean isActive() {
        return this.status == PatientStatus.ACTIVE;
    }

    /**
     * Verifica se o paciente está inativo
     */
    public boolean isInactive() {
        return this.status == PatientStatus.INACTIVE;
    }

    /**
     * Verifica se o paciente pertence ao profissional especificado
     */
    public boolean belongsToProfessional(ProfessionalId professionalId) {
        return this.assignedProfessionalId.equals(professionalId);
    }

    // Getters
    public PatientId getId() {
        return id;
    }

    public PatientName getDomainName() {
        return name;
    }

    public String getName() {
        return name != null ? name.value() : null;
    }

    public Email getEmail() {
        return email;
    }

    public ContactDetails getContact() {
        return contact;
    }

    public ProfessionalId getAssignedProfessionalId() {
        return assignedProfessionalId;
    }

    public PatientStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Objects.equals(id, patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Patient{id=%s, name='%s', status=%s}", 
                           id != null ? id.value() : "novo", 
                           name != null ? name.value() : "null", 
                           status);
    }
}
