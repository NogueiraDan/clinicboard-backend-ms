package com.clinicboard.business_service.application.port.outbound;

/**
 * Port para publicação de eventos assíncronos
 * Define o contrato que a camada de domínio/aplicação espera da infraestrutura
 */
public interface EventPublisherPort {
    
    /**
     * Publica evento de agendamento criado
     */
    void publishAppointmentScheduled(String appointmentId, String patientId, String professionalId);
    
    /**
     * Publica evento de agendamento confirmado
     */
    void publishAppointmentConfirmed(String appointmentId);
    
    /**
     * Publica evento de agendamento cancelado
     */
    void publishAppointmentCancelled(String appointmentId, String reason);
    
    /**
     * Publica evento de paciente registrado
     */
    void publishPatientRegistered(String patientId, String professionalId, String email);
}
