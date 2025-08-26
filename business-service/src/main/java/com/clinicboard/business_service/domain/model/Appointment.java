package com.clinicboard.business_service.domain.model;

import com.clinicboard.business_service.domain.exception.AppointmentConflictException;
import com.clinicboard.business_service.domain.exception.DomainException;
import com.clinicboard.business_service.domain.event.AppointmentScheduledEvent;
import com.clinicboard.business_service.domain.event.AppointmentCancelledEvent;
import com.clinicboard.business_service.domain.event.AppointmentStatusChangedEvent;
import com.clinicboard.business_service.domain.event.DomainEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root: Appointment
 * 
 * Entidade rica que encapsula todas as regras de negócio relacionadas a agendamentos
 * no contexto clínico.
 * 
 * Responsabilidades:
 * - Manter consistência do agendamento
 * - Aplicar regras de negócio para mudanças de status
 * - Gerenciar eventos de domínio
 * - Controlar conflitos de horário
 */
public class Appointment {
    
    private final AppointmentId id;
    private final PatientId patientId;
    private final ProfessionalId professionalId;
    private final AppointmentTime scheduledTime;
    private AppointmentStatus status;
    private final AppointmentType type;
    private String observation;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Domain Events - seguindo pattern do Eric Evans
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // Construtor para novos agendamentos
    public Appointment(PatientId patientId, ProfessionalId professionalId, 
                      AppointmentTime scheduledTime, AppointmentType type) {
        this.id = null; // Será definido pela infraestrutura
        this.patientId = Objects.requireNonNull(patientId, "PatientId não pode ser nulo");
        this.professionalId = Objects.requireNonNull(professionalId, "ProfessionalId não pode ser nulo");
        this.scheduledTime = Objects.requireNonNull(scheduledTime, "AppointmentTime não pode ser nulo");
        this.type = Objects.requireNonNull(type, "AppointmentType não pode ser nulo");
        this.status = AppointmentStatus.PENDING;
        this.observation = "";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        // Validações de negócio no construtor
        this.validateNewAppointment();
        
        // Registra evento de agendamento criado
        this.addDomainEvent(new AppointmentScheduledEvent(
            this.id,
            this.patientId,
            this.professionalId,
            this.scheduledTime,
            this.createdAt.toInstant(java.time.ZoneOffset.UTC)
        ));
    }

