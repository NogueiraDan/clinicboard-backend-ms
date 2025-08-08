package com.clinicboard.business_service.application.port.outbound;

import com.clinicboard.business_service.application.dto.AppointmentRequestDto;

/**
 * Porta de saída para publicação de eventos
 */
public interface EventPublisher {
    
    void publishAppointmentScheduled(AppointmentRequestDto appointment);
    
    void publishAppointmentCancelled(String appointmentId, String reason);
    
    void publishPatientRegistered(String patientId, String patientName, String professionalId);
}
