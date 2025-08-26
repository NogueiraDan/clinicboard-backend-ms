package com.clinicboard.business_service.application.usecase;

import com.clinicboard.business_service.application.port.in.FindAppointmentQuery;
import com.clinicboard.business_service.application.port.out.AppointmentRepository;
import com.clinicboard.business_service.domain.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação dos casos de uso de consulta de agendamentos.
 * 
 * Responsável por todas as operações de leitura relacionadas
 * a agendamentos, seguindo o padrão CQRS.
 * 
 * Princípios DDD aplicados:
 * - Separação clara entre Commands e Queries (CQRS)
 * - Foco em consultas sem efeitos colaterais
 * - Uso de linguagem ubíqua
 */
public class FindAppointmentUseCaseImpl implements FindAppointmentQuery {

    private final AppointmentRepository appointmentRepository;

    public FindAppointmentUseCaseImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = Objects.requireNonNull(appointmentRepository, "AppointmentRepository cannot be null");
    }

    @Override
    public Optional<AppointmentView> findById(AppointmentId appointmentId) {
        Objects.requireNonNull(appointmentId, "AppointmentId cannot be null");
        
        return appointmentRepository.findById(appointmentId)
                .map(this::toAppointmentView);
    }

    @Override
    public List<AppointmentView> findByPatientId(PatientId patientId) {
        Objects.requireNonNull(patientId, "PatientId cannot be null");
        
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(this::toAppointmentView)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentView> findByProfessionalId(ProfessionalId professionalId) {
        Objects.requireNonNull(professionalId, "ProfessionalId cannot be null");
        
        return appointmentRepository.findByProfessionalId(professionalId)
                .stream()
                .map(this::toAppointmentView)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentView> findByDate(LocalDate date) {
        Objects.requireNonNull(date, "Date cannot be null");
        
        return appointmentRepository.findByDate(date)
                .stream()
                .map(this::toAppointmentView)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentView> findByDateRange(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "Start date cannot be null");
        Objects.requireNonNull(endDate, "End date cannot be null");
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        return appointmentRepository.findByDateRange(startDate, endDate)
                .stream()
                .map(this::toAppointmentView)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentView> findByStatus(AppointmentStatus status) {
        Objects.requireNonNull(status, "Status cannot be null");
        
        return appointmentRepository.findByStatus(status)
                .stream()
                .map(this::toAppointmentView)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isTimeSlotAvailable(ProfessionalId professionalId, AppointmentTime appointmentTime) {
        Objects.requireNonNull(professionalId, "ProfessionalId cannot be null");
        Objects.requireNonNull(appointmentTime, "AppointmentTime cannot be null");
        
        return !appointmentRepository.hasConflictingAppointment(professionalId, appointmentTime);
    }

    /**
     * Converte um agregado Appointment para AppointmentView (read model).
     */
    private AppointmentView toAppointmentView(Appointment appointment) {
        return AppointmentView.of(
                appointment.getId(),
                appointment.getPatientId(),
                "Patient-" + appointment.getPatientId().value(), // Nome temporário até integração com user-service
                appointment.getProfessionalId(),
                "Professional-" + appointment.getProfessionalId().value(), // Nome temporário
                appointment.getScheduledTime(),
                appointment.getType(),
                appointment.getStatus()
        );
    }
}
