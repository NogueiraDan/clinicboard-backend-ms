package com.clinicboard.business_service.application.port.outbound;

import com.clinicboard.business_service.domain.model.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Porta de saída para persistência de agendamentos
 */
public interface AppointmentRepository {
    
    Appointment save(Appointment appointment);
    
    Optional<Appointment> findById(String id);
    
    List<Appointment> findAll();
    
    void deleteById(String id);
    
    boolean existsById(String id);
    
    // Consultas específicas do domínio
    boolean existsByProfessionalAndTimeRange(String professionalId, LocalDateTime startRange, LocalDateTime endRange);
    
    boolean existsByTimeRange(LocalDateTime startRange, LocalDateTime endRange);
    
    boolean existsByPatientAndDate(String patientId, LocalDateTime date);
    
    List<Appointment> findByProfessionalAndDate(String professionalId, String date);
    
    List<Appointment> findByStatus(String professionalId, String status);
}