    // Construtor para agendamentos existentes (reconstrução do banco)
    public Appointment(AppointmentId id, PatientId patientId, ProfessionalId professionalId,
                      AppointmentTime scheduledTime, AppointmentStatus status, 
                      AppointmentType type, String observation,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "AppointmentId não pode ser nulo para agendamento existente");
        this.patientId = Objects.requireNonNull(patientId, "PatientId não pode ser nulo");
        this.professionalId = Objects.requireNonNull(professionalId, "ProfessionalId não pode ser nulo");
        this.scheduledTime = Objects.requireNonNull(scheduledTime, "AppointmentTime não pode ser nulo");
        this.status = Objects.requireNonNull(status, "AppointmentStatus não pode ser nulo");
        this.type = Objects.requireNonNull(type, "AppointmentType não pode ser nulo");
        this.observation = observation != null ? observation : "";
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt não pode ser nulo");
        this.updatedAt = Objects.requireNonNull(updatedAt, "UpdatedAt não pode ser nulo");
    }

    // Construtor protegido para frameworks
    protected Appointment() {
        this.id = null;
        this.patientId = null;
        this.professionalId = null;
        this.scheduledTime = null;
        this.status = null;
        this.type = null;
        this.observation = null;
        this.createdAt = null;
        this.updatedAt = null;
    }

    /**
     * Validações para novos agendamentos
     */
    private void validateNewAppointment() {
        // Valida se o horário está no futuro com antecedência mínima
        if (!scheduledTime.value().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new InvalidAppointmentException(
                "Agendamento deve ser feito com pelo menos 2 horas de antecedência"
            );
        }

        // Valida horário comercial (já validado no AppointmentTime, mas reforçamos aqui)
        if (!scheduledTime.isWithinBusinessHours()) {
            throw new InvalidAppointmentException(
                "Agendamento deve estar dentro do horário comercial"
            );
        }
    }

    /**
     * Método de negócio: Confirma o agendamento
     */
    public Appointment confirm() {
        if (!status.canTransitionTo(AppointmentStatus.CONFIRMED)) {
            throw new InvalidStatusTransitionException(
                String.format("Não é possível confirmar agendamento com status %s", status)
            );
        }

        AppointmentStatus previousStatus = this.status;
        Appointment confirmedAppointment = new Appointment(
            this.id, this.patientId, this.professionalId, this.scheduledTime,
            AppointmentStatus.CONFIRMED, this.type, this.observation,
            this.createdAt, LocalDateTime.now()
        );

        confirmedAppointment.addDomainEvent(new AppointmentStatusChangedEvent(
            this.id, previousStatus, AppointmentStatus.CONFIRMED,
            LocalDateTime.now().toInstant(java.time.ZoneOffset.UTC)
        ));

        return confirmedAppointment;
    }

    /**
     * Método de negócio: Cancela o agendamento
     */
    public Appointment cancel(String reason) {
        if (!status.isCancellable()) {
            throw new InvalidStatusTransitionException(
                String.format("Não é possível cancelar agendamento com status %s", status)
            );
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new InvalidAppointmentException("Motivo do cancelamento é obrigatório");
        }

        AppointmentStatus previousStatus = this.status;
        Appointment cancelledAppointment = new Appointment(
            this.id, this.patientId, this.professionalId, this.scheduledTime,
            AppointmentStatus.CANCELLED, this.type, reason,
            this.createdAt, LocalDateTime.now()
        );

        cancelledAppointment.addDomainEvent(new AppointmentCancelledEvent(
            this.id, this.patientId, this.professionalId, reason,
            LocalDateTime.now().toInstant(java.time.ZoneOffset.UTC)
        ));

        return cancelledAppointment;
    }

    /**
     * Método de negócio: Marca como não compareceu
     */
    public Appointment markAsNoShow() {
        if (!status.canTransitionTo(AppointmentStatus.NO_SHOW)) {
            throw new InvalidStatusTransitionException(
                String.format("Não é possível marcar como faltou para agendamento com status %s", status)
            );
        }

        // Só pode marcar como no-show após o horário agendado
        if (LocalDateTime.now().isBefore(scheduledTime.value().plusMinutes(15))) {
            throw new InvalidAppointmentException(
                "Só é possível marcar como faltou após 15 minutos do horário agendado"
            );
        }

        AppointmentStatus previousStatus = this.status;
        Appointment noShowAppointment = new Appointment(
            this.id, this.patientId, this.professionalId, this.scheduledTime,
            AppointmentStatus.NO_SHOW, this.type, "Paciente não compareceu",
            this.createdAt, LocalDateTime.now()
        );

        noShowAppointment.addDomainEvent(new AppointmentStatusChangedEvent(
            this.id, previousStatus, AppointmentStatus.NO_SHOW,
            LocalDateTime.now().toInstant(java.time.ZoneOffset.UTC)
        ));

        return noShowAppointment;
    }

    /**
     * Método de negócio: Completa o agendamento
     */
    public Appointment complete(String observations) {
        if (!status.canTransitionTo(AppointmentStatus.COMPLETED)) {
            throw new InvalidStatusTransitionException(
                String.format("Não é possível completar agendamento com status %s", status)
            );
        }

        String finalObservations = observations != null ? observations : this.observation;
        
        AppointmentStatus previousStatus = this.status;
        Appointment completedAppointment = new Appointment(
            this.id, this.patientId, this.professionalId, this.scheduledTime,
            AppointmentStatus.COMPLETED, this.type, finalObservations,
            this.createdAt, LocalDateTime.now()
        );

        completedAppointment.addDomainEvent(new AppointmentStatusChangedEvent(
            this.id, previousStatus, AppointmentStatus.COMPLETED,
            LocalDateTime.now().toInstant(java.time.ZoneOffset.UTC)
        ));

        return completedAppointment;
    }

    /**
     * Método de negócio: Verifica conflito temporal com outro agendamento
     */
    public boolean conflictsWith(Appointment other) {
        if (other == null || !this.professionalId.equals(other.professionalId)) {
            return false;
        }

        return this.scheduledTime.conflictsWith(other.scheduledTime);
    }

    /**
     * Método de negócio: Atualiza observações
     */
    public Appointment updateObservation(String newObservation) {
        String updatedObservation = newObservation != null ? newObservation : "";
        
        return new Appointment(
            this.id, this.patientId, this.professionalId, this.scheduledTime,
            this.status, this.type, updatedObservation,
            this.createdAt, LocalDateTime.now()
        );
    }

    /**
     * Método de negócio: Define ID após persistência
     */
    public Appointment withId(AppointmentId newId) {
        Objects.requireNonNull(newId, "AppointmentId não pode ser nulo");
        return new Appointment(
            newId, this.patientId, this.professionalId, this.scheduledTime,
            this.status, this.type, this.observation,
            this.createdAt, this.updatedAt
        );
    }

    /**
     * Verifica se o agendamento está ativo (pode ser atendido)
     */
    public boolean isActive() {
        return status.isActive();
    }

    /**
     * Verifica se o agendamento pode ser cancelado
     */
    public boolean isCancellable() {
        return status.isCancellable();
    }

    /**
     * Verifica se o agendamento pode ser remarcado
     */
    public boolean isReschedulable() {
        return status.isReschedulable();
    }

    /**
     * Verifica se pertence ao paciente especificado
     */
    public boolean belongsToPatient(PatientId patientId) {
        return this.patientId.equals(patientId);
    }

    /**
     * Verifica se pertence ao profissional especificado
     */
    public boolean belongsToProfessional(ProfessionalId professionalId) {
        return this.professionalId.equals(professionalId);
    }

    // Domain Events Management
    private void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    // Getters
    public AppointmentId getId() {
        return id;
    }

    public PatientId getPatientId() {
        return patientId;
    }

    public ProfessionalId getProfessionalId() {
        return professionalId;
    }

    public AppointmentTime getScheduledTime() {
        return scheduledTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public AppointmentType getType() {
        return type;
    }

    public String getObservation() {
        return observation;
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
        Appointment that = (Appointment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Appointment{id=%s, patient=%s, professional=%s, time=%s, status=%s}", 
                           id != null ? id.value() : "novo", 
                           patientId.value(), 
                           professionalId.value(),
                           scheduledTime.getFormattedDateTime(),
                           status);
    }

    // Exceções específicas do Appointment
    public static class InvalidAppointmentException extends DomainException {
        private static final String ERROR_CODE = "INVALID_APPOINTMENT";
        
        public InvalidAppointmentException(String message) {
            super(message);
        }

        @Override
        public String getErrorCode() {
            return ERROR_CODE;
        }
    }

    public static class InvalidStatusTransitionException extends DomainException {
        private static final String ERROR_CODE = "INVALID_STATUS_TRANSITION";
        
        public InvalidStatusTransitionException(String message) {
            super(message);
        }

        @Override
        public String getErrorCode() {
            return ERROR_CODE;
        }
    }
}
