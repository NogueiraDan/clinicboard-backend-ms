package com.clinicboard.business_service.application.usecase;

import com.clinicboard.business_service.application.port.in.ScheduleAppointmentCommand;
import com.clinicboard.business_service.application.port.out.AppointmentRepository;
import com.clinicboard.business_service.application.port.out.PatientRepository;
import com.clinicboard.business_service.application.port.out.EventPublisherGateway;
import com.clinicboard.business_service.domain.model.Appointment;
import com.clinicboard.business_service.domain.model.Patient;
import com.clinicboard.business_service.domain.event.AppointmentScheduledEvent;
import com.clinicboard.business_service.domain.exception.DomainException;
import com.clinicboard.business_service.domain.exception.PatientBusinessRuleException;
import com.clinicboard.business_service.domain.exception.AppointmentConflictException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Implementação do caso de uso de agendamento de consultas.
 * 
 * Orquestra a lógica de negócio para criar um novo agendamento,
 * validando regras de negócio e publicando eventos de domínio.
 * 
 * Princípios DDD aplicados:
 * - Caso de uso na camada de aplicação
 * - Orquestração entre agregados
 * - Publicação de eventos de domínio
 * - Validação de regras de negócio
 */
@Component
public class ScheduleAppointmentUseCaseImpl implements ScheduleAppointmentCommand {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final EventPublisherGateway eventPublisher;

    public ScheduleAppointmentUseCaseImpl(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            EventPublisherGateway eventPublisher) {
        this.appointmentRepository = Objects.requireNonNull(appointmentRepository, "AppointmentRepository cannot be null");
        this.patientRepository = Objects.requireNonNull(patientRepository, "PatientRepository cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisherGateway cannot be null");
    }

    @Override
    public ScheduleAppointmentResponse scheduleAppointment(ScheduleAppointmentRequest request) {
        // Validar entrada
        Objects.requireNonNull(request, "ScheduleAppointmentRequest cannot be null");

        try {
            // 1. Verificar se o paciente existe
            Patient patient = patientRepository.findById(request.patientId())
                    .orElseThrow(() -> new PatientBusinessRuleException("Patient not found with ID: " + request.patientId().value()));

            // 2. Criar o agendamento usando o construtor do agregado
            Appointment appointment = new Appointment(
                    request.patientId(),
                    request.professionalId(),
                    request.appointmentTime(),
                    request.appointmentType()
            );

            // 3. Verificar conflitos de horário
            validateNoConflicts(appointment);

            // 4. Verificar se o paciente está ativo
            if (!patient.isActive()) {
                throw new PatientBusinessRuleException("Cannot schedule appointment for inactive patient");
            }

            // 5. Persistir o agendamento
            Appointment savedAppointment = appointmentRepository.save(appointment);

            // 6. Publicar evento de domínio - usar os eventos já criados pelo agregado
            savedAppointment.getDomainEvents().forEach(event -> {
                if (event instanceof AppointmentScheduledEvent scheduledEvent) {
                    eventPublisher.publishAppointmentScheduled(scheduledEvent);
                }
            });

            // Limpar eventos após publicação
            savedAppointment.clearDomainEvents();

            // 7. Retornar resposta
            return ScheduleAppointmentResponse.success(
                    savedAppointment.getId(),
                    savedAppointment.getPatientId(),
                    savedAppointment.getProfessionalId(),
                    savedAppointment.getScheduledTime(),
                    savedAppointment.getType(),
                    request.observations()
            );

        } catch (PatientBusinessRuleException | AppointmentConflictException e) {
            // Re-lançar exceções de domínio
            throw e;
        } catch (Exception e) {
            // Encapsular outras exceções
            throw new DomainException("Failed to schedule appointment: " + e.getMessage(), e) {
                @Override
                public String getErrorCode() {
                    return "SCHEDULE_APPOINTMENT_FAILED";
                }
            };
        }
    }

    /**
     * Valida se não há conflitos de horário para o agendamento.
     * 
     * @param appointment agendamento a ser validado
     * @throws AppointmentConflictException se houver conflito
     */
    private void validateNoConflicts(Appointment appointment) {
        boolean hasConflict = appointmentRepository
                .findByDateTimeRange(
                        appointment.getScheduledTime().value().minusMinutes(59),
                        appointment.getScheduledTime().value().plusMinutes(59)
                )
                .stream()
                .anyMatch(existing -> existing.conflictsWith(appointment));

        if (hasConflict) {
            throw new AppointmentConflictException(
                    "Time conflict detected for appointment at " + appointment.getScheduledTime().value()
            );
        }
    }
}
