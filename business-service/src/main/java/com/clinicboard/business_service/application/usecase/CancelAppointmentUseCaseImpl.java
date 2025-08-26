package com.clinicboard.business_service.application.usecase;

import com.clinicboard.business_service.application.port.in.CancelAppointmentCommand;
import com.clinicboard.business_service.application.port.out.AppointmentRepository;
import com.clinicboard.business_service.application.port.out.EventPublisher;
import com.clinicboard.business_service.domain.model.Appointment;
import com.clinicboard.business_service.domain.event.AppointmentCancelledEvent;
import com.clinicboard.business_service.domain.exception.DomainException;

import java.util.Objects;

/**
 * Implementação do caso de uso de cancelamento de consultas.
 * 
 * Orquestra a lógica de negócio para cancelar um agendamento,
 * validando regras de negócio e publicando eventos de domínio.
 * 
 * Princípios DDD aplicados:
 * - Caso de uso na camada de aplicação
 * - Delegação da lógica de negócio para o agregado
 * - Publicação de eventos de domínio
 * - Validação de regras de negócio
 */
public class CancelAppointmentUseCaseImpl implements CancelAppointmentCommand {

    private final AppointmentRepository appointmentRepository;
    private final EventPublisher eventPublisher;

    public CancelAppointmentUseCaseImpl(
            AppointmentRepository appointmentRepository,
            EventPublisher eventPublisher) {
        this.appointmentRepository = Objects.requireNonNull(appointmentRepository, "AppointmentRepository cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "EventPublisher cannot be null");
    }

    @Override
    public CancelAppointmentResponse cancelAppointment(CancelAppointmentRequest request) {
        // Validar entrada
        Objects.requireNonNull(request, "CancelAppointmentRequest cannot be null");

        try {
            // 1. Buscar o agendamento
            Appointment appointment = appointmentRepository.findById(request.appointmentId())
                    .orElseThrow(() -> new DomainException("Appointment not found with ID: " + request.appointmentId().value()) {
                        @Override
                        public String getErrorCode() {
                            return "APPOINTMENT_NOT_FOUND";
                        }
                    });

            // 2. Delegar o cancelamento para o agregado (aplicação das regras de negócio)
            Appointment cancelledAppointment = appointment.cancel(request.cancellationReason());

            // 3. Persistir o agendamento cancelado
            Appointment savedAppointment = appointmentRepository.save(cancelledAppointment);

            // 4. Publicar eventos de domínio criados pelo agregado
            savedAppointment.getDomainEvents().forEach(event -> {
                if (event instanceof AppointmentCancelledEvent cancelledEvent) {
                    eventPublisher.publishAppointmentCancelled(cancelledEvent);
                }
            });

            // Limpar eventos após publicação
            savedAppointment.clearDomainEvents();

            // 5. Retornar resposta
            return CancelAppointmentResponse.success(
                    savedAppointment.getId(),
                    request.cancellationReason()
            );

        } catch (DomainException e) {
            // Re-lançar exceções de domínio
            throw e;
        } catch (Exception e) {
            // Encapsular outras exceções
            throw new DomainException("Failed to cancel appointment: " + e.getMessage(), e) {
                @Override
                public String getErrorCode() {
                    return "CANCEL_APPOINTMENT_FAILED";
                }
            };
        }
    }
}
