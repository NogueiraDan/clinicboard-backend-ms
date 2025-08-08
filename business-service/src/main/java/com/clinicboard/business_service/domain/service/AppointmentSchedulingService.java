package com.clinicboard.business_service.domain.service;

import com.clinicboard.business_service.domain.port.AppointmentRepositoryPort;
import com.clinicboard.business_service.common.error.BusinessException;
import com.clinicboard.business_service.domain.model.valueobjects.AppointmentTime;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Serviço de domínio para validações de agendamento
 */
@Service
public class AppointmentSchedulingService {
    
    private final AppointmentRepositoryPort appointmentRepository;
    
    public AppointmentSchedulingService(AppointmentRepositoryPort appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }
    
    /**
     * Valida se um agendamento pode ser criado considerando todas as regras de negócio
     */
    public void validateAppointmentScheduling(AppointmentTime appointmentTime, 
                                            ProfessionalId professionalId, 
                                            String patientId) {
        LocalDateTime dateTime = appointmentTime.getDateTime();
        LocalDateTime startRange = dateTime.minusMinutes(30);
        LocalDateTime endRange = dateTime.plusMinutes(30);
        
        // Verifica se profissional está disponível
        if (appointmentRepository.existsByProfessionalIdAndDateRange(professionalId.getValue(), startRange, endRange)) {
            throw new BusinessException(
                "Profissional já possui um agendamento nesta data/hora ou horário está fora do intervalo permitido."
            );
        }
        
        // Verifica conflito de horário geral (se necessário)
        if (appointmentRepository.existsByDateRange(startRange, endRange)) {
            throw new BusinessException(
                "Já existe um agendamento marcado nesta data/hora ou horário está fora do intervalo permitido."
            );
        }
        
        // Verifica se paciente já tem agendamento no mesmo dia
        if (appointmentRepository.existsByPatientIdAndDate(dateTime, patientId)) {
            throw new BusinessException("Paciente já possui um agendamento nesta data!");
        }
    }
    
    /**
     * Verifica se um profissional está disponível em determinado horário
     */
    public boolean isProfessionalAvailable(ProfessionalId professionalId, AppointmentTime appointmentTime) {
        LocalDateTime dateTime = appointmentTime.getDateTime();
        LocalDateTime startRange = dateTime.minusMinutes(30);
        LocalDateTime endRange = dateTime.plusMinutes(30);
        
        return !appointmentRepository.existsByProfessionalIdAndDateRange(professionalId.getValue(), startRange, endRange);
    }
    
    /**
     * Verifica se um paciente já tem agendamento no dia
     */
    public boolean isPatientAvailable(String patientId, AppointmentTime appointmentTime) {
        return !appointmentRepository.existsByPatientIdAndDate(appointmentTime.getDateTime(), patientId);
    }
}
