package com.clinicboard.business_service.application.port.inbound;

import com.clinicboard.business_service.application.dto.AppointmentRequestDto;
import com.clinicboard.business_service.application.dto.AppointmentResponseDto;

import java.time.LocalTime;
import java.util.List;

/**
 * Porta de entrada para operações de agendamento
 */
public interface AppointmentUseCase {
    
    AppointmentResponseDto scheduleAppointment(AppointmentRequestDto request);
    
    AppointmentResponseDto rescheduleAppointment(String appointmentId, AppointmentRequestDto request);
    
    void cancelAppointment(String appointmentId, String reason);
    
    AppointmentResponseDto findAppointmentById(String appointmentId);
    
    List<AppointmentResponseDto> findAllAppointments();
    
    List<AppointmentResponseDto> findAppointmentsByProfessional(String professionalId);
    
    List<AppointmentResponseDto> findAppointmentsByPatient(String patientId);
    
    List<AppointmentResponseDto> findAppointmentsByDate(String professionalId, String date);
    
    List<AppointmentResponseDto> findAppointmentsByFilter(String id, String param, String value);
    
    List<LocalTime> getAvailableTimes(String professionalId, String date);
}
