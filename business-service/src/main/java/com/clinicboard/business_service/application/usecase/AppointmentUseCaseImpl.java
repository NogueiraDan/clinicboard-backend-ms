package com.clinicboard.business_service.application.usecase;

import com.clinicboard.business_service.common.error.CustomGenericException;
import com.clinicboard.business_service.application.dto.AppointmentRequestDto;
import com.clinicboard.business_service.application.dto.AppointmentResponseDto;
import com.clinicboard.business_service.application.mapper.DomainAppointmentMapper;
import com.clinicboard.business_service.application.port.inbound.AppointmentUseCase;
import com.clinicboard.business_service.domain.port.AppointmentRepositoryPort;
import com.clinicboard.business_service.application.port.outbound.EventPublisher;
import com.clinicboard.business_service.domain.model.enums.AppointmentType;
import com.clinicboard.business_service.domain.model.Appointment;
import com.clinicboard.business_service.domain.model.valueobjects.AppointmentTime;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;
import com.clinicboard.business_service.domain.service.AppointmentSchedulingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação dos casos de uso de agendamento
 */
@Service
@Transactional
public class AppointmentUseCaseImpl implements AppointmentUseCase {
    
    private final AppointmentRepositoryPort appointmentRepository;
    private final AppointmentSchedulingService schedulingService;
    private final EventPublisher eventPublisher;
    private final DomainAppointmentMapper appointmentMapper;
    
    public AppointmentUseCaseImpl(AppointmentRepositoryPort appointmentRepository,
                                 AppointmentSchedulingService schedulingService,
                                 EventPublisher eventPublisher,
                                 DomainAppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.schedulingService = schedulingService;
        this.eventPublisher = eventPublisher;
        this.appointmentMapper = appointmentMapper;
    }
    
    @Override
    public AppointmentResponseDto scheduleAppointment(AppointmentRequestDto request) {
        // Criar value objects
        AppointmentTime appointmentTime = new AppointmentTime(request.getDate());
        ProfessionalId professionalId = new ProfessionalId(request.getProfessionalId());
        
        // Validar regras de negócio através do domain service
        schedulingService.validateAppointmentScheduling(appointmentTime, professionalId, request.getPatientId());
        
        // Criar agregado
        Appointment appointment = new Appointment(
            appointmentTime,
            professionalId,
            request.getPatientId(),
            request.getObservation(),
            AppointmentType.MARCACAO
        );
        
        // Agendar (mudará status para SCHEDULED e disparará evento)
        appointment.schedule();
        
        // Salvar
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Publicar evento via messaging (compatibilidade com sistema atual)
        eventPublisher.publishAppointmentScheduled(request);
        
        return appointmentMapper.toDto(savedAppointment);
    }
    
    @Override
    public AppointmentResponseDto rescheduleAppointment(String appointmentId, AppointmentRequestDto request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new CustomGenericException("Consulta não encontrada"));
        
        AppointmentTime newAppointmentTime = new AppointmentTime(request.getDate());
        ProfessionalId professionalId = new ProfessionalId(request.getProfessionalId());
        
        // Validar novo horário
        schedulingService.validateAppointmentScheduling(newAppointmentTime, professionalId, request.getPatientId());
        
        // Reagendar usando comportamento do agregado
        appointment.reschedule(newAppointmentTime, "Reagendamento solicitado");
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        return appointmentMapper.toDto(savedAppointment);
    }
    
    @Override
    public void cancelAppointment(String appointmentId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new CustomGenericException("Consulta não encontrada para cancelamento"));
        
        // Cancelar usando comportamento do agregado
        appointment.cancel(reason);
        
        appointmentRepository.save(appointment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDto findAppointmentById(String appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new CustomGenericException("Consulta não encontrada"));
        
        return appointmentMapper.toDto(appointment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findAllAppointments() {
        return appointmentRepository.findAll().stream()
            .map(appointmentMapper::toDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findAppointmentsByProfessional(String professionalId) {
        return appointmentRepository.findAll().stream()
            .filter(appointment -> appointment.isManagedBy(new ProfessionalId(professionalId)))
            .map(appointmentMapper::toDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findAppointmentsByPatient(String patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
            .map(appointmentMapper::toDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findAppointmentsByDate(String professionalId, String date) {
        return appointmentRepository.findByProfessionalId(professionalId).stream()
            .map(appointmentMapper::toDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findAppointmentsByFilter(String id, String param, String value) {
        List<Appointment> appointments;
        String searchValue = "%" + value + "%";
        
        switch (param.toLowerCase()) {
            case "status":
                appointments = appointmentRepository.findByStatusAndProfessionalId(searchValue, id);
                break;
            default:
                throw new CustomGenericException("Parâmetro de busca inválido.");
        }
        
        return appointments.stream()
            .map(appointmentMapper::toDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LocalTime> getAvailableTimes(String professionalId, String date) {
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(21, 0);
        
        // Buscar agendamentos do dia
        List<Appointment> appointments = appointmentRepository.findByProfessionalId(professionalId);
        
        // Coletar horários ocupados
        Set<LocalTime> busyTimes = appointments.stream()
            .map(appointment -> appointment.getAppointmentTime().getValue().toLocalTime().withSecond(0).withNano(0))
            .collect(Collectors.toSet());
        
        // Gerar horários livres
        List<LocalTime> availableTimes = new ArrayList<>();
        LocalTime current = start;
        
        while (!current.isAfter(end)) {
            if (!busyTimes.contains(current)) {
                availableTimes.add(current);
            }
            current = current.plusMinutes(30);
        }
        
        return availableTimes;
    }
}
